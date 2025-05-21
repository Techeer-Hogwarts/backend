package backend.techeerzip.domain.task.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskType {
    SIGNUP_BLOG_FETCH("signUp_blog_fetch"),
    DAILY_UPDATE("blogs_daily_update"),
    SHARED_POST_FETCH("shared_post_fetch");

    private final String value;

    public static TaskType fromValue(String value) {
        for (TaskType type : TaskType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown task type: " + value);
    }
}
