package backend.techeerzip.domain.studyTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public class StudyImageMapper {
    private StudyImageMapper() {}

    public static StudyResultImage toResultEntity(String url, StudyTeam team) {
        return StudyResultImage.builder().imageUrl(url).studyTeam(team).build();
    }

    public static List<StudyResultImage> toManyResultEntity(List<String> urls, StudyTeam team) {
        return urls.stream().map(url -> toResultEntity(url, team)).toList();
    }
}
