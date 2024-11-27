package net.pointofviews.curation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Curation", description = "관리자 영화 추천 큐레이션 API")
public interface CurationSpecification {

    @Operation(summary = "큐레이션 생성", description = "관리자가 새로운 큐레이션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "등록 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "등록 실패 - 사용자 인증 불가",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "인증되지 않은 사용자입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createCuration(
//            @RequestHeader(value = "Authorization", required = true) String authorization,
            @Valid @RequestBody CreateCurationRequest createCurationRequest
    );


    @Operation(summary = "모든 큐레이션 조회", description = "관리자가 생성한 모든 큐레이션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadCurationListResponse>> readAllCurations();

    @Operation(summary = "특정 큐레이션 조회", description = "큐레이션 ID를 기반으로 특정 큐레이션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 큐레이션 입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadCurationResponse>> readCuration(
            @PathVariable Long curationId
    );




    @Operation(summary = "큐레이션 수정", description = "관리자가 기존의 큐레이션을 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "큐레이션 수정이 성공적으로 완료되었습니다.."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "수정 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> updateCuration(
            @PathVariable Long curationId,
            @Valid @RequestBody CreateCurationRequest createCurationRequest
    );

    @Operation(summary = "큐레이션 삭제", description = "큐레이션 ID를 기반으로 특정 큐레이션을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "큐레이션 삭제가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 큐레이션",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 큐레이션입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteCuration(
            @PathVariable Long curationId
    );
}