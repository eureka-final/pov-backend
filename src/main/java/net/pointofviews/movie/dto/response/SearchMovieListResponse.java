package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;

import java.util.List;

@Schema(description = "Response containing a list of movies")
public record SearchMovieListResponse(
        @Schema(description = "영화 리스트")
        Slice<SearchMovieResponse> movies
) {
}