package net.pointofviews.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "회원가입 요청 DTO")
public record CreateMemberRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank @Email
        String email,

        @Schema(description = "닉네임", example = "nickname")
        @NotBlank
        String nickname,

        @Schema(description = "생년월일", example = "2000-01-01")
        @NotNull
        LocalDate birth,

        @Schema(description = "소셜 로그인 타입", example = "GOOGLE")
        @NotBlank
        String socialType,

        @Schema(description = "관심 장르 목록 (최대 3개)", example = "[\"로맨스\", \"코미디\", \"액션\"]")
        @NotNull
        @Size(max = 3, message = "관심 장르는 최대 3개까지 선택 가능합니다")
        List<String> favorGenres,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        @NotNull
        String profileImage
) {
}
