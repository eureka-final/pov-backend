package net.pointofviews.premiere.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.controller.specification.EntrySpecification;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.service.EntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premieres")
public class EntryController implements EntrySpecification {

    private final EntryService entryService;

    @Override
    @PostMapping("/{premiereId}/entry")
    public ResponseEntity<BaseResponse<CreateEntryResponse>> createEntry(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestBody @Valid CreateEntryRequest request
    ) {
        CreateEntryResponse response = entryService.saveEntry(loginMember, premiereId, request);

        return BaseResponse.ok("시사회 응모가 성공적으로 완료되었습니다.", response);
    }
}