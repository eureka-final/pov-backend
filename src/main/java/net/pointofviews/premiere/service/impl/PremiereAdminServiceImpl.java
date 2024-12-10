package net.pointofviews.premiere.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereResponse;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.PremiereAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            MultipartFile eventImage,
            MultipartFile thumbnail
    ) {
        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        // 이벤트 이미지 처리
        String newEventImage = processImage(
                eventImage,
                premiere.getEventImage(),
                "premieres/" + premiereId + "/event/"
        );

        if (newEventImage != null || eventImage == null) {
            premiere.updateEventImage(newEventImage);
        }

        // 썸네일 이미지 처리
        String newThumbnail = processImage(
                thumbnail,
                premiere.getThumbnail(),
                "premieres/" + premiereId + "/thumbnail/"
        );

        if (newThumbnail != null || thumbnail == null) {
            premiere.updateThumbnail(newThumbnail);
        }

        premiere.updatePremiere(request);
    }

    private String processImage(MultipartFile file, String oldImage, String folderPath) {
        if (file != null && !file.isEmpty()) {
            s3Service.validateImageFile(file);

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = s3Service.createUniqueFileName(originalFileName);
            String filePath = folderPath + uniqueFileName;
            String newImageUrl = s3Service.saveImage(file, filePath);

            if (oldImage != null) {
                s3Service.deleteImage(oldImage);
            }

            return newImageUrl;

        } else if (oldImage != null) {
            s3Service.deleteImage(oldImage);
            return null;
        }

        return null;
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
    public ReadPremiereListResponse findAllPremiere(Member loginMember, Pageable pageable) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Page<Premiere> premierePage = premiereRepository.findAll(pageable);

        Page<ReadPremiereResponse> premieres = premierePage.map(premiere ->
                new ReadPremiereResponse(
                        premiere.getId(),
                        premiere.getTitle(),
                        premiere.getThumbnail(),
                        premiere.getStartAt()
                )
        );

        return new ReadPremiereListResponse(premieres);
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
                premiere.getPrice(),
                premiere.isPaymentRequired(),
                premiere.getEventImage(),
                premiere.getThumbnail()
        );

        return response;
    }
}
