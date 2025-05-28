package backend.techeerzip.infra.googleDrive.service;

import backend.techeerzip.global.logger.CustomLogger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "TecheerZip";
    private final CustomLogger logger;

    @Value("${google.auth.type}")
    private String type;
    @Value("${google.auth.project-id}")
    private String projectId;
    @Value("${google.auth.private-key-id}")
    private String privateKeyId;
    @Value("${google.auth.private-key}")
    private String privateKey;
    @Value("${google.auth.client-email}")
    private String clientEmail;
    @Value("${google.auth.client-id}")
    private String clientId;
    @Value("${google.folder.id}")
    private String folderId;
    @Value("${google.archive.folder.id}")
    private String archiveFolderId;

    private Drive driveClient;

    @PostConstruct
    public void init() {
        try {
            initDriveClient();
        } catch (Exception e) {
            logger.error("구글 드라이브 인증 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("구글 드라이브 인증 초기화 실패", e);
        }
    }

    private void initDriveClient() throws Exception {

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance())
                .setServiceAccountId(clientEmail)
                .setServiceAccountScopes(java.util.Collections.singleton(com.google.api.services.drive.DriveScopes.DRIVE))
                .setServiceAccountPrivateKeyFromP12File(new java.io.File(privateKey))
                .build();

        driveClient = new Drive.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        logger.info("구글 드라이브 인증 초기화 완료: {}", APPLICATION_NAME);
    }

    public String uploadFileBuffer(byte[] fileBuffer, String fileName) throws IOException {
        if(folderId == null || folderId.isEmpty()) {
            throw new IllegalArgumentException("Google Drive folder ID is not set.");
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));

        // 버퍼를 InputStreamContent로 변환
        InputStreamContent mediaContent = new InputStreamContent(
                "application/pdf", // TODO: googleDrive에서는 파일 타입 지정하지 않고 이력서 도메인에서 타입 검사
                new ByteArrayInputStream(fileBuffer));

        // 파일 업로드
        File uploadFile = driveClient.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadFile.getWebViewLink() != null ? uploadFile.getWebViewLink() : "";
    }

    public void moveToFileArchive(String fileName) throws IOException {
        // 파일 ID 검색
        String fileQuery = String.format("name='%s' and '%s' in parents", fileName, folderId);
        FileList result = driveClient.files().list()
                .setQ(fileQuery)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        List<File> files = result.getFiles();
        if (files.isEmpty()) {
            logger.error("파일 {}을(를) 찾을 수 없습니다.", fileName);
            // 예외처리
            return;
        }

        String fileId = files.get(0).getId();

        // 파일 위치 아카이브로 이동
        driveClient.files().update(fileId, null)
                .setAddParents(archiveFolderId)
                .setRemoveParents(folderId)
                .setFields("id, parents")
                .execute();

        logger.info("파일 [{}]를 아카이브 폴더 [{}]로 이동 완료", fileName, archiveFolderId);
    }
}
