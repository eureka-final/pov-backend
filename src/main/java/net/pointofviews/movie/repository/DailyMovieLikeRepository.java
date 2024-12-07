package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.DailyMovieLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyMovieLikeRepository extends JpaRepository<DailyMovieLike, Long> {

    @Query(value = """
            SELECT d
              FROM DailyMovieLike d
              JOIN FETCH d.movie m
             WHERE DATE(d.createdAt) = CURRENT_DATE
            """)
    List<DailyMovieLike> findDailyMovieLikeStatistics();

}