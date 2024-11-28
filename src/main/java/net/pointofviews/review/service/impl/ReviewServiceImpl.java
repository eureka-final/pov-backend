package net.pointofviews.review.service.impl;

import static net.pointofviews.movie.exception.MovieException.*;
import static net.pointofviews.review.exception.ReviewException.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewLikeCountRepository reviewLikeCountRepository;
	private final ReviewKeywordLinkRepository reviewKeywordLinkRepository;

	@Override
	@Transactional
	public void saveReview(Long movieId, CreateReviewRequest request) {

		 Movie movie = movieRepository.findById(movieId)
            		.orElseThrow(() -> movieNotFound(movieId));

		// 리뷰 생성 및 저장
		Review review = Review.builder()
				.title(request.title())
				.contents(request.contents())
				.preference(request.preference())
				.isSpoiler(request.spoiler())
				.movie(movie)
				.build();

		reviewRepository.save(review);

		// 키워드 저장
		if (request.keywords() != null && !request.keywords().isEmpty()) {
			for (String keyword : request.keywords()) {
				ReviewKeywordLink keywordLink = ReviewKeywordLink.builder()
						.review(review)
						.reviewKeywordCode(keyword)
						.build();

				reviewKeywordLinkRepository.save(keywordLink);
			}
		}
	}

	@Override
	public ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request) {
		return null;
	}

	@Override
	@Transactional
	public void updateReview(Long movieId, Long reviewId, PutReviewRequest request) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		review.update(request.title(), request.contents());
	}

	@Override
	@Transactional
	public void deleteReview(Long movieId, Long reviewId) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		review.delete(); // soft delete 처리
	}

	@Override
	public void blindReview(Long movieId, Long reviewId) {

	}

	@Override
	public ReadReviewListResponse findReviewByMovie(Long movieId, Pageable pageable) {

		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Slice<ReadReviewResponse> reviews = reviewRepository.findAllWithLikesByMovieId(movieId, pageable);

		return new ReadReviewListResponse(reviews);
	}

	@Override
	public ReadReviewListResponse findAllReview() {
		return null;
	}

	@Override
	public ReadReviewResponse findReviewDetail(Long reviewId) {

		Review review = reviewRepository.findReviewDetailById(reviewId)
			.orElseThrow(() -> reviewNotFound(reviewId));

		Long likeAmount = reviewLikeCountRepository.getReviewLikeCountByReviewId(reviewId);
		boolean isLiked = reviewLikeRepository.getIsLikedByReviewId(reviewId);

		ReadReviewResponse response = new ReadReviewResponse(
			review.getMovie().getTitle(),
			review.getTitle(),
			review.getContents(),
			review.getMember().getNickname(),
			review.getThumbnail(),
			review.getCreatedAt(),
			likeAmount,
			isLiked
		);

		return response;
	}

	@Override
	public void updateReviewLike(Long reviewId, Long likedId) {

	}
}
