package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "시사회 정보 목록 DTO")
public record ReadPremiereListResponse(
        @Schema(description = "시사회 목록")
        Page<ReadPremiereResponse> premieres
) {
}
