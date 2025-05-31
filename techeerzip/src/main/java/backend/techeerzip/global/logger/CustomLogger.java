package backend.techeerzip.global.logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CustomLogger {

    // ANSI 색상 상수
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    // MDC 키 상수
    private static final String MDC_CONTEXT = "context";
    private static final String MDC_TRACE = "trace";

    // 민감 필드
    private static final List<String> SENSITIVE_FIELDS =
            List.of("password", "token", "secret", "authorization");

    // SLF4J Logger
    private final Logger logger = LoggerFactory.getLogger("backend.techeerzip");

    // -------------------------------------------------------------------------------
    // 기본 메시지 로깅 (색상 + SLF4J 플레이스홀더 지원)
    // -------------------------------------------------------------------------------

    public void info(String format, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(getColoredMessage("INFO", ANSI_CYAN, format), args);
        }
    }

    public void warn(String format, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(getColoredMessage("WARN", ANSI_YELLOW, format), args);
        }
    }

    public void error(String format, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(getColoredMessage("ERROR", ANSI_RED, format), args);
        }
    }

    public void debug(String format, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(getColoredMessage("DEBUG", ANSI_BLUE, format), args);
        }
    }

    public void fatal(String format, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(getColoredMessage("FATAL", ANSI_PURPLE, format), args);
        }
    }

    /** HTTP 요청 바디를 포함한 에러 로깅. 민감 데이터는 REDACTED 처리됩니다. */
    public void bodyError(
            Exception e,
            String errorCode,
            String errorMessage,
            int statusCode,
            HttpServletRequest request) {

        if (logger.isErrorEnabled()) {
            String logMessage =
                    String.format(
                            """
                            %s[ERROR] %s%s
                            %s* ERROR CODE:    %s%s
                            %s* ERROR MESSAGE: %s%s
                            %s* STATUS CODE:   %d%s
                            %s* PATH:          %s%s
                            %s* METHOD:        %s%s
                            %s* BODY:          %s%s
                            %s* STACK TRACE:   %s%s
                            %s━━━━━━━━━━━━━━━━
                            """,
                            ANSI_RED,
                            new Date(),
                            ANSI_RESET,
                            ANSI_RED,
                            errorCode,
                            ANSI_RESET,
                            ANSI_RED,
                            errorMessage,
                            ANSI_RESET,
                            ANSI_RED,
                            statusCode,
                            ANSI_RESET,
                            ANSI_RED,
                            request.getRequestURI(),
                            ANSI_RESET,
                            ANSI_RED,
                            request.getMethod(),
                            ANSI_RESET,
                            ANSI_RED,
                            sanitizeRequestBody(request),
                            ANSI_RESET,
                            ANSI_RED,
                            e.getStackTrace()[0],
                            ANSI_RESET,
                            ANSI_RED);
            logger.error(logMessage);
        }
    }

    // -------------------------------------------------------------------------------
    // MDC 활용 로그 (context, trace)
    // -------------------------------------------------------------------------------

    public void log(String message, String context) {
        if (logger.isInfoEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                logger.info(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
            }
        }
    }

    public void error(String message, String trace, String context) {
        if (logger.isErrorEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                MDC.put(MDC_TRACE, trace);
                logger.error(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
                MDC.remove(MDC_TRACE);
            }
        }
    }

    public void warn(String message, String context) {
        if (logger.isWarnEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                logger.warn(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
            }
        }
    }

    public void debug(String message, String context) {
        if (logger.isDebugEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                logger.debug(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
            }
        }
    }

    public void verbose(String message, String context) {
        if (logger.isTraceEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                logger.trace(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
            }
        }
    }

    public void fatal(String message, String context) {
        if (logger.isErrorEnabled()) {
            try {
                MDC.put(MDC_CONTEXT, context);
                logger.error(message);
            } finally {
                MDC.remove(MDC_CONTEXT);
            }
        }
    }

    // -------------------------------------------------------------------------------
    // 내부 헬퍼 메서드
    // -------------------------------------------------------------------------------

    /** 로그 메시지에 ANSI 컬러를 붙여서 반환 */
    private String getColoredMessage(String level, String color, String format) {
        return String.format("%s[%s]%s %s", color, level, ANSI_RESET, format);
    }

    /** HTTP 요청 바디를 읽어서 민감 필드를 마스킹 */
    private String sanitizeRequestBody(HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining());
            return body.isEmpty() ? "{}" : sanitizeSensitiveData(body);
        } catch (IOException e) {
            return "{}";
        }
    }

    /** 민감 필드를 REDACTED 처리 */
    private String sanitizeSensitiveData(String body) {
        for (String field : SENSITIVE_FIELDS) {
            body =
                    body.replaceAll(
                            "(?i)\"" + field + "\":\"[^\"]*\"", "\"" + field + "\":\"[REDACTED]\"");
        }
        return body;
    }
}