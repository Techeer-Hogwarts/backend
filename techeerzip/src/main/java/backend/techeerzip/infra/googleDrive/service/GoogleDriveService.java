package backend.techeerzip.infra.googleDrive.service;

import backend.techeerzip.global.logger.CustomLogger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

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

    private void authorize() throws Exception {

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
                "application/pdf", // TODO: 유효성 검사 필요
                new ByteArrayInputStream(fileBuffer));

        // 파일 업로드
        File uploadFile = driveClient.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadFile.getWebViewLink() != null ? uploadFile.getWebViewLink() : "";
    }
}
