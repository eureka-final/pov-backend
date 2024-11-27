package net.pointofviews.curation.service;

import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;

public interface CurationService {
    ReadCurationResponse saveCuration(CreateCurationRequest request);
    ReadCurationListResponse readAllCurations();
    ReadCurationResponse readCuration(Long curationId);
    ReadCurationListResponse searchCurations(String theme, CurationCategory category);
    ReadCurationResponse updateCuration(Long curationId, CreateCurationRequest request);
    void deleteCuration(Long curationId);
}
