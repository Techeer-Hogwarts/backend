package backend.techeerzip.infra.s3;

import static backend.techeerzip.infra.s3.ImageExtensionHandler.extractExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static String makeKeyPath(String fileName, String folderPath) {
        return fileName + "/" + folderPath;
    }

    private static String makeImageType(String ext) {
        return "image." + ext;
    }

    private static String extractKeyFromUrl(String s3Url) {
        try {
            return new URI(s3Url).getPath().substring(1);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String getFileName(String urlPrefix, String ext, Integer index) {
        return urlPrefix + "-" + System.currentTimeMillis() + "-" + index + ext;
    }

    private PutObjectRequest setPutObjectRequest(String keyPath, String ext) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyPath)
                .contentType(makeImageType(ext))
                .build();
    }

    private RequestBody getFile(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    public List<String> upload(MultipartFile file, String folderPath, String urlPrefix) {
        if (file == null) {
            return List.of();
        }
        final String ext = extractExtension(file.getOriginalFilename());
        final String fileName = getFileName(urlPrefix, ext, 0);
        final String keyPath = makeKeyPath(fileName, folderPath);
        try {
            this.s3Client.putObject(this.setPutObjectRequest(keyPath, ext), this.getFile(file));
            log.info("S3Service: upload 완료");
            return List.of(
                    s3Client.utilities()
                            .getUrl(GetUrlRequest.builder().bucket(bucketName).key(keyPath).build())
                            .toString());
        } catch (IOException e) {
            log.error(
                    "S3Service: Upload error - file: {}, folderPath: {}, urlPrefix: {}",
                    file,
                    folderPath,
                    urlPrefix,
                    e);
            throw new S3UploadFailException();
        }
    }

    public List<String> uploadMany(List<MultipartFile> files, String folderPath, String urlPrefix) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        final List<CompletableFuture<String>> uploadFuture = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            final String ext = extractExtension(files.get(i).getOriginalFilename());
            final String fileName = getFileName(urlPrefix, ext, i);
            uploadFuture.add(uploadAsync(files.get(i), folderPath, fileName, ext));
        }
        final List<String> uploaded = new ArrayList<>();
        try {
            uploadFuture.stream().map(CompletableFuture::join).forEach(uploaded::add);
            log.info("S3Service: uploadMany 완료");
            return List.copyOf(uploaded);
        } catch (CompletionException e) {
            deleteMany(uploaded);
            throw e;
        }
    }

    private CompletableFuture<String> uploadAsync(
            MultipartFile file, String folderPath, String fileName, String ext) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        final String keyPath = makeKeyPath(fileName, folderPath);
                        s3Client.putObject(setPutObjectRequest(keyPath, ext), this.getFile(file));
                        return s3Client.utilities()
                                .getUrl(
                                        GetUrlRequest.builder()
                                                .bucket(bucketName)
                                                .key(keyPath)
                                                .build())
                                .toString();
                    } catch (IOException e) {
                        log.error(
                                "S3Service: uploadAsync error - file: {}, folderPath: {}",
                                file,
                                folderPath,
                                e);
                        throw new S3UploadFailException();
                    }
                });
    }

    public void deleteMany(List<String> s3Urls) {
        List<CompletableFuture<Void>> futures = s3Urls.stream().map(this::deleteAsync).toList();
        CompletableFuture<Void> all =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            all.join(); // 여기서 내부 예외 발생 시 throw
        } catch (CompletionException e) {
            log.error("S3Service: deleteMany error - s3Urls: {}", s3Urls, e);
            throw new S3DeleteFailException(); // or custom exception
        }
    }

    private CompletableFuture<Void> deleteAsync(String url) {
        return CompletableFuture.runAsync(
                () -> {
                    try {
                        final String keyPath = extractKeyFromUrl(url);
                        final DeleteObjectRequest deleteObjectRequest =
                                DeleteObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(keyPath)
                                        .build();
                        s3Client.deleteObject(deleteObjectRequest);
                    } catch (Exception e) {
                        log.error("S3Service: deleteAsync error - s3Url: {}", url, e);
                    }
                });
    }
}
