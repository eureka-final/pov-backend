package net.pointofviews.premiere.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Premiere-Admin", description = "시사회 관련 관리자 API")
public interface PremiereAdminSpecification {

    @Operation(
            summary = "시사회 등록",
            description = "관리자가 시사회를 등록할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "시사회 등록 성공", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "시사회가 성공적으로 등록되었습니다."
                            }
                            """)
            ))
    })
    ResponseEntity<BaseResponse<Void>> createPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestBody @Valid PremiereRequest request
    );

    @Operation(
            summary = "시사회 수정",
            description = "관리자가 시사회를 수정할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 수정 성공", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "시사회 정보가 성공적으로 수정되었습니다."
                            }
                            """)
            ))
    })
    ResponseEntity<BaseResponse<Void>> putPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Parameter(description = "수정할 시사회 ID", example = "123") Long premiereId,
            @RequestPart(value = "request") @Valid PremiereRequest request,
            @RequestPart(value = "eventImage", required = false) MultipartFile eventImage,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    );

    @Operation(
            summary = "시사회 삭제",
            description = "관리자가 시사회를 삭제할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 삭제 성공", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "시사회가 성공적으로 삭제되었습니다."
                            }
                            """)
            )),
            @ApiResponse(responseCode = "404", description = "시사회 상세 조회 실패 - 존재하지 않는 시사회", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "시사회(Id: 123)가 존재하지 않습니다."
                            }
                            """)
            ))
    })
    ResponseEntity<BaseResponse<Void>> deletePremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Parameter(description = "삭제할 시사회 ID", example = "123") Long premiereId
    );

    @Operation(
            summary = "시사회 전체 조회",
            description = "등록된 모든 시사회 목록을 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 전체 조회 성공"),
            @ApiResponse(responseCode = "204", description = "아직 시사회 정보 없음")
    })
    ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PageableDefault(size = 8, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "시사회 상세 조회",
            description = "시사회 ID를 이용해 특정 시사회의 세부 정보를 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "시사회 상세 조회 실패 - 존재하지 않는 시사회", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "시사회(Id: 123)가 존재하지 않습니다."
                            }
                            """)
            ))

    })
    ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetail(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Parameter(description = "조회할 시사회 ID", example = "123") Long premiereId
    );
}
