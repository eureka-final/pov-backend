package net.pointofviews.premiere.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.PremiereMemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.pointofviews.premiere.exception.PremiereException.premiereNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PremiereMemberServiceImpl implements PremiereMemberService {

    private final PremiereRepository premiereRepository;

    @Override
    public ReadPremiereListResponse findAllPremiere(Pageable pageable) {
        return null;
    }

    @Override
    public ReadDetailPremiereResponse findPremiereDetail(Long premiereId) {

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        ReadDetailPremiereResponse response = new ReadDetailPremiereResponse(
                premiere.getTitle(),
                premiere.getStartAt(),
                premiere.getEndAt(),
                premiere.getPrice(),
                premiere.isPaymentRequired(),
                premiere.getEventImage(),
                premiere.getThumbnail()
        );

        return response;
    }
}
