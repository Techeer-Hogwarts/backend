package backend.techeerzip.domain.studyTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.mapper.TeamIndexMapper;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.dto.request.StudyData;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamUpdateResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public class StudyTeamMapper {

    private StudyTeamMapper() {
        // static 유틸 클래스
    }

    public static StudySliceTeamsResponse toGetAllResponse(StudyTeam studyTeam) {
        return StudySliceTeamsResponse.builder()
                .id(studyTeam.getId())
                .name(studyTeam.getName())
                .studyExplain(studyTeam.getStudyExplain())
                .isDeleted(studyTeam.getIsDeleted())
                .isFinished(studyTeam.getIsFinished())
                .isRecruited(studyTeam.getIsRecruited())
                .recruitNum(studyTeam.getRecruitNum())
                .viewCount(studyTeam.getViewCount())
                .likeCount(studyTeam.getLikeCount())
                .updatedAt(studyTeam.getUpdatedAt())
                .createdAt(studyTeam.getCreatedAt())
                .build();
    }

    public static StudyTeam toEntity(StudyData studyData, Boolean isRecruited) {
        return StudyTeam.builder()
                .name(studyData.getName())
                .goal(studyData.getGoal())
                .rule(studyData.getRule())
                .studyExplain(studyData.getStudyExplain())
                .githubLink(studyData.getGithubLink())
                .notionLink(studyData.getNotionLink())
                .recruitExplain(studyData.getRecruitExplain())
                .recruitNum(studyData.getRecruitNum())
                .isFinished(studyData.getIsFinished())
                .isRecruited(isRecruited)
                .build();
    }

    public static StudyTeamUpdateResponse toUpdatedResponse(
            Long studyTeamId, StudyTeam team, List<LeaderInfo> leaders) {
        return StudyTeamUpdateResponse.builder()
                .id(studyTeamId)
                .slackRequest(StudySlackMapper.toChannelRequest(team, leaders))
                .indexRequest(TeamIndexMapper.toStudyRequest(team))
                .build();
    }

    public static StudyTeamUpdateResponse toIndexOnlyUpdateResponse(
            Long studyTeamId, StudyTeam team) {
        return StudyTeamUpdateResponse.builder()
                .id(studyTeamId)
                .indexRequest(TeamIndexMapper.toStudyRequest(team))
                .build();
    }

    public static StudyTeamDetailResponse toDetailResponse(
            StudyTeam team, List<StudyMember> studyMembers) {
        return StudyTeamDetailResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .notionLink(team.getNotionLink())
                .githubLink(team.getGithubLink())
                .recruitExplain(team.getRecruitExplain())
                .recruitNum(team.getRecruitNum())
                .rule(team.getRule())
                .goal(team.getGoal())
                .studyExplain(team.getStudyExplain())
                .isRecruited(team.getIsRecruited())
                .isFinished(team.getIsFinished())
                .resultImages(
                        team.getResultImages().stream()
                                .map(StudyTeamMapper::mapToResultImageInfo)
                                .toList())
                .studyMember(
                        studyMembers.stream().map(StudyTeamMapper::mapToStudyMemberInfo).toList())
                .build();
    }

    private static StudyTeamDetailResponse.ResultImageInfo mapToResultImageInfo(
            StudyResultImage image) {
        return StudyTeamDetailResponse.ResultImageInfo.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }

    private static StudyTeamDetailResponse.StudyMemberInfo mapToStudyMemberInfo(
            StudyMember member) {
        return StudyTeamDetailResponse.StudyMemberInfo.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .name(member.getUser().getName())
                .profileImage(member.getUser().getProfileImage())
                .isLeader(member.isLeader())
                .build();
    }
}
