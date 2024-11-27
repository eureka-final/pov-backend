package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationNotFoundException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationServiceImpl implements CurationService {
    private final CurationRepository curationRepository;

    @Override
    @Transactional
    public ReadCurationResponse saveCuration(CreateCurationRequest request) {
        LocalDateTime startTime = LocalDateTime.parse(request.startTime(), DateTimeFormatter.ISO_DATE_TIME);

        Curation curation = Curation.builder()
                .theme(request.theme())
                .category(request.category())
                .title(request.title())
                .description(request.description())
                .build();

        Curation savedCuration = curationRepository.save(curation);

        return new ReadCurationResponse(
                savedCuration.getId(),
                savedCuration.getTheme(),
                savedCuration.getCategory(),
                savedCuration.getTitle(),
                savedCuration.getDescription()
        );
    }


    @Override
    public ReadCurationListResponse readAllCurations() {
        List<ReadCurationResponse> curationResponses = curationRepository.findAll()
                .stream()
                .map(curation -> new ReadCurationResponse(
                        curation.getId(),
                        curation.getTheme(),
                        curation.getCategory(),
                        curation.getTitle(),
                        curation.getDescription()
                ))
                .collect(Collectors.toList());

        return new ReadCurationListResponse(curationResponses);
    }

    @Override
    public ReadCurationResponse readCuration(Long curationId) {
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(CurationNotFoundException::new);

        return new ReadCurationResponse(
                curation.getId(),
                curation.getTheme(),
                curation.getCategory(),
                curation.getTitle(),
                curation.getDescription()
        );
    }

    @Override
    public ReadCurationListResponse searchCurations(String theme, CurationCategory category) {
        List<Curation> searchedCurations = curationRepository.searchCurations(theme, category);

        // 검색 결과를 Response로 변환
        List<ReadCurationResponse> responses = searchedCurations.stream()
                .map(curation -> new ReadCurationResponse(
                        curation.getId(),
                        curation.getTheme(),
                        curation.getCategory(),
                        curation.getTitle(),
                        curation.getDescription()
                ))
                .collect(Collectors.toList());

        return new ReadCurationListResponse(responses);

    }

    @Override
    @Transactional
    public ReadCurationResponse updateCuration(Long curationId, CreateCurationRequest request) {
        if(curationRepository.findById(curationId).isEmpty()){
            throw new CurationNotFoundException();
        }

        Curation curation = curationRepository.findById(curationId).get();
        curation.updateCuration(
                request.theme(),
                request.category(),
                request.title(),
                request.description()
        );

        Curation updatedCuration = curationRepository.save(curation);

        return new ReadCurationResponse(
                updatedCuration.getId(),
                updatedCuration.getTheme(),
                updatedCuration.getCategory(),
                updatedCuration.getTitle(),
                updatedCuration.getDescription()
        );
    }

    @Override
    @Transactional
    public void deleteCuration(Long curationId) {

        if(curationRepository.findById(curationId).isEmpty()){
            throw new CurationNotFoundException();
        };

        curationRepository.deleteById(curationId);
    }
}
