package net.pointofviews.club.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReadPrivateClubDetailsResponse(
        @JsonIgnore
        String clubId,

        @Schema(description = "클럽 이름", example = "00즈")
        String clubName,

        @Schema(description = "클럽 설명", example = "영화를 좋아하는 00년생들")
        String clubDescription,

        @Schema(description = "클럽 대표 이미지", example = "https://example.com/image.jpg")
        String clubImage,

        @Schema(description = "클럽 선호 장르 해시태그", example = "[\"액션\", \"SF\"]")
        List<String> clubFavorGenres,

        @Schema(description = "참가자 수", example = "3")
        Long participant,

        @Schema(description = "최대 참가자 수", example = "100")
        Integer maxParticipants,

        @Schema(description = "클럽 공개 여부", example = "true")
        boolean isPublic,

        @Schema(description = "이 클럽의 북마크 수", example = "5")
        Long movieCount,

        @Schema(description = "로그인 사용자 해당 클럽 가입 여부", example = "true")
        boolean isMember
) {
    public static ReadPrivateClubDetailsResponse joinedMember(String clubId) {
        return new ReadPrivateClubDetailsResponse(clubId, null, null, null, null, null, null, false, null, true);
    }
}