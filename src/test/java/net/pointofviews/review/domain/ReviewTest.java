package net.pointofviews.review.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void Review_객체_생성() {
                // given
                Member member = mock(Member.class);
                String title = "리뷰 제목";
                String contents = "리뷰 내용";
                boolean isSpoiler = true;

                // when
                Review review = Review.builder()
                        .member(member)
                        .title(title)
                        .contents(contents)
                        .isSpoiler(isSpoiler)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(review).isNotNull();
                    softly.assertThat(review.getMember()).isEqualTo(member);
                    softly.assertThat(review.getTitle()).isEqualTo(title);
                    softly.assertThat(review.getContents()).isEqualTo(contents);
                    softly.assertThat(review.isSpoiler()).isEqualTo(isSpoiler);
                    softly.assertThat(review.isDisabled()).isFalse();
                    softly.assertThat(review.getModifiedAt()).isNull();
                });
            }
        }

        @Nested
        class failure {

            @Test
            void 제목_없음_IllegalArgumentException_예외발생() {
                // given
                Member member = mock(Member.class);
                String contents = "리뷰 내용";
                boolean isSpoiler = false;

                // when & then
                assertThatThrownBy(() -> Review.builder()
                        .member(member)
                        .contents(contents)
                        .isSpoiler(isSpoiler)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}