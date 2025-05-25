package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public record GetAllTeamsResponse(List<SliceTeamsResponse> teams, SliceNextInfo nextInfo) {

}
