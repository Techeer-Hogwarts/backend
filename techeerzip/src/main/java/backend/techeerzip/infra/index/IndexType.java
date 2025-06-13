package backend.techeerzip.infra.index;

import lombok.Getter;

@Getter
public enum IndexType {
    USER("user"),
    RESUME("resume"),
    BLOG("blog"),
    SESSION("session"),
    PROJECT("project"),
    STUDY("study"),
    EVENT("event"),
    STACK("stack"),
    BOOTCAMP("bootcamp");

    private final String low;

    IndexType(String low) {
        this.low = low;
    }

}
