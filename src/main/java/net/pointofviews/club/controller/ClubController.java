package net.pointofviews.club.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.club.dto.request.*;
import net.pointofviews.club.service.ClubService;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController implements ClubSpecification{

    private final ClubService clubService;

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs() {
        return null;
    }

    @GetMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(String clubId) {
        return null;
    }

    @GetMapping("/myclub")
    @Override
    public ResponseEntity<BaseResponse<ReadMyClubsListResponse>> readMyClubs() {
        return null;
    }

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<CreateClubResponse>> createClub(@Valid @RequestBody CreateClubRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        CreateClubResponse response = clubService.saveClub(request, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 생성되었습니다.", response);
    }

    @Override
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<CreateClubImageListResponse>> createClubImages(
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateClubImageListResponse response = clubService.saveClubImages(files,memberDetailsDto.member());
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
    }

    @Override
    @PostMapping(value = "/{clubId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<CreateClubImageListResponse>> putClubImages(
            @PathVariable UUID clubId,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateClubImageListResponse response = clubService.updateClubImages(clubId, files, memberDetailsDto.member());
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
    }

    @PutMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<PutClubResponse>> putClub(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        PutClubResponse response = clubService.updateClub(clubId, request, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 수정되었습니다.", response);
    }

    @DeleteMapping("/{clubId}/leave")
    @Override
    public ResponseEntity<BaseResponse<Void>> leaveClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto){
        clubService.leaveClub(clubId, memberDetailsDto.member());
        return BaseResponse.ok("클럽을 성공적으로 탈퇴하였습니다.");
    }

    @DeleteMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> deleteClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        clubService.deleteClub(clubId, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 삭제되었습니다.");
    }

    @PutMapping("/{clubId}/leader")
    @Override
    public ResponseEntity<BaseResponse<PutClubLeaderResponse>> putClubLeader(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubLeaderRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        PutClubLeaderResponse response = clubService.updateClubLeader(clubId, request, memberDetailsDto.member());
        return BaseResponse.ok("클럽장이 성공적으로 변경되었습니다.", response);
    }

    // 그룹원 강퇴
    @PutMapping("/{clubId}/member/{memberId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> kickMemberFromClub(
            @PathVariable UUID clubId,
            @PathVariable UUID memberId) {
        return null;
    }

    // 그룹원 목록 조회
    @GetMapping("/{clubId}/member")
    @Override
    public ResponseEntity<BaseResponse<ReadClubMemberListResponse>> readClubMembers(@PathVariable UUID clubId) {
        return null;
    }
}
