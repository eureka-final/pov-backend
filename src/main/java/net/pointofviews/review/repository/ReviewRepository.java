package net.pointofviews.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.response.ReadReviewResponse;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query(value = """
		SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
				mv.title,
				r.title,
				r.contents,
				m.nickname,
				r.thumbnail,
				r.createdAt,
				(SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id),
				CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END
		 )
		 FROM Review r
		 JOIN r.member m
		 JOIN r.movie mv
		 WHERE mv.id = :movieId
	""")
	Slice<ReadReviewResponse> findAllWithLikesByMovieId(@Param("movieId") Long movieId, Pageable pageable);

	@Query("""
		SELECT r
		  FROM Review r
		  JOIN FETCH r.member m
		  JOIN FETCH r.movie mv
		 WHERE r.id = :reviewId
	""")
	Optional<Review> findReviewDetailById(@Param("reviewId") Long reviewId);
}
