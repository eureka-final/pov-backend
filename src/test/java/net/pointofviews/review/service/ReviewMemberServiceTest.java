package net.pointofviews.review.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.exception.S3Exception;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.fixture.ReviewFixture;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.exception.ReviewException;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.impl.ReviewMemberServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewMemberServiceTest {

    @InjectMocks
    private ReviewMemberServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewLikeCountRepository reviewLikeCountRepository;

    @Mock
    private ReviewKeywordLinkRepository reviewKeywordLinkRepository;

    @Mock
    private CommonCodeService commonCodeService;

    @Mock
    private S3Service s3Service;

    @Nested
    class SaveReview {
        @Nested
        class Success {
            @Test
            void 리뷰_등록_성공() {
                // given
                Member loginMember = mock(Member.class);
                Movie movie = mock(Movie.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));
                given(commonCodeService.convertCommonCodeNameToCommonCode(any(), eq(CodeGroupEnum.REVIEW_KEYWORD)))
                        .willReturn("01");

                CreateReviewRequest request = new CreateReviewRequest(
                        "제목",
                        "내용",
                        "긍정적",
                        List.of("감동적인", "몰입감 있는"),
                        false
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> reviewService.saveReview(1L, request, loginMember))
                            .doesNotThrowAnyException();
                });
            }

            @Test
            void 키워드없이_리뷰_등록_성공() {
                // given
                Member loginMember = mock(Member.class);
                Movie movie = mock(Movie.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));

                CreateReviewRequest request = new CreateReviewRequest(
                        "제목",
                        "내용",
                        "긍정적",
                        List.of(),
                        false
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> reviewService.saveReview(1L, request, loginMember))
                            .doesNotThrowAnyException();
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_회원_MemberNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                CreateReviewRequest request = new CreateReviewRequest(
                        "제목",
                        "내용",
                        "긍정적",
                        List.of("감동적인"),
                        false
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.saveReview(1L, request, loginMember))
                            .isInstanceOf(MemberException.class);
                });
            }

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.empty());

                CreateReviewRequest request = new CreateReviewRequest(
                        "제목",
                        "내용",
                        "긍정적",
                        List.of("감동적인"),
                        false
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.saveReview(1L, request, loginMember))
                            .isInstanceOf(MovieException.class);
                });
            }
        }
    }

    @Nested
    class UpdateReview {
        private final PutReviewRequest request = new PutReviewRequest(
                "수정된 제목",
                "수정된 내용",
                "긍정적",
                List.of("감동적인"),
                false
        );

        @Nested
        class Success {
            @Test
            void 리뷰_수정_성공() {
                // given
                Member loginMember = mock(Member.class);
                Review review = mock(Review.class);
                UUID memberId = UUID.randomUUID();

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(mock(Movie.class)));
                given(reviewRepository.findById(any())).willReturn(Optional.of(review));
                given(loginMember.getId()).willReturn(memberId);
                given(review.getMember()).willReturn(loginMember);

                given(reviewKeywordLinkRepository.findAllByReview(any()))
                        .willReturn(List.of(mock(ReviewKeywordLink.class)));
                given(commonCodeService.convertCommonCodeNameToCommonCode(any(), any()))
                        .willReturn("03");

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> reviewService.updateReview(1L, 1L, request, loginMember))
                            .doesNotThrowAnyException();
                    verify(reviewKeywordLinkRepository).deleteAll(any());
                    verify(reviewKeywordLinkRepository).save(any());
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_회원_MemberNotFoundException_예외발생() {
                // given
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request, mock(Member.class)))
                            .isInstanceOf(MemberException.class);
                });
            }

            @Test
            void 권한이_없는_사용자_ReviewException_unauthorizedReview_예외발생() {
                // given
                Member loginMember = mock(Member.class), reviewMember = mock(Member.class);
                Review review = mock(Review.class);
                UUID loginId = UUID.randomUUID();
                UUID reviewerId = UUID.randomUUID();

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(mock(Movie.class)));
                given(reviewRepository.findById(any())).willReturn(Optional.of(review));
                given(loginMember.getId()).willReturn(loginId);
                given(reviewMember.getId()).willReturn(reviewerId);
                given(review.getMember()).willReturn(reviewMember);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request, loginMember))
                            .isInstanceOf(ReviewException.class);
                });
            }

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                given(memberRepository.findById(any())).willReturn(Optional.of(mock(Member.class)));
                given(movieRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request, mock(Member.class)))
                            .isInstanceOf(MovieException.class);
                });
            }

            @Test
            void 존재하지_않는_리뷰_ReviewNotFoundException_예외발생() {
                // given
                given(memberRepository.findById(any())).willReturn(Optional.of(mock(Member.class)));
                given(movieRepository.findById(any())).willReturn(Optional.of(mock(Movie.class)));
                given(reviewRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request, mock(Member.class)))
                            .isInstanceOf(ReviewException.class);
                });
            }
        }
    }

    @Nested
    class DeleteReview {

        @Nested
        class Success {

            @Test
            void 리뷰_삭제_성공() {
                // given
                Member loginMember = mock(Member.class);
                Member reviewMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();
                Movie movie = mock(Movie.class);
                Review review = mock(Review.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));
                given(reviewRepository.findById(any())).willReturn(Optional.of(review));
                given(loginMember.getId()).willReturn(memberId);
                given(reviewMember.getId()).willReturn(memberId);
                given(review.getMember()).willReturn(reviewMember);

                // movieRepository.existsById() 호출에 대한 mock 추가
                given(movieRepository.existsById(any())).willReturn(true);
                // s3Service.deleteFolder() 호출에 대한 mock 추가
                willDoNothing().given(s3Service).deleteFolder(any());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> reviewService.deleteReview(1L, 1L, loginMember))
                            .doesNotThrowAnyException();
                    verify(review, times(1)).delete();
                    verify(s3Service, times(1)).deleteFolder(any());
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_회원_MemberNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();
                given(loginMember.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L, loginMember))
                            .isInstanceOf(MemberException.class);
                });
            }

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L, loginMember))
                            .isInstanceOf(MovieException.class);
                });
            }

            @Test
            void 존재하지_않는_리뷰_ReviewNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                Movie movie = mock(Movie.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));
                given(reviewRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L, loginMember))
                            .isInstanceOf(ReviewException.class);
                });
            }
        }
    }

    @Nested
    class FindReviewByMovie {

        @Nested
        class Success {

            @Test
            void 영화별_리뷰_조회() {
                // given -- 테스트의 상태 설정
                Movie movie = mock(Movie.class);
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));

                ReadReviewResponse review1 = ReviewFixture.readReviewResponse();
                ReadReviewResponse review2 = ReviewFixture.readReviewResponse();

                List<ReadReviewResponse> reviewList = List.of(review1, review2);
                Slice<ReadReviewResponse> reviews = new SliceImpl<>(reviewList);

                given(reviewRepository.findReviewsWithLikesByMovieId(any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadReviewListResponse result = reviewService.findReviewByMovie(1L, pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.reviews().getSize()).isEqualTo(2);
                    softly.assertThat(result.reviews().getContent()).contains(review1, review2);
                });
            }

            @Test
            void 리뷰_없을_경우_빈_목록_반환() {
                // given -- 테스트의 상태 설정
                Movie movie = mock(Movie.class);
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));

                Slice<ReadReviewResponse> reviews = new SliceImpl<>(List.of());
                given(reviewRepository.findReviewsWithLikesByMovieId(any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadReviewListResponse result = reviewService.findReviewByMovie(1L, pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.reviews()).isEmpty();
                    softly.assertThat(result.reviews().getSize()).isEqualTo(0);
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_영화_MovieException_movieNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                given(movieRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MovieException exception = assertThrows(MovieException.class, () ->
                        reviewService.findReviewByMovie(-1L, PageRequest.of(0, 10))
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo("영화(Id: -1)는 존재하지 않습니다.");
                });
            }
        }
    }

    @Nested
    class FindReviewByMember {

        @Nested
        class Success {

            @Test
            void 자신이_작성한_리뷰_전체_조회() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(member));

                ReadReviewResponse review1 = ReviewFixture.readReviewResponse();
                ReadReviewResponse review2 = ReviewFixture.readReviewResponse();

                List<ReadReviewResponse> reviewList = List.of(review1, review2);
                Slice<ReadReviewResponse> reviews = new SliceImpl<>(reviewList);

                given(reviewRepository.findReviewsWithLikesByMemberId(any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadReviewListResponse result = reviewService.findReviewByMember(member, pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.reviews().getSize()).isEqualTo(2);
                    softly.assertThat(result.reviews().getContent()).contains(review1, review2);
                });
            }

            @Test
            void 리뷰_없을_경우_빈_목록_반환() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(member));

                Slice<ReadReviewResponse> reviews = new SliceImpl<>(List.of());
                given(reviewRepository.findReviewsWithLikesByMemberId(any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadReviewListResponse result = reviewService.findReviewByMember(member, pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.reviews()).isEmpty();
                    softly.assertThat(result.reviews().getSize()).isEqualTo(0);
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_회원_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(member.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        reviewService.findReviewByMember(member, pageable)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("회원(Id: %s)이 존재하지 않습니다.", memberId));
                    verify(reviewRepository, times(0)).findReviewsWithLikesByMemberId(any(), any());
                });
            }
        }
    }

    @Nested
    class FindReviewDetail {

        @Nested
        class Success {

            @Test
            void 리뷰_상세_조회() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Review review = mock(Review.class);

                given(review.getMember()).willReturn(member);

                given(reviewRepository.findReviewDetailById(any())).willReturn(Optional.of(review));
                given(reviewLikeRepository.getIsLikedByReviewId(any())).willReturn(Optional.of(true));
                given(reviewLikeCountRepository.getReviewLikeCountByReviewId(any())).willReturn(Optional.of(10L));
                given(reviewKeywordLinkRepository.findKeywordsByReviewId(any())).willReturn(List.of("흥미진진", "몰입감"));

                // when -- 테스트하고자 하는 행동
                ReadReviewDetailResponse result = reviewService.findReviewDetail(1L);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.title()).isEqualTo(review.getTitle());
                    softly.assertThat(result.contents()).isEqualTo(review.getContents());
                    softly.assertThat(result.reviewer()).isEqualTo(member.getNickname());
                    softly.assertThat(result.profileImage()).isEqualTo(member.getProfileImage());
                    softly.assertThat(result.thumbnail()).isEqualTo(review.getThumbnail());
                    softly.assertThat(result.likeAmount()).isEqualTo(10L);
                    softly.assertThat(result.spoiler()).isFalse();
                    softly.assertThat(result.isLiked()).isTrue();
                    softly.assertThat(result.keywords()).contains("흥미진진", "몰입감");
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_리뷰_ReviewException_reviewNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                given(reviewRepository.findReviewDetailById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                ReviewException exception = assertThrows(ReviewException.class, () ->
                        reviewService.findReviewDetail(-1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo("리뷰(Id: -1)는 존재하지 않습니다.");
                });
            }
        }
    }

    @Nested
    class SaveReviewImages {
        @Nested
        class Success {
            @Test
            void 이미지_업로드_성공() {
                // given
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();
                MockMultipartFile file = new MockMultipartFile(
                        "test-image",
                        "test.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "test image content".getBytes()
                );

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(loginMember.getId()).willReturn(memberId);
                given(s3Service.saveImage(any(), any()))
                        .willReturn("https://s3-bucket.../test.jpg");

                // when
                CreateReviewImageListResponse response = reviewService.saveReviewImages(List.of(file), 1L, loginMember);


                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.imageUrls()).hasSize(1);
                    verify(s3Service, times(1)).saveImage(any(), any());
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_회원_MemberNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();
                given(loginMember.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.saveReviewImages(List.of(), 1L, loginMember))
                            .isInstanceOf(MemberException.class);
                });
            }

            @Test
            void 총_파일_크기_초과시_ImageException_invalidTotalImageSize_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));

                MockMultipartFile file1 = new MockMultipartFile(
                        "image1",
                        "test1.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        new byte[6 * 1024 * 1024]  // 6MB
                );
                MockMultipartFile file2 = new MockMultipartFile(
                        "image2",
                        "test2.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        new byte[5 * 1024 * 1024]  // 5MB
                );

                List<MultipartFile> files = List.of(file1, file2);  // 총 11MB

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.saveReviewImages(files, 1L, loginMember))
                            .isInstanceOf(S3Exception.class);
                });
            }
        }
    }

    @Nested
    class DeleteReviewImages {
        @Nested
        class Success {
            @Test
            void 이미지_폴더_삭제_성공() {
                // given
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(loginMember.getId()).willReturn(memberId);
                given(movieRepository.existsById(any())).willReturn(true);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> reviewService.deleteReviewImagesFolder(1L, loginMember))
                            .doesNotThrowAnyException();
                    verify(s3Service, times(1)).deleteFolder(any());
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_회원_MemberNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();
                given(loginMember.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.deleteReviewImagesFolder(1L, loginMember))
                            .isInstanceOf(MemberException.class);
                });
            }

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                Member loginMember = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(loginMember));
                given(movieRepository.existsById(any())).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> reviewService.deleteReviewImagesFolder(1L, loginMember))
                            .isInstanceOf(MovieException.class);
                });
            }
        }
    }

    @Nested
    class FindAllReview {

        @Nested
        class Success {

            @Test
            void 전체_리뷰_조회() {
                // given
                Pageable pageable = PageRequest.of(0, 10);

                List<ReadReviewResponse> reviewResponses = Arrays.asList(
                        mock(ReadReviewResponse.class), mock(ReadReviewResponse.class), mock(ReadReviewResponse.class)
                );

                Slice<ReadReviewResponse> mockSlice = new SliceImpl<>(reviewResponses, pageable, true);

                BDDMockito.given(reviewRepository.findAllSliced(pageable))
                        .willReturn(mockSlice);

                // when
                ReadReviewListResponse allReview = reviewService.findAllReview(pageable);

                // then
                assertThat(allReview.reviews()).hasSize(3);
            }
        }
    }

}