package net.pointofviews.premiere.service;

import net.pointofviews.fixture.PremiereFixture;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.exception.PremiereException;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.impl.PremiereMemberServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PremiereMemberServiceTest {

    @InjectMocks
    private PremiereMemberServiceImpl premiereMemberService;

    @Mock
    private PremiereRepository premiereRepository;

    @Nested
    class FindPremiereDetail {

        @Nested
        class Success {

            @Test
            void 시사회_정보_상세_조회() {
                // given -- 테스트의 상태 설정
                Premiere premiere = PremiereFixture.createPremiere();
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                // when -- 테스트하고자 하는 행동
                ReadDetailPremiereResponse result = premiereMemberService.findPremiereDetail(any());

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.title()).isEqualTo(premiere.getTitle());
                    softly.assertThat(result.startAt()).isEqualTo(premiere.getStartAt());
                    softly.assertThat(result.endAt()).isEqualTo(premiere.getEndAt());
                    softly.assertThat(result.price()).isEqualTo(premiere.getPrice());
                    softly.assertThat(result.isPaymentRequired()).isEqualTo(premiere.isPaymentRequired());
                    softly.assertThat(result.eventImage()).isEqualTo(premiere.getEventImage());
                    softly.assertThat(result.thumbnail()).isEqualTo(premiere.getThumbnail());
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_시사회_PremiereException_premiereNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                given(premiereRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                PremiereException exception = assertThrows(PremiereException.class, () -> premiereMemberService.findPremiereDetail(-1L));

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("시사회(Id: %d)가 존재하지 않습니다.", -1L));
                });
            }
        }
    }

}