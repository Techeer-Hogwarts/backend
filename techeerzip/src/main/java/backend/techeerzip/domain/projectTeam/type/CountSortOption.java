package backend.techeerzip.domain.projectTeam.type;

import static backend.techeerzip.domain.projectTeam.entity.QProjectTeam.projectTeam;
import static backend.techeerzip.domain.studyTeam.entity.QStudyTeam.studyTeam;

import java.util.Arrays;

import com.querydsl.core.types.OrderSpecifier;

import lombok.Getter;

@Getter
public enum CountSortOption {
    VIEW_COUNT_DESC_PROJECT(
            projectTeam.viewCount.desc(), SortType.VIEW_COUNT_DESC, TeamType.PROJECT),
    LIKE_COUNT_DESC_PROJECT(
            projectTeam.likeCount.desc(), SortType.LIKE_COUNT_DESC, TeamType.PROJECT),
    VIEW_COUNT_DESC_STUDY(studyTeam.viewCount.desc(), SortType.VIEW_COUNT_DESC, TeamType.STUDY),
    LIKE_COUNT_DESC_STUDY(studyTeam.likeCount.desc(), SortType.LIKE_COUNT_DESC, TeamType.STUDY);

    private final OrderSpecifier<Integer> order;
    private final SortType sortType;
    private final TeamType teamType;

    CountSortOption(OrderSpecifier<Integer> order, SortType sortType, TeamType teamType) {
        this.order = order;
        this.sortType = sortType;
        this.teamType = teamType;
    }

    public static OrderSpecifier<Integer> setOrder(String sort, TeamType teamType) {
        return Arrays.stream(values())
                .filter(s -> s.sortType.name().equals(sort) && s.teamType.equals(teamType))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getOrder();
    }
}
