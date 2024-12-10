package net.pointofviews.premiere.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.controller.specification.PremiereAdminSpecification;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.service.PremiereAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/premieres")
public class PremiereAdminController implements PremiereAdminSpecification {

    private final PremiereAdminService premiereAdminService;

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestBody @Valid PremiereRequest request
    ) {
        return null;
    }

    @Override
    @PutMapping(value = "/{premiereId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Void>> putPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestPart(value = "request") PremiereRequest request,
            @RequestPart(value = "eventImage", required = false) MultipartFile eventImage,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        premiereAdminService.updatePremiere(loginMember, premiereId, request, eventImage, thumbnail);

        return BaseResponse.ok("시사회 정보가 성공적으로 수정되었습니다.");
    }

    @Override
    @DeleteMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<Void>> deletePremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId
    ) {
        premiereAdminService.deletePremiere(loginMember, premiereId);

        return BaseResponse.ok("시사회가 성공적으로 삭제되었습니다.");
    }

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PageableDefault(size = 8, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ReadPremiereListResponse response = premiereAdminService.findAllPremiere(loginMember, pageable);

        if (response.premieres().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("모든 시사회가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetail(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId
    ) {
        ReadDetailPremiereResponse response = premiereAdminService.findPremiereDetail(loginMember, premiereId);

        return BaseResponse.ok("시사회 상세 정보가 성공적으로 조회되었습니다.", response);
    }

}
