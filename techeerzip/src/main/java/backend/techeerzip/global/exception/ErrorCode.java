package backend.techeerzip.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "Internal Server Error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "Invalid Type Value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "Access is Denied"),

    // Jwt
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 JWT 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "JWT 토큰이 만료 되었습니다."),
    AUTH_MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "JWT 토큰이 필요합니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "U002", "Email is Duplication"),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "U003", "Login input is invalid"),

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

    // ProjectTeam
    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "PT001", "Project team not found"),
    PROJECT_TEAM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PT002", "Project team already exists"),
    PROJECT_TEAM_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED, "PT003", "Unauthorized to access this project team"),

    // StudyMember
    STUDY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "SM001", "Study member not found"),
    STUDY_MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SM002", "Study member already exists"),
    STUDY_MEMBER_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED, "SM003", "Unauthorized to access this study member"),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND,"SS001", "해당 세션을 찾을 수 없습니다"),
    SESSION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SS002", "해당 세션이 이미 존재합니다."),
    SESSION_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED, "SS003", "해당 세션에 대한 권한이 없습니다."),

    // Stack
    STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "Stack not found"),
    STACK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "S002", "Stack already exists"),
    STACK_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "S003", "Unauthorized to access this stack"),

    // Redis
    REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "R001", "Redis connection error"),
    REDIS_MESSAGE_PROCESSING_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, "R002", "Redis message processing error"),
    REDIS_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "Redis task not found");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
