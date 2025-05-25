package backend.techeerzip.domain.projectTeam.repository.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

public class SortAssembler {
    private SortAssembler() {}

    public static <T extends Comparable<T>> OrderSpecifier<T> buildAsc(SortOption<T> option, PathBuilder<?> root) {
        return root.getComparable(option.getProperty(), option.getType()).asc();
    }
    public static <T extends Comparable<T>> OrderSpecifier<T> buildDesc(SortOption<T> option, PathBuilder<?> root) {
        return root.getComparable(option.getProperty(), option.getType()).desc();
    }

}
