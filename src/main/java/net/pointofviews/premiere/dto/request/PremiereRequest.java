package net.pointofviews.premiere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PremiereRequest(

        @NotNull
        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @NotNull
        @Schema(description = "시사회 응모 시작 날짜", example = "2024-12-15T18:00:00", format = "date-time")
        LocalDateTime startAt,

        @NotNull
        @Schema(description = "시사회 응모 종료 날짜", example = "2024-12-25T18:00:00", format = "date-time")
        LocalDateTime endAt,

        @Schema(description = "응모 가격", example = "10000")
        int price,

        @Schema(description = "결제 필요 여부", example = "true")
        Boolean isPaymentRequired
) {
}