package net.pointofviews.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginMemberRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank @Email
        String email,

        @Schema(description = "소셜 로그인 타입", example = "GOOGLE")
        @NotBlank
        String socialType
) {
}