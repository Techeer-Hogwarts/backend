package backend.techeerzip.global.logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomLogger {

    // ANSI 색상 상수
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    // MDC 키 상수
    private static final String MDC_CONTEXT = "context";
    private static final String MDC_TRACE = "trace";

    // 기타 상수
    private static final String REDACTED = "[REDACTED]";
    private static final String LEVEL_ERROR = "error";
    private static final String LEVEL_WARN = "warn";
    private static final String LEVEL_INFO = "info";

    private static final List<String> SENSITIVE_FIELDS =
            List.of("password", "token", "secret", "authorization");

    private final Logger logger;
    private final String logLevel;

    public CustomLogger(@Value("${LOGGER_LEVEL:production}") String logLevel) {
        this.logger = LoggerFactory.getLogger(CustomLogger.class);
        this.logLevel = logLevel;
    }

    public void info(String message) {
        if (shouldLog(LEVEL_INFO) && logger.isInfoEnabled()) {
            logger.info(getColoredMessage("INFO", ANSI_CYAN, message));
        }
    }

    public void error(String message) {
        if (shouldLog(LEVEL_ERROR) && logger.isErrorEnabled()) {
            logger.error(getColoredMessage("ERROR", ANSI_RED, message));
        }
    }

    public void warn(String message) {
        if (shouldLog(LEVEL_WARN) && logger.isWarnEnabled()) {
            logger.warn(getColoredMessage("WARN", ANSI_YELLOW, message));
        }
    }

    public void debug(String message) {
        if (shouldLog("debug") && logger.isDebugEnabled()) {
            logger.debug(getColoredMessage("DEBUG", ANSI_BLUE, message));
        }
    }

    public void fatal(String message) {
        if (shouldLog("fatal") && logger.isErrorEnabled()) {
            logger.error(getColoredMessage("FATAL", ANSI_PURPLE, message));
        }
    }

    public void bodyError(
            Exception e,
            String errorCode,
            String errorMessage,
            int statusCode,
            HttpServletRequest request) {
        if (shouldLog(LEVEL_ERROR) && logger.isErrorEnabled()) {
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

    private boolean shouldLog(String level) {
        if ("production".equalsIgnoreCase(logLevel)) {
            return List.of(LEVEL_ERROR, LEVEL_WARN, LEVEL_INFO).contains(level);
        }
        return true; // development 환경에서는 모든 로그 허용
    }

    private String getColoredMessage(String level, String color, String message) {
        return String.format("%s[%s]%s %s", color, level, ANSI_RESET, message);
    }

    private String sanitizeRequestBody(HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining());
            return body.isEmpty() ? "{}" : sanitizeSensitiveData(body);
        } catch (IOException e) {
            return "{}";
        }
    }

    private String sanitizeSensitiveData(String body) {
        for (String field : SENSITIVE_FIELDS) {
            body =
                    body.replaceAll(
                            "(?i)\"" + field + "\":\"[^\"]*\"",
                            "\"" + field + "\":\"" + REDACTED + "\"");
        }
        return body;
    }

    public void log(String message, String context) {
        if (logger.isInfoEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            logger.info(message);
            MDC.remove(MDC_CONTEXT);
        }
    }

    public void error(String message, String trace, String context) {
        if (logger.isErrorEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            MDC.put(MDC_TRACE, trace);
            logger.error(message);
            MDC.remove(MDC_CONTEXT);
            MDC.remove(MDC_TRACE);
        }
    }

    public void warn(String message, String context) {
        if (logger.isWarnEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            logger.warn(message);
            MDC.remove(MDC_CONTEXT);
        }
    }

    public void debug(String message, String context) {
        if (logger.isDebugEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            logger.debug(message);
            MDC.remove(MDC_CONTEXT);
        }
    }

    public void verbose(String message, String context) {
        if (logger.isTraceEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            logger.trace(message);
            MDC.remove(MDC_CONTEXT);
        }
    }

    public void fatal(String message, String context) {
        if (logger.isErrorEnabled()) {
            MDC.put(MDC_CONTEXT, context);
            logger.error(message);
            MDC.remove(MDC_CONTEXT);
        }
    }

    private boolean isSensitiveField(String fieldName) {
        return SENSITIVE_FIELDS.stream().anyMatch(field -> fieldName.toLowerCase().contains(field));
    }

    public void error(String format, Object... args) {
        logger.debug(format, args);
    }
}
