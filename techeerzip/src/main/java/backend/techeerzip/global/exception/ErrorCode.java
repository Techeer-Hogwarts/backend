package backend.techeerzip.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "Internal Server Error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "Invalid Type Value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "Access is Denied"),

    // Auth
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 JWT 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "JWT 토큰이 만료 되었습니다."),
    AUTH_MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "JWT 토큰이 필요합니다."),
    AUTH_EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A005", "이메일 인증 코드 전송이 실패했습니다."),
    AUTH_INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "A006", "잘못된 이메일 인증 코드입니다."),
    AUTH_NOT_VERIFIED_EMAIL(HttpStatus.UNAUTHORIZED, "A007", "이메일 인증이 완료되지 않았습니다."),
    AUTH_NOT_TECHEER(HttpStatus.BAD_REQUEST, "A008", "테커가 아닌 사용자입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "이미 가입한 이메일입니다."),
    USER_NOT_RESUME(HttpStatus.BAD_REQUEST, "U004", "이력서 파일이 없습니다."),
    USER_UNAUTHORIZED_ADMIN(HttpStatus.FORBIDDEN, "U005", "권한이 없는 사용자입니다."),
    USER_PROFILE_IMG_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "U006", "프로필 이미지를 가져오지 못했습니다."),

    // UserExperience
    USER_EXPERIENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "UE001", "해당 경력을 찾을 수 없습니다."),

    // Blog
    BLOG_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "블로그를 찾을 수 없습니다."),
    BLOG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "B002", "이미 존재하는 블로그입니다."),
    BLOG_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "B003", "블로그에 접근할 권한이 없습니다."),
    BLOG_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "B004", "블로그 요청이 유효하지 않습니다."),
    BLOG_CRAWLING_ERROR(HttpStatus.BAD_REQUEST, "B005", "블로그 크롤링 중 오류가 발생했습니다."),
    BLOG_INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "B006", "잘못된 날짜 형식입니다."),
    BLOG_EMPTY_DATE(HttpStatus.BAD_REQUEST, "B007", "날짜가 비어있습니다."),
    BLOG_EMPTY_POSTS(HttpStatus.BAD_REQUEST, "B008", "크롤링 포스트 목록이 비어있습니다."),
    BLOG_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "B009", "블로그 작성자를 찾을 수 없습니다."),

    // Bookmark
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BM001", "Bookmark not found"),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "BM002", "Bookmark already exists"),
    BOOKMARK_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "BM003", "Unauthorized to access this bookmark"),

    // ==== ProjectMember ====
    PROJECT_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "PM001", "존재하지 않는 프로젝트 멤버입니다."),
    PROJECT_MEMBER_INVALID_TEAM_ROLE(HttpStatus.BAD_REQUEST, "PM002", "프로젝트 멤버의 팀 역할이 유효하지 않습니다."),
    PROJECT_MEMBER_INVALID_ACTIVE_REQUESTER(
            HttpStatus.BAD_REQUEST, "PM003", "프로젝트 멤버만 접근할 수 있습니다."),
    PROJECT_MEMBER_APPLICATION_EXISTS(HttpStatus.BAD_REQUEST, "PM004", "이미 해당 프로젝트에 지원하셨습니다."),
    PROJECT_MEMBER_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "PM005", "이미 해당 프로젝트에서 활동 중인 멤버입니다."),
    PROJECT_MEMBER_NOT_APPLICANT(HttpStatus.BAD_REQUEST, "PM006", "해당 프로젝트 지원자가 아닙니다."),

    // ==== ProjectTeam ====
    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "PT001", "프로젝트 팀을 찾을 수 없습니다."),
    PROJECT_TEAM_MAIN_IMAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "PT002", "메인 이미지를 확인해주세요."),
    PROJECT_TEAM_MISSING_LEADER(HttpStatus.BAD_REQUEST, "PT003", "프로젝트 팀 리더가 존재하지 않습니다."),
    PROJECT_TEAM_MISSING_MAIN_IMAGE(HttpStatus.BAD_REQUEST, "PT004", "메인 이미지가 존재하지 않습니다."),
    PROJECT_TEAM_DUPLICATE_TEAM_NAME(HttpStatus.CONFLICT, "PT005", "존재하는 프로젝트 이름입니다."),
    PROJECT_TEAM_DUPLICATE_DELETE_UPDATE(
            HttpStatus.CONFLICT, "PT006", "프로젝트 삭제 멤버와 업데이트 멤버가 중복됩니다."),
    PROJECT_TEAM_INVALID_TEAM_STACK(HttpStatus.BAD_REQUEST, "PT007", "팀 스택이 유효하지 않습니다."),
    PROJECT_TEAM_POSITION_CLOSED(HttpStatus.BAD_REQUEST, "PT008", "모집하는 포지션이 아닙니다."),
    PROJECT_TEAM_MISSING_UPDATE_MEMBER(
            HttpStatus.BAD_REQUEST, "PT009", "프로젝트 팀 멤버 업데이트에 누락된 인원이 존재합니다."),
    PROJECT_TEAM_RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "PT010", "프로젝트 팀 모집이 종료되었습니다."),
    PROJECT_TEAM_INVALID_TEAM_ROLE(HttpStatus.BAD_REQUEST, "PT011", "유효하지 않은 팀 역할입니다."),
    PROJECT_TEAM_INVALID_UPDATE_MEMBER(HttpStatus.BAD_REQUEST, "PT012", "업데이트 멤버가 유효하지 않습니다."),
    PROJECT_TEAM_INVALID_APPLICANT(HttpStatus.BAD_REQUEST, "PT013", "유효하지 않은 지원자입니다."),
    PROJECT_TEAM_EXCEEDED_RESULT_IMAGE(HttpStatus.BAD_REQUEST, "PT014", "결과 이미지는 10개까지만 등록 가능합니다."),
    PROJECT_TEAM_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "PT015", "이미 승인된 프로젝트 멤버입니다."),
    PROJECT_TEAM_INVALID_DELETE_IMAGE(HttpStatus.BAD_REQUEST, "PT016", "삭제하는 결과이미지가 유효하지 않습니다."),
    PROJECT_TEAM_INVALID_PROJECT_MEMBER(HttpStatus.BAD_REQUEST, "PT017", "프로젝트 멤버가 유효하지 않습니다."),

    // ==== StudyMember ====
    STUDY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "SM001", "스터디 멤버를 찾을 수 없습니다."),
    STUDY_MEMBER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "SM002", "유효하지 않은 요청입니다."),
    STUDY_MEMBER_IS_ACTIVE_MEMBER(HttpStatus.CONFLICT, "SM003", "이미 활동 중인 스터디 멤버입니다."),
    STUDY_MEMBER_INVALID_TEAM_MEMBER(HttpStatus.BAD_REQUEST, "SM004", "유효하지 않은 스터디 멤버입니다."),

    // ==== StudyTeam ====
    STUDY_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "ST001", "요청한 스터디 팀을 찾을 수 없습니다."),
    STUDY_TEAM_BAD_REQUEST(HttpStatus.BAD_REQUEST, "ST002", "유효하지 않은 요청입니다."),
    STUDY_TEAM_DUPLICATE_TEAM_NAME(HttpStatus.CONFLICT, "ST003", "존재하는 스터디 이름입니다."),
    STUDY_TEAM_INVALID_RECRUIT_NUM(HttpStatus.BAD_REQUEST, "ST004", "모집 인원이 음수 입니다."),
    STUDY_TEAM_MISSING_LEADER(HttpStatus.BAD_REQUEST, "ST005", "스터디 팀 리더가 존재하지 않습니다."),
    STUDY_TEAM_INVALID_UPDATE_MEMBER(HttpStatus.BAD_REQUEST, "ST006", "스터디 업데이트 멤버가 유효하지 않습니다."),
    STUDY_TEAM_ALREADY_ACTIVE_MEMBER(HttpStatus.BAD_REQUEST, "ST007", "이미 활동중인 스터디 멤버입니다."),
    STUDY_TEAM_ALREADY_REJECT_MEMBER(HttpStatus.BAD_REQUEST, "ST008", "이미 거절된 스터디 팀 지원자 입니다."),
    STUDY_TEAM_INVALID_APPLICANT(HttpStatus.BAD_REQUEST, "ST009", "유효한 스터디 팀 지원자가 아닙니다."),
    STUDY_TEAM_INVALID_USER(HttpStatus.BAD_REQUEST, "ST010", "유효한 사용자가 아닙니다."),
    STUDY_TEAM_NOT_ACTIVE_MEMBER(HttpStatus.BAD_REQUEST, "ST011", "스터디 팀 활동 중인 멤버가 아닙니다."),
    STUDY_TEAM_DUPLICATE_DELETE_UPDATE(
            HttpStatus.BAD_REQUEST, "ST012", "스터디 삭제 멤버와 업데이트 멤버가 중복됩니다."),
    STUDY_TEAM_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "ST013", "이미 지원한 팀입니다."),
    STUDY_TEAM_CLOSED_RECRUIT(HttpStatus.BAD_REQUEST, "ST014", "모집이 종료된 스터디입니다."),
    // Stack
    STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "Stack not found"),
    STACK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "S002", "Stack already exists"),
    STACK_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "S003", "Unauthorized to access this stack"),

    // Redis
    REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "R001", "Redis connection error"),
    REDIS_MESSAGE_PROCESSING_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, "R002", "Redis message processing error"),
    REDIS_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "Redis task not found"),

    // Role
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "해당 권한을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
