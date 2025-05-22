package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

public record TeamUnionSliceYoungInfo(
        List<Long> projectsId, List<Long> studiesId, SliceNextInfo sliceNextInfo) {}
