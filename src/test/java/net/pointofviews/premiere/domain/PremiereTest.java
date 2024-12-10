package net.pointofviews.premiere.domain;

import net.pointofviews.fixture.PremiereFixture;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PremiereTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Premiere_객체_생성() {
                // given
                String title = "premiere title";
                String thumbnail = "https://example.com/premieres/1/thumbnail/new-thumbnail.jpg";
                String eventImage = "https://example.com/premieres/1/event/new-eventImage.jpg";
                int price = 10000;
                boolean isPaymentRequired = true;
                LocalDateTime startAt = LocalDateTime.of(2024, 11, 22, 14, 30);
                LocalDateTime endAt = LocalDateTime.of(2024, 11, 28, 14, 30);

                // when
                Premiere premiere = Premiere.builder()
                        .title(title)
                        .thumbnail(thumbnail)
                        .eventImage(eventImage)
                        .price(price)
                        .isPaymentRequired(isPaymentRequired)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(premiere).isNotNull();
                    softly.assertThat(premiere.getTitle()).isEqualTo(title);
                    softly.assertThat(premiere.getThumbnail()).isEqualTo(thumbnail);
                    softly.assertThat(premiere.getEventImage()).isEqualTo(eventImage);
                    softly.assertThat(premiere.getPrice()).isEqualTo(price);
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(isPaymentRequired);
                    softly.assertThat(premiere.getStartAt()).isEqualTo(startAt);
                    softly.assertThat(premiere.getEndAt()).isEqualTo(endAt);
                });
            }
        }
    }

    @Nested
    class UpdatePremiere {

        @Nested
        class Success {

            @Test
            void 시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                String eventImage = "https://example.com/premieres/1/event/new-eventImage.jpg";
                String thumbnail = "https://example.com/premieres/1/thumbnail/new-thumbnail.jpg";

                LocalDateTime startAt = LocalDateTime.of(2024, 11, 22, 14, 30);
                LocalDateTime endAt = LocalDateTime.of(2024, 11, 28, 14, 30);

                Premiere premiere = Premiere.builder()
                        .title("title")
                        .thumbnail(thumbnail)
                        .eventImage(eventImage)
                        .price(10000)
                        .isPaymentRequired(true)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build();

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        20000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiere.updatePremiere(request);

                // then -- 예상되는 변화 및 결과
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(premiere).isNotNull();
                    softly.assertThat(premiere.getTitle()).isEqualTo(request.title());
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(request.isPaymentRequired());
                    softly.assertThat(premiere.getStartAt()).isEqualTo(request.startAt());
                    softly.assertThat(premiere.getEndAt()).isEqualTo(request.endAt());
                });
            }

            @Test
            void 시사회_이벤트_이미지_수정() {
                // given -- 테스트의 상태 설정
                Premiere premiere = PremiereFixture.createPremiere();
                String eventImage = "https://example.com/premieres/1/event/new-eventImage.jpg";

                // when -- 테스트하고자 하는 행동
                premiere.updateEventImage(eventImage);

                // then -- 예상되는 변화 및 결과
                assertThat(premiere.getEventImage()).isEqualTo(eventImage);
            }

            @Test
            void 시사회_썸네일_이미지_수정() {
                // given -- 테스트의 상태 설정
                Premiere premiere = PremiereFixture.createPremiere();
                String thumbnail = "https://example.com/premieres/1/thumbnail/new-thumbnail.jpg";

                // when -- 테스트하고자 하는 행동
                premiere.updateThumbnail(thumbnail);

                // then -- 예상되는 변화 및 결과
                assertThat(premiere.getThumbnail()).isEqualTo(thumbnail);
            }

        }
    }
}