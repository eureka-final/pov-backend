package net.pointofviews.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.response.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Movie-Admin", description = "관리자 영화 관련 API")
public interface MovieAdminSpecification {

    @Operation(
            summary = "영화 단건 등록",
            description = "관리자가 새로운 영화를 등록할 때 사용하는 API."
    )
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
                    responseCode = "409",
                    description = "등록 실패 - 중복된 영화",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미 등록된 영화입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createMovie(CreateMovieRequest request);

    @Operation(
            summary = "영화 삭제",
            description = "영화 식별자를 이용해 서버에 등록된 영화를 삭제하는 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화 삭제가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화(Id: 1)는 존재하지 않습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteMovie(Long movieId);

    @Operation(summary = "영화 이미지 등록", description = "지정된 영화 ID에 이미지를 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 URL 등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미지 등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createImages(Long movieId, List<MultipartFile> files);

    @Operation(summary = "영화 영상 URL 등록", description = "지정된 영화 ID에 영상을 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "영상 URL 등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영상 URL 등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createVideos(Long movieId, List<String> urls);

    @Operation(summary = "영화 이미지 삭제", description = "지정된 영화 ID의 특정 이미지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미지 URL이 성공적으로 삭제되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화 이미지",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 이미지 입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteImages(Long movieId, List<Long> ids);

    @Operation(summary = "영화 영상 URL 삭제", description = "지정된 영화 ID의 특정 영상 URL을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영상 URL이 성공적으로 삭제되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화 컨텐츠",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 영상 URL 입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteVideos(Long movieId, List<Long> ids);

    @Operation(
            summary = "영화 TMDB 검색",
            description = "영화를 서버에 등록 전 TMDB에서 검색하기 위한 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "검색 실패",
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
    ResponseEntity<BaseResponse<SearchMovieApiListResponse>> searchTMDbMovieList(
            @Parameter(description = "검색할 영화 제목", example = "Inception") String query,
            @Parameter(description = "요청 페이지", example = "1") int page
    );

    @Operation(
            summary = "영화 TMDB 상세 검색",
            description = "TMDB id를 이용해 영화를 상세 검색하기 위한 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "검색 실패",
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
    ResponseEntity<BaseResponse<SearchFilteredMovieDetailResponse>> searchTMDbMovie(
            @Parameter(description = "TMDb 영화 id", example = "27205") String tmdbId);

    @Operation(
            summary = "영화 TMDB 크레딧 검색",
            description = "TMDB id를 이용해 영화의 크레딧을 검색하기 위한 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "검색 실패",
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
    ResponseEntity<BaseResponse<SearchCreditApiResponse>> searchTMDbCreditsLimit10(
            @Parameter(description = "TMDb 영화 id", example = "27205") String tmdbId);

    @Operation(
            summary = "영화 TMDB 개봉 정보 검색",
            description = "TMDB id를 이용해 영화 개봉 정보를 검색하기 위한 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "검색 실패",
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
    ResponseEntity<BaseResponse<SearchReleaseApiResponse>> searchTMDbReleases(
            @Parameter(description = "TMDb 영화 id", example = "27205") String tmdbId);

    @Operation(
            summary = "영화 좋아요 관리 조회",
            description = "일일 `좋아요` 가 가장 많은 상위 10개 영화 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "영화 좋아요 관리 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "어제 날짜의 영화 좋아요 정보 없음"
            ),

    })
    ResponseEntity<BaseResponse<ReadDailyMovieLikeListResponse>> readDailyMovieLikeList(
            @AuthenticationPrincipal(expression = "member") Member loginMember
    );

}
