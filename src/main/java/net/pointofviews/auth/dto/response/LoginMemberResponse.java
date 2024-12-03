package net.pointofviews.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "로그인 응답 DTO")
public record LoginMemberResponse(
        @Schema(description = "회원 ID")
        UUID id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "닉네임", example = "nickname")
        String nickname,

        @Schema(description = "생년월일", example = "2000-01-01")
        LocalDate birth,

        @Schema(description = "관심 장르 목록 (최대 3개)", example = "[\"로맨스\", \"코미디\", \"액션\"]")
        List<String> favorGenres,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        String profileImage,

        @Schema(description = "회원 권한", example = "USER")
        String role
) {
}
