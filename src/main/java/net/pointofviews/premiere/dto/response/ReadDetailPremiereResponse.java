package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReadDetailPremiereResponse(

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "시사회 응모 시작 날짜", example = "2024-12-15T18:00:00", format = "date-time")
        LocalDateTime startAt,

        @Schema(description = "시사회 응모 종료 날짜", example = "2024-12-25T18:00:00", format = "date-time")
        LocalDateTime endAt,

        @Schema(description = "결제 필요 여부", example = "true")
        Boolean isPaymentRequired,

        @Schema(
                description = "시사회 이미지 URL",
                example = "https://example.com/images/premiere.jpg"
        )
        String eventImage
) {
}