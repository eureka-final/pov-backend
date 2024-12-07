package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 좋아요 응답 DTO")
public record DailyMovieLikeDto(
        @Schema(description = "영화 ID", example = "1")
        Long movieId,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "좋아요 수", example = "100")
        Long likeAmount
) {
}
