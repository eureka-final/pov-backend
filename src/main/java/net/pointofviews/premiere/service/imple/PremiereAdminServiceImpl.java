package net.pointofviews.premiere.service.imple;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.PremiereAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static net.pointofviews.member.exception.MemberException.adminNotFound;
import static net.pointofviews.premiere.exception.PremiereException.premiereNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PremiereAdminServiceImpl implements PremiereAdminService {

    private final PremiereRepository premiereRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Override
    public void savePremiere(Member loginMember, PremiereRequest premiere) {
    }

    @Override
    @Transactional
    public void updatePremiere(
            Member loginMember,
            Long premiereId,
            PremiereRequest request,
            MultipartFile file
    ) {
        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        String oldEventImage = premiere.getEventImage();

        if (file == null) {
            if (oldEventImage != null) {
                s3Service.deleteImage(oldEventImage);
                premiere.updateEventImage(null);
            }
        } else {
            s3Service.validateImageFile(file);

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = s3Service.createUniqueFileName(originalFileName);
            String filePath = "premieres/" + premiereId + "/" + uniqueFileName;

            String imageUrl = s3Service.saveImage(file, filePath);

            if (oldEventImage != null) {
                s3Service.deleteImage(oldEventImage);
            }

            premiere.updateEventImage(imageUrl);
        }

        premiere.updatePremiere(request);
    }

    @Override
    @Transactional
    public void deletePremiere(Member loginMember, Long premiereId) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        if (premiere.getEventImage() != null) {
            s3Service.deleteImage(premiere.getEventImage());
        }

        premiereRepository.delete(premiere);
    }

    @Override
    public void findAllPremiere(Member loginMember) {
    }

    @Override
    public ReadDetailPremiereResponse findPremiereDetail(Member loginMember, Long premiereId) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        ReadDetailPremiereResponse response = new ReadDetailPremiereResponse(
                premiere.getTitle(),
                premiere.getStartAt(),
                premiere.getEndAt(),
                premiere.isPaymentRequired(),
                premiere.getEventImage()
        );

        return response;
    }
}
