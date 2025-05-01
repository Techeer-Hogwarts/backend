package backend.techeerzip.infra.s3;

import static backend.techeerzip.infra.s3.ImageExtensionHandler.extractExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    public List<String> uploadMany(List<MultipartFile> files, String folderPath, String urlPrefix) {
        if (files.isEmpty()) {
            return List.of();
        }
        final List<CompletableFuture<String>> futures =
                IntStream.range(0, files.size())
                        .mapToObj(
                                i -> {
                                    final String ext =
                                            extractExtension(files.get(i).getOriginalFilename());
                                    final String fileName =
                                            urlPrefix
                                                    + "-"
                                                    + System.currentTimeMillis()
                                                    + "-"
                                                    + i
                                                    + ext;

                                    return uploadAsync(files.get(i), folderPath, fileName, ext);
                                })
                        .toList();

        return futures.stream().map(CompletableFuture::join).toList();
    }

    private CompletableFuture<String> uploadAsync(
            MultipartFile file, String folderPath, String fileName, String ext) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        final String keyPath = makeKeyPath(fileName, folderPath);
                        this.s3Client.putObject(
                                this.setPutObjectRequest(keyPath, ext), this.getFile(file));
                        return s3Client.utilities()
                                .getUrl(
                                        GetUrlRequest.builder()
                                                .bucket(bucketName)
                                                .key(keyPath)
                                                .build())
                                .toString();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                });
    }

    public void deleteMany(List<String> s3Urls) {
        List<CompletableFuture<Void>> futures = s3Urls.stream().map(this::deleteAsync).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
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
                        throw new IllegalArgumentException(e);
                    }
                });
    }
}
