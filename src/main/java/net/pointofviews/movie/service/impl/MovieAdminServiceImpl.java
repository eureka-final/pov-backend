package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.DailyMovieLike;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.dto.DailyMovieLikeDto;
import net.pointofviews.movie.dto.response.ReadDailyMovieLikeListResponse;
import net.pointofviews.movie.repository.DailyMovieLikeRepository;
import net.pointofviews.movie.service.MovieAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static net.pointofviews.member.exception.MemberException.adminNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieAdminServiceImpl implements MovieAdminService {

    private final DailyMovieLikeRepository movieLikeRepository;
    private final MemberRepository memberRepository;

    @Override
    public ReadDailyMovieLikeListResponse findDailyMovieLikeList(Member loginMember) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        LocalDateTime date = LocalDateTime.now().minusDays(1);

        List<DailyMovieLike> dailyMovieLikes = movieLikeRepository.findDailyMovieLikeStatistics();

        List<DailyMovieLikeDto> movies = dailyMovieLikes.stream()
                .map(dailyMovieLike -> {
                    Movie movie = dailyMovieLike.getMovie();

                    return new DailyMovieLikeDto(
                            movie.getId(),
                            movie.getTitle(),
                            dailyMovieLike.getTotalCount()
                    );
                })
                .toList();

        return new ReadDailyMovieLikeListResponse(date, movies);
    }
}
