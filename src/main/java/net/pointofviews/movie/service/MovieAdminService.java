package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.response.ReadDailyMovieLikeListResponse;

public interface MovieAdminService {

    ReadDailyMovieLikeListResponse findDailyMovieLikeList(Member loginMember);
}
