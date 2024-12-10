package net.pointofviews.premiere.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Premiere-Member", description = "시사회 관련 사용자 API")
public interface PremiereMemberSpecification {

    @Operation(
            summary = "시사회 전체 조회",
            description = "등록된 모든 시사회 목록을 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 전체 조회 성공")
    })
    ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList();

    @Operation(
            summary = "시사회 상세 조회",
            description = "시사회 ID를 이용해 특정 시사회의 세부 정보를 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "시사회 상세 조회 실패 - 존재하지 않는 시사회",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회(Id: 123)가 존재하지 않습니다."
                                    }
                                    """)
                    ))

    })
    ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetail(
            @Parameter(description = "조회할 시사회 ID", example = "123") Long premiereId
    );


    @Operation(
            summary = "시사회 응모",
            description = "사용자가 시사회에 응모할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "시사회 응모 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "시사회 응모 실패 - 존재하지 않는 시사회",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회(Id: 123)가 존재하지 않습니다."
                                    }
                                    """)
                    ))

    })
    ResponseEntity<BaseResponse<Void>> createEntryPremiere(
            @Parameter(description = "응모할 시사회 ID", example = "123") Long premiereId
    );

}
