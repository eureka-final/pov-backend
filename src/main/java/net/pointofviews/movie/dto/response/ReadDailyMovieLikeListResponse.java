package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.movie.dto.DailyMovieLikeDto;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "일일 좋아요가 가장 많은 상위 10개 영화 응답 DTO")
public record ReadDailyMovieLikeListResponse(

        @Schema(description = "어제 기준 일자", example = "2024-12-05T00:00:00")
        LocalDateTime date,

        @Schema(description = "상위 10개 영화 목록")
        List<DailyMovieLikeDto> movies
) {
}
