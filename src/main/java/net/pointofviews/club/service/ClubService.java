package net.pointofviews.club.service;

import jakarta.validation.Valid;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.CreateClubImageListResponse;
import net.pointofviews.club.dto.response.CreateClubResponse;
import net.pointofviews.club.dto.response.PutClubLeaderResponse;
import net.pointofviews.club.dto.response.PutClubResponse;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ClubService {
    CreateClubResponse saveClub(@Valid CreateClubRequest request, Member member);

    PutClubResponse updateClub(UUID clubId, @Valid PutClubRequest request, Member member);

    PutClubLeaderResponse updateClubLeader(UUID clubId, @Valid PutClubLeaderRequest request, Member member);

    Void deleteClub(UUID clubId, Member member);

    Void leaveClub(UUID clubId, Member member);

    CreateClubImageListResponse saveClubImages(List<MultipartFile> files, Member member);

    CreateClubImageListResponse updateClubImages(List<MultipartFile> files, Member member);
}
