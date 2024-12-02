package net.pointofviews.review.service;

import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import org.springframework.data.domain.Pageable;

import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewMemberService {

	void saveReview(Long movieId, CreateReviewRequest request);

	ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request);

	void updateReview(Long movieId, Long reviewId, PutReviewRequest request);

	void deleteReview(Long movieId, Long reviewId);

	ReadReviewListResponse findReviewByMovie(Long movieId, Pageable pageable);

	ReadReviewListResponse findAllReview();

	ReadReviewDetailResponse findReviewDetail(Long reviewId);

	void updateReviewLike(Long reviewId, Long likedId);

	CreateReviewImageListResponse saveReviewImages(List<MultipartFile> files);

    void deleteReviewImages(List<String> strings);
}
