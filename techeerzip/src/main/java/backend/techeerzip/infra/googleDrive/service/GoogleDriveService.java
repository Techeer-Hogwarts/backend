package backend.techeerzip.infra.googleDrive.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "TecheerZip";
    private static final String CONTEXT = "GoogleDriveService";
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

        Map<String, Object> credentialsMap = new HashMap<>();
        credentialsMap.put("type", type);
        credentialsMap.put("project_id", projectId);
        credentialsMap.put("private_key_id", privateKeyId);
        credentialsMap.put("private_key", privateKey);
        credentialsMap.put("client_email", clientEmail);
        credentialsMap.put("client_id", clientId);

        String jsonCredentials =
                new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(credentialsMap);

        // GoogleCredentials 생성
        GoogleCredentials credentials =
                GoogleCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes()))
                        .createScoped(Collections.singleton(DriveScopes.DRIVE));

        driveClient =
                new Drive.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(),
                                JacksonFactory.getDefaultInstance(),
                                new HttpCredentialsAdapter(credentials))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        logger.info("구글 드라이브 인증 초기화 완료: {} - {}", APPLICATION_NAME, CONTEXT);

    }

    public String uploadFileBuffer(byte[] fileBuffer, String fileName) throws IOException {
        if (folderId == null || folderId.isEmpty()) {
            logger.error("드라이브 업로드 실패 - 폴더 ID가 유효하지 않습니다.");
            throw new IllegalArgumentException("구글 드라이브 폴더 ID가 유효하지 않습니다.");
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));

        // 버퍼를 InputStreamContent로 변환
        InputStreamContent mediaContent =
                new InputStreamContent("application/pdf", new ByteArrayInputStream(fileBuffer));

        // 파일 업로드
        File uploadFile =
                driveClient
                        .files()
                        .create(fileMetadata, mediaContent)
                        .setFields("id, webViewLink")
                        .execute();

        return uploadFile.getWebViewLink() != null ? uploadFile.getWebViewLink() : "";
    }

    public void moveToFileArchive(String fileName) throws IOException {
        // 파일 ID 검색
        String fileQuery = String.format("name='%s' and '%s' in parents", fileName, folderId);
        FileList result =
                driveClient
                        .files()
                        .list()
                        .setQ(fileQuery)
                        .setSpaces("drive")
                        .setFields("files(id, name)")
                        .execute();

        List<File> files = result.getFiles();
        if (files.isEmpty()) {
            logger.error("드라이브 업로드 실패 - 파일 {}을(를) 찾을 수 없습니다.", fileName);
            throw new IllegalArgumentException("드라이브 업로드 실패 - 파일을 찾을 수 없습니다.");
        }

        String fileId = files.get(0).getId();

        // 파일 위치 아카이브로 이동
        driveClient
                .files()
                .update(fileId, null)
                .setAddParents(archiveFolderId)
                .setRemoveParents(folderId)
                .setFields("id, parents")
                .execute();

        logger.info("파일 [{}]를 아카이브 폴더 [{}]로 이동 완료", fileName, archiveFolderId);
    }
}
