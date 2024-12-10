package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "시사회 정보 응답 DTO")
public record ReadPremiereResponse(
        @Schema(description = "시사회 ID", example = "123")
        Long premiereId,

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "시사회 썸네일 이미지", example = "https://example.com/premieres/1/thumbnail/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "시사회 응모 시작 일시", example = "2024-12-15T18:00:00", type = "date-time")
        LocalDateTime startAt
) {
}
