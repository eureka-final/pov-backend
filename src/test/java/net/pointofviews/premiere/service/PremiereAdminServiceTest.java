package net.pointofviews.premiere.service;

import net.pointofviews.common.service.S3Service;
import net.pointofviews.fixture.PremiereFixture;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.exception.PremiereException;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.impl.PremiereAdminServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiereAdminServiceTest {

    @InjectMocks
    private PremiereAdminServiceImpl premiereAdminService;

    @Mock
    private PremiereRepository premiereRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @Nested
    class UpdatePremiere {

        @Nested
        class Success {

            @Test
            void 요청_이미지_썸네일과_이벤트_모두_포함된_시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                MockMultipartFile thumbnail = new MockMultipartFile(
                        "thumbnail",
                        "thumbnail.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "thumbnail".getBytes()
                );

                MockMultipartFile eventImage = new MockMultipartFile(
                        "eventImage",
                        "eventImage.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "eventImage".getBytes()
                );

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        10000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiereAdminService.updatePremiere(admin, 1L, request, thumbnail, eventImage);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(premiere.getTitle()).isEqualTo(request.title());
                    softly.assertThat(premiere.getStartAt()).isEqualTo(request.startAt());
                    softly.assertThat(premiere.getEndAt()).isEqualTo(request.endAt());
                    softly.assertThat(premiere.getPrice()).isEqualTo(request.price());
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(request.isPaymentRequired());
                });
            }

            @Test
            void 요청_이미지_썸네일만_포함_기존_썸네일이_null_일_때_시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                premiere.updateThumbnail(null);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                MockMultipartFile thumbnail = new MockMultipartFile(
                        "thumbnail",
                        "thumbnail.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "thumbnail".getBytes()
                );

                given(s3Service.saveImage(any(), any())).willReturn("https://s3-bucket.../thumbnail.jpg");

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        10000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiereAdminService.updatePremiere(admin, 1L, request, null, thumbnail);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(premiere.getThumbnail()).isEqualTo("https://s3-bucket.../thumbnail.jpg");
                    verify(s3Service, times(1)).saveImage(any(), any());
                });
            }

            @Test
            void 요청_이미지_이벤트이미지만_포함_기존_이벤트이미지가_null_일_때_시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                premiere.updateThumbnail(null);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                MockMultipartFile eventImage = new MockMultipartFile(
                        "eventImage",
                        "eventImage.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "eventImage".getBytes()
                );

                given(s3Service.saveImage(any(), any())).willReturn("https://s3-bucket.../eventImage.jpg");

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        10000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiereAdminService.updatePremiere(admin, 1L, request, eventImage, null);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(premiere.getEventImage()).isEqualTo("https://s3-bucket.../eventImage.jpg");
                    verify(s3Service, times(1)).saveImage(any(), any());
                });
            }

            @Test
            void 요청_이미지가_모두_null_이고_기존_이미지가_있을_때_이미지_모두_삭제_후_시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        10000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiereAdminService.updatePremiere(admin, 1L, request, null, null);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(premiere.getTitle()).isEqualTo(request.title());
                    softly.assertThat(premiere.getEventImage()).isNull();
                    softly.assertThat(premiere.getThumbnail()).isNull();
                    softly.assertThat(premiere.getStartAt()).isEqualTo(request.startAt());
                    softly.assertThat(premiere.getEndAt()).isEqualTo(request.endAt());
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(request.isPaymentRequired());
                    verify(s3Service, times(2)).deleteImage(any());
                });
            }

            @Test
            void 요청_이미지와_기존_이미지가_모두_null_일_때_시사회_정보만_수정() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = mock(Premiere.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        10000,
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiereAdminService.updatePremiere(admin, 1L, request, null, null);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    verify(s3Service, times(0)).deleteImage(any());
                    verify(s3Service, times(0)).saveImage(any(), any());
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_관리자_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                UUID adminId = UUID.randomUUID();

                given(admin.getId()).willReturn(adminId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        premiereAdminService.updatePremiere(
                                admin,
                                1L,
                                mock(PremiereRequest.class),
                                mock(MultipartFile.class),
                                mock(MultipartFile.class)
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("관리자(Id: %s)가 존재하지 않습니다.", adminId));
                    verify(premiereRepository, times(0)).findById(any());
                });
            }

            @Test
            void 존재하지_않는_시사회_PremiereException_premiereNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = mock(Premiere.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                PremiereException exception = assertThrows(PremiereException.class, () ->
                        premiereAdminService.updatePremiere(
                                admin,
                                -1L,
                                mock(PremiereRequest.class),
                                mock(MultipartFile.class),
                                mock(MultipartFile.class)
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("시사회(Id: %d)가 존재하지 않습니다.", -1L));
                });
            }
        }
    }

    @Nested
    class DeletePremiere {

        @Nested
        class Success {

            @Test
            void 이미지를_포함한_시사회_정보_삭제() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                // when -- 테스트하고자 하는 행동
                premiereAdminService.deletePremiere(admin, 1L);

                // then -- 예상되는 변화 및 결과
                verify(s3Service, times(1)).deleteImage(any());
                verify(premiereRepository, times(1)).delete(any());
            }

            @Test
            void 이미지가_null_인_시사회_정보_삭제() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = mock(Premiere.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                // when -- 테스트하고자 하는 행동
                premiereAdminService.deletePremiere(admin, 1L);

                // then -- 예상되는 변화 및 결과
                verify(s3Service, times(0)).deleteImage(any());
                verify(premiereRepository, times(1)).delete(any());
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_관리자_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                UUID adminId = UUID.randomUUID();

                given(admin.getId()).willReturn(adminId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        premiereAdminService.deletePremiere(admin, 1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("관리자(Id: %s)가 존재하지 않습니다.", adminId));
                    verify(premiereRepository, times(0)).findById(any());
                });
            }

            @Test
            void 존재하지_않는_시사회_PremiereException_premiereNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                PremiereException exception = assertThrows(PremiereException.class, () ->
                        premiereAdminService.deletePremiere(admin, -1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("시사회(Id: %d)가 존재하지 않습니다.", -1L));
                });
            }
        }
    }

    @Nested
    class FindAllPremiere {

        @Nested
        class Success {

            @Test
            void 시사회_목록_전체_조회() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(admin));

                Premiere premiere1 = PremiereFixture.createPremiere();
                Premiere premiere2 = PremiereFixture.createPremiere();

                Pageable pageable = PageRequest.of(0, 1);
                Page<Premiere> premieres = new PageImpl<>(List.of(premiere1, premiere2));

                given(premiereRepository.findAll(pageable)).willReturn(premieres);

                // when -- 테스트하고자 하는 행동
                ReadPremiereListResponse result = premiereAdminService.findAllPremiere(admin, pageable);

                // then -- 예상되는 변화 및 결과
                assertThat(result.premieres().getSize()).isEqualTo(2);
            }

            @Test
            void 시사회_빈_목록_전체_조회() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(admin));

                Pageable pageable = PageRequest.of(0, 1);
                Page<Premiere> premieres = new PageImpl<>(List.of());

                given(premiereRepository.findAll(pageable)).willReturn(premieres);

                // when -- 테스트하고자 하는 행동
                ReadPremiereListResponse result = premiereAdminService.findAllPremiere(admin, pageable);

                // then -- 예상되는 변화 및 결과
                assertThat(result.premieres().getSize()).isEqualTo(0);
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_관리자_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                UUID adminId = UUID.randomUUID();

                given(admin.getId()).willReturn(adminId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                Pageable pageable = PageRequest.of(0, 1);

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        premiereAdminService.findAllPremiere(admin, pageable)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("관리자(Id: %s)가 존재하지 않습니다.", adminId));
                    verify(premiereRepository, times(0)).findAll(pageable);
                });
            }
        }
    }

    @Nested
    class FindPremiereDetail {

        @Nested
        class Success {

            @Test
            void 시사회_상세_정보_조회() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                // when -- 테스트하고자 하는 행동
                ReadDetailPremiereResponse result = premiereAdminService.findPremiereDetail(admin, 1L);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.title()).isEqualTo(premiere.getTitle());
                    softly.assertThat(result.startAt()).isEqualTo(premiere.getStartAt());
                    softly.assertThat(result.endAt()).isEqualTo(premiere.getEndAt());
                    softly.assertThat(result.eventImage()).isEqualTo(premiere.getEventImage());
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_관리자_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);
                UUID adminId = UUID.randomUUID();

                given(admin.getId()).willReturn(adminId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        premiereAdminService.findPremiereDetail(admin, 1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("관리자(Id: %s)가 존재하지 않습니다.", adminId));
                    verify(premiereRepository, times(0)).findById(any());
                });
            }

            @Test
            void 존재하지_않는_시사회_PremiereException_premiereNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member admin = mock(Member.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
                given(premiereRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                PremiereException exception = assertThrows(PremiereException.class, () ->
                        premiereAdminService.findPremiereDetail(admin, -1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("시사회(Id: %d)가 존재하지 않습니다.", -1L));
                });
            }
        }
    }

}