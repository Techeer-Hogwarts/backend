package backend.techeerzip.domain.projectTeam.type;

import lombok.Getter;

@Getter
public enum SortType {
    UPDATE_AT_DESC,
    VIEW_COUNT_DESC,
    LIKE_COUNT_DESC;

    public boolean isDate() {
        return this.equals(UPDATE_AT_DESC);
    }

    public boolean isCount() {
        return this.equals(VIEW_COUNT_DESC) || this.equals(LIKE_COUNT_DESC);
    }
}
