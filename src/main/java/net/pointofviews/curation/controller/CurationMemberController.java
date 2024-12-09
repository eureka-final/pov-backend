package net.pointofviews.curation.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.controller.specification.CurationMemberSpecification;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.service.CurationMemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/curations")
@RequiredArgsConstructor
public class CurationMemberController implements CurationMemberSpecification {

    private final CurationMemberService curationMemberService;

    @GetMapping("/scheduled")
    @Override
    public ResponseEntity<BaseResponse<ReadUserCurationListResponse>> readScheduledCurations(Pageable pageable) {
        ReadUserCurationListResponse response = curationMemberService.readScheduledCurations(pageable);
        return BaseResponse.ok("사용자 스케줄링 된 큐레이션 조회에 성공하였습니다.", response);
    }
}
