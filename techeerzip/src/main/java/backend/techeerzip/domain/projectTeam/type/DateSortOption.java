package backend.techeerzip.domain.projectTeam.type;

import static backend.techeerzip.domain.projectTeam.entity.QProjectTeam.projectTeam;
import static backend.techeerzip.domain.studyTeam.entity.QStudyTeam.studyTeam;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.querydsl.core.types.OrderSpecifier;

import lombok.Getter;

@Getter
public enum DateSortOption {
    UPDATE_AT_DESC_PROJECT(projectTeam.updatedAt.desc(), SortType.UPDATE_AT_DESC, TeamType.PROJECT),
    UPDATE_AT_DESC_STUDY(studyTeam.updatedAt.desc(), SortType.UPDATE_AT_DESC, TeamType.STUDY);

    private final OrderSpecifier<LocalDateTime> order;
    private final SortType sortType;
    private final TeamType teamType;

    DateSortOption(OrderSpecifier<LocalDateTime> order, SortType sortType, TeamType teamType) {
        this.order = order;
        this.sortType = sortType;
        this.teamType = teamType;
    }

    public static OrderSpecifier<LocalDateTime> setOrder(SortType sortType, TeamType teamType) {
        return Arrays.stream(values())
                .filter(s -> s.sortType.equals(sortType) && s.teamType.equals(teamType))
                .findFirst()
                .orElseThrow()
                .getOrder();
    }

    //    public OrderSpecifier<LocalDateTime> getOrder() {
    //        return order;
    //    }

}
