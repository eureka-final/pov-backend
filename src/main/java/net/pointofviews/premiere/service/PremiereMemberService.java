package net.pointofviews.premiere.service;

import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.data.domain.Pageable;

public interface PremiereMemberService {

    ReadPremiereListResponse findAllPremiere(Pageable pageable);

    ReadDetailPremiereResponse findPremiereDetail(Long premiereId);
}
