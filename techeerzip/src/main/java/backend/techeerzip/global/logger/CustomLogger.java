package backend.techeerzip.global.logger;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomLogger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    private static final List<String> SENSITIVE_FIELDS =
            Arrays.asList("password", "token", "secret", "authorization");
    private final Logger logger;
    private final String logLevel;

    public CustomLogger(@Value("${LOGGER_LEVEL:production}") String logLevel) {
        this.logger = LoggerFactory.getLogger(CustomLogger.class);
        this.logLevel = logLevel;
    }

    public void info(String message) {
        if (shouldLog("info")) {
            logger.info(getColoredMessage("INFO", ANSI_CYAN, message));
        }
    }

    public void error(String message) {
        if (shouldLog("error")) {
            logger.error(getColoredMessage("ERROR", ANSI_RED, message));
        }
    }

    public void warn(String message) {
        if (shouldLog("warn")) {
            logger.warn(getColoredMessage("WARN", ANSI_YELLOW, message));
        }
    }

    public void debug(String message) {
        if (shouldLog("debug")) {
            logger.debug(getColoredMessage("DEBUG", ANSI_BLUE, message));
        }
    }

    public void fatal(String message) {
        if (shouldLog("fatal")) {
            logger.error(getColoredMessage("FATAL", ANSI_PURPLE, message));
        }
    }

    public void bodyError(
            Exception e,
            String errorCode,
            String errorMessage,
            int statusCode,
            HttpServletRequest request) {
        if (shouldLog("error")) {
            String logMessage =
                    String.format(
                            "\n%s[ERROR] %s%s\n"
                                    + "%s* ERROR CODE:    %s%s\n"
                                    + "%s* ERROR MESSAGE: %s%s\n"
                                    + "%s* STATUS CODE:   %d%s\n"
                                    + "%s* PATH:          %s%s\n"
                                    + "%s* METHOD:        %s%s\n"
                                    + "%s* BODY:          %s%s\n"
                                    + "%s* STACK TRACE:   %s%s\n"
                                    + "%s━━━━━━━━━━━━━━━━",
                            ANSI_RED,
                            new java.util.Date().toString(),
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
        if ("production".equals(logLevel)) {
            return Arrays.asList("error", "warn", "info").contains(level);
        }
        return true; // development 환경에서는 모든 레벨의 로그 출력
    }

    private String getColoredMessage(String level, String color, String message) {
        return String.format("%s[%s]%s %s", color, level, ANSI_RESET, message);
    }

    private String sanitizeRequestBody(HttpServletRequest request) {
        try {
            String body =
                    request.getReader().lines().collect(java.util.stream.Collectors.joining());
            if (body.isEmpty()) {
                return "{}";
            }
            return sanitizeSensitiveData(body);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String sanitizeSensitiveData(String body) {
        List<String> sensitiveFields =
                Arrays.asList("password", "token", "secret", "authorization");
        for (String field : sensitiveFields) {
            body =
                    body.replaceAll(
                            "(?i)\"" + field + "\":\"[^\"]*\"", "\"" + field + "\":\"[REDACTED]\"");
        }
        return body;
    }

    public void log(String message, String context) {
        MDC.put("context", context);
        logger.info(message);
        MDC.remove("context");
    }

    public void error(String message, String trace, String context) {
        MDC.put("context", context);
        MDC.put("trace", trace);
        logger.error(message);
        MDC.remove("context");
        MDC.remove("trace");
    }

    public void warn(String message, String context) {
        MDC.put("context", context);
        logger.warn(message);
        MDC.remove("context");
    }

    public void debug(String message, String context) {
        MDC.put("context", context);
        logger.debug(message);
        MDC.remove("context");
    }

    public void verbose(String message, String context) {
        MDC.put("context", context);
        logger.trace(message);
        MDC.remove("context");
    }

    public void fatal(String message, String context) {
        MDC.put("context", context);
        logger.error(message);
        MDC.remove("context");
    }

    private boolean isSensitiveField(String fieldName) {
        return SENSITIVE_FIELDS.stream().anyMatch(field -> fieldName.toLowerCase().contains(field));
    }

    public void error(String format, Object... args) {
        logger.debug(format, args);
    }
}
