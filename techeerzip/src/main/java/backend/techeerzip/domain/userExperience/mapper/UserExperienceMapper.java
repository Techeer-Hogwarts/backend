package backend.techeerzip.domain.userExperience.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.userExperience.entity.UserExperience;

@Component
public class UserExperienceMapper {

    private UserExperienceMapper() {}

    public GetUserResponse.ExperienceDTO toDto(UserExperience exp) {
        return GetUserResponse.ExperienceDTO.builder()
                .id(exp.getId())
                .position(exp.getPosition())
                .companyName(exp.getCompanyName())
                .startDate(exp.getStartDate().toString())
                .endDate(exp.getEndDate() != null ? exp.getEndDate().toString() : null)
                .category(exp.getCategory())
                .isFinished(exp.isFinished())
                .description(exp.getDescription())
                .build();
    }

    public List<GetUserResponse.ExperienceDTO> toDtoList(List<UserExperience> experiences) {
        return experiences.stream().map(this::toDto).collect(Collectors.toList());
    }
}
