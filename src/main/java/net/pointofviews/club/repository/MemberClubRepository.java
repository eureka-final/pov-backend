package net.pointofviews.club.repository;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberClubRepository extends JpaRepository<MemberClub, Long> {

    @Query(value = """
            SELECT mcr
              FROM MemberClub mcr
              JOIN FETCH mcr.member m
              JOIN FETCH mcr.club c
             WHERE m.id = :memberId
            """)
    List<MemberClub> findClubsByMemberId(@Param("memberId") UUID memberId);

    @Query(value = """
            SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
                    r.id,
                    mv.id,
                    mv.title,
                    r.title,
                    r.contents,
                    m.nickname,
                    m.profileImage,
                    mv.poster,
                    r.createdAt,
                    COALESCE((SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id), 0),
                    CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END,
                    r.isSpoiler
             )
             FROM MemberClub mc
             JOIN mc.member m
             JOIN mc.club c
             JOIN Review r ON r.member.id = m.id
             JOIN r.movie mv
            WHERE c.id = :clubId AND r.deletedAt IS NULL
            ORDER BY r.createdAt DESC
            """)
    Slice<ReadReviewResponse> findReviewsWithLikesByClubId(@Param("clubId") UUID clubId, Pageable pageable);

    Optional<MemberClub> findByClubAndMember(Club club, Member member);

    void deleteAllByClub(Club club);

    long countByClub(Club club);

    @Query("""
           SELECT c.id AS clubId,
                  c.name AS clubName,
                  c.description AS clubDescription,
                  c.maxParticipants AS maxParticipant,
                  COUNT(DISTINCT mc.id) AS participantCount,
                  COUNT(DISTINCT cm.id) AS movieCount,
                  GROUP_CONCAT(DISTINCT cfg.genreCode) AS genreCodes
           FROM MemberClub mc
           JOIN mc.club c
           LEFT JOIN c.clubMovies cm
           LEFT JOIN c.clubFavorGenres cfg
           WHERE mc.member.id = :memberId
           GROUP BY c.id
           """)
    List<Object[]> findMyClubsByMemberId(@Param("memberId") UUID memberId);

    @Query("""
       SELECT new net.pointofviews.club.dto.response.ReadClubMemberResponse(
           m.nickname,
           m.profileImage,
           mc.isLeader
       )
       FROM MemberClub mc
       JOIN mc.member m
       WHERE mc.club.id = :clubId
       """)
    List<ReadClubMemberResponse> findMembersByClubId(@Param("clubId") UUID clubId);

    @Query("""
       SELECT mc
       FROM MemberClub mc
       WHERE mc.club.id = :clubId AND mc.member.id = :memberId
       """)
    Optional<MemberClub> findByClubIdAndMemberId(@Param("clubId") UUID clubId, @Param("memberId") UUID memberId);

    @Query("""
       SELECT new net.pointofviews.club.dto.response.ReadClubMemberResponse(
           mc.member.nickname,
           mc.member.profileImage,
           mc.isLeader
       )
       FROM MemberClub mc
       WHERE mc.club.id = :clubId AND mc.isLeader = true
       """)
    Optional<ReadClubMemberResponse> findLeaderByClubId(@Param("clubId") UUID clubId);
}
