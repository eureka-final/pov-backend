package net.pointofviews.curation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.pointofviews.curation.domain.CurationCategory;

@Schema(description = "큐레이션 생성 요청 DTO")
public record CreateCurationRequest(
        @NotBlank(message = "테마는 필수 입력 항목입니다.")
        @Schema(description = "큐레이션 주제", example = "Top Action Movies")
        String theme,

        @NotNull(message = "카테고리는 필수 입력 항목입니다.")
        @Schema(description = "큐레이션 카테고리", example = "GENRE", allowableValues = {"ACTOR", "GENRE", "DIRECTOR", "AWARD", "RELEASE", "OTHER", "COUNTRY"})
        CurationCategory category,

        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Schema(description = "큐레이션 제목", example = "Best Action Movies of the Year")
        String title,

        @Schema(description = "큐레이션 내용", example = "관리자가 뽑은 이달의 최고의 액션영화를 소개합니다.")
        String description,

        @Schema(description = "큐레이션 시작 시간 (ISO 8601 형식)", example = "2024-11-22T10:00:00Z")
        String startTime
) {}