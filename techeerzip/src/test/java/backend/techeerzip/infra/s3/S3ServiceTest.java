// package backend.techeerzip.infra.s3;
//
// import java.io.IOException;
// import java.util.List;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;
//
// @SpringBootTest
// class S3ServiceTest {
//
//    @Autowired
//    private S3Service s3Service;
//
//    @Test
//    void uploadMany() throws IOException {
//        final ClassPathResource image = new ClassPathResource("IMG_2326.jpg");
//
//        final MultipartFile multipartFile = new MockMultipartFile(
//                "file",
//                "test-image.jpg",
//                "image/jpeg",
//                image.getInputStream()
//        );
//
//        final List<String> urls = s3Service.uploadMany(
//                List.of(multipartFile, multipartFile, multipartFile),
//                "project-teams/main",
//                "project-team"
//        );
//        for (String url : urls) {
//            System.out.println(url);
//        }
//    }
//
//    @Test
//    void deleteAsync() {
//        final List<String> urls = List.of(
//
// "https://techeerzip-bucket.s3.ap-southeast-2.amazonaws.com/project-team-1746115357899-0jpg/project-teams/main",
//
// "https://techeerzip-bucket.s3.ap-southeast-2.amazonaws.com/project-team-1746115588474-0jpg/project-teams/main");
//        s3Service.deleteMany(urls);
//    }
// }
