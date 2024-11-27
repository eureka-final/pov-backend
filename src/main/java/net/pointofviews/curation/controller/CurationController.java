package net.pointofviews.curation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies/curations")
@RequiredArgsConstructor
public class CurationController implements CurationSpecification{

    private final CurationService curationService;

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> createCuration(
//            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateCurationRequest createCurationRequest) {

        // 관리자 유효성 검사 추가

        ReadCurationResponse response = curationService.saveCuration(createCurationRequest);
        return BaseResponse.ok("큐레이션이 성공적으로 생성되었습니다.", response);
    }

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationListResponse>> readAllCurations() {
        ReadCurationListResponse response = curationService.readAllCurations();
        return BaseResponse.ok("큐레이션 전체 조회에 성공하였습니다.", response);
    }

    @GetMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> readCuration(Long curationId) {
        ReadCurationResponse response = curationService.readCuration(curationId);
        return BaseResponse.ok("큐레이션 조회에 성공하였습니다.", response);
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<ReadCurationListResponse>> searchCurations(
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) CurationCategory category) {

        ReadCurationListResponse response = curationService.searchCurations(theme, category);
        return BaseResponse.ok("큐레이션 검색 성공", response);
    }

    @PutMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> updateCuration(Long curationId, @Valid @RequestBody CreateCurationRequest createCurationRequest) {
        ReadCurationResponse response = curationService.updateCuration(curationId, createCurationRequest);
        return BaseResponse.ok("큐레이션이 성공적으로 수정되었습니다.", response);
    }

    @DeleteMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> deleteCuration(Long curationId) {
        curationService.deleteCuration(curationId);
        return BaseResponse.ok("큐레이션이 성공적으로 삭제되었습니다.");
    }
}
