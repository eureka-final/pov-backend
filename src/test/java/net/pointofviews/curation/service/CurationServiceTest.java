package net.pointofviews.curation.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationNotFoundException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.impl.CurationServiceImpl;

@ExtendWith(MockitoExtension.class)
class CurationServiceTest {

    @InjectMocks
    private CurationServiceImpl curationService;

    @Mock
    private CurationRepository curationRepository;

    @Nested
    class SaveCuration {

        @Nested
        class Success {

            @Test
            void 큐레이션_저장_성공() {
                // given
                CreateCurationRequest request = new CreateCurationRequest(
                        "Theme",
                        CurationCategory.GENRE,
                        "Title",
                        "Description",
                        "2024-11-22T10:00:00Z"
                );

                Curation curation = Curation.builder()
                        .theme(request.theme())
                        .category(request.category())
                        .title(request.title())
                        .description(request.description())
                        .build();

                given(curationRepository.save(any(Curation.class))).willReturn(curation);

                // when
                ReadCurationResponse response = curationService.saveCuration(request);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.id()).isEqualTo(curation.getId());
                    softly.assertThat(response.theme()).isEqualTo(curation.getTheme());
                    softly.assertThat(response.category()).isEqualTo(curation.getCategory());
                    softly.assertThat(response.title()).isEqualTo(curation.getTitle());
                    softly.assertThat(response.description()).isEqualTo(curation.getDescription());
                });
                verify(curationRepository, times(1)).save(any(Curation.class));
            }
        }
    }

    @Nested
    class ReadAllCuration {

        @Nested
        class Success {

            @Test
            void 모든_큐레이션_조회_성공() {
                // given
                List<Curation> curations = List.of(
                        Curation.builder().theme("Theme1").category(CurationCategory.GENRE).title("Title1").description("Description1").build(),
                        Curation.builder().theme("Theme2").category(CurationCategory.DIRECTOR).title("Title2").description("Description2").build()
                );

                given(curationRepository.findAll()).willReturn(curations);

                // when
                ReadCurationListResponse response = curationService.readAllCurations();

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.curations()).hasSize(2);
                    softly.assertThat(response.curations().get(0).theme()).isEqualTo("Theme1");
                    softly.assertThat(response.curations().get(1).theme()).isEqualTo("Theme2");
                });
                verify(curationRepository, times(1)).findAll();
            }
        }
    }

    @Nested
    class ReadCuration {

        @Nested
        class Success {

            @Test
            void 특정_큐레이션_조회_성공() {
                // given
                Curation curation = Curation.builder()
                        .theme("Theme")
                        .category(CurationCategory.GENRE)
                        .title("Title")
                        .description("Description")
                        .build();

                given(curationRepository.findById(anyLong())).willReturn(Optional.of(curation));

                // when
                ReadCurationResponse response = curationService.readCuration(1L);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.id()).isEqualTo(curation.getId());
                    softly.assertThat(response.theme()).isEqualTo(curation.getTheme());
                    softly.assertThat(response.category()).isEqualTo(curation.getCategory());
                    softly.assertThat(response.title()).isEqualTo(curation.getTitle());
                    softly.assertThat(response.description()).isEqualTo(curation.getDescription());
                });
                verify(curationRepository, times(1)).findById(anyLong());
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_큐레이션_조회시_CurationNotFoundException_예외발생() {
                // given
                given(curationRepository.findById(anyLong())).willReturn(Optional.empty());

                // when & then
                assertThrows(CurationNotFoundException.class, () -> curationService.readCuration(1L));
                verify(curationRepository, times(1)).findById(anyLong());
            }
        }
    }


    @Nested
    class SearchCurations {

        @Nested
        class Success {

            @Test
            void 큐레이션_검색_성공() {
                // given
                String theme = "Action";
                CurationCategory category = CurationCategory.GENRE;

                List<Curation> curations = List.of(
                        Curation.builder().theme("Action Movies").category(CurationCategory.GENRE).title("Best Action").description("Top Action Movies").build(),
                        Curation.builder().theme("Action Favorites").category(CurationCategory.GENRE).title("All Time Action").description("Favorite Action Movies").build()
                );

                given(curationRepository.searchCurations(theme, category)).willReturn(curations);

                // when
                ReadCurationListResponse response = curationService.searchCurations(theme, category);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.curations()).hasSize(2);
                    softly.assertThat(response.curations().get(0).theme()).isEqualTo("Action Movies");
                    softly.assertThat(response.curations().get(0).category()).isEqualTo(CurationCategory.GENRE);
                    softly.assertThat(response.curations().get(0).title()).isEqualTo("Best Action");
                    softly.assertThat(response.curations().get(0).description()).isEqualTo("Top Action Movies");
                    softly.assertThat(response.curations().get(1).theme()).isEqualTo("Action Favorites");
                });

                verify(curationRepository, times(1)).searchCurations(theme, category);
            }

            @Test
            void 검색_결과가_없을_경우_빈_목록_반환() {
                // given
                String theme = "Nonexistent";
                CurationCategory category = CurationCategory.DIRECTOR;

                given(curationRepository.searchCurations(theme, category)).willReturn(List.of());

                // when
                ReadCurationListResponse response = curationService.searchCurations(theme, category);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.curations()).isEmpty();
                });

                verify(curationRepository, times(1)).searchCurations(theme, category);
            }
        }
    }

    @Nested
    class DeleteCuration {

        @Nested
        class Success {

            @Test
            void 큐레이션_삭제_성공() {
                // given
                Curation curation = Curation.builder()
                        .theme("Theme")
                        .category(CurationCategory.GENRE)
                        .title("Title")
                        .description("Description")
                        .build();

                given(curationRepository.findById(anyLong())).willReturn(Optional.of(curation));

                // when
                curationService.deleteCuration(1L);

                // then
                verify(curationRepository, times(1)).deleteById(anyLong());
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_큐레이션_삭제시_CurationNotFoundException_예외발생() {
                // given
                given(curationRepository.findById(anyLong())).willReturn(Optional.empty());

                // when & then
                assertThrows(CurationNotFoundException.class, () -> curationService.deleteCuration(1L));
                verify(curationRepository, times(1)).findById(anyLong());
            }
        }
    }
}