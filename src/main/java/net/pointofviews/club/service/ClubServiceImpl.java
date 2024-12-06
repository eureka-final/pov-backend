package net.pointofviews.club.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubFavorGenre;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.CreateClubImageListResponse;
import net.pointofviews.club.dto.response.CreateClubResponse;
import net.pointofviews.club.dto.response.PutClubLeaderResponse;
import net.pointofviews.club.dto.response.PutClubResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubFavorGenreRepository;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static net.pointofviews.club.exception.ClubException.clubNotFound;
import static net.pointofviews.common.exception.S3Exception.invalidTotalImageSize;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubServiceImpl implements ClubService {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;
    private final ClubFavorGenreRepository clubFavorGenreRepository;
    private final CommonCodeService commonCodeService;
    private final S3Service s3Service;

    @Override
    @Transactional
    public CreateClubResponse saveClub(CreateClubRequest request, Member member) {

        // 클럽 생성
        Club club = Club.builder()
                .name(request.name())
                .description(request.description())
                .maxParticipants(request.maxParticipants())
                .isPublic(request.isPublic())
                .build();
        Club savedClub = clubRepository.save(club);

        // 클럽장 등록
        MemberClub memberClub = MemberClub.builder()
                .club(savedClub)
                .member(member)
                .isLeader(true)
                .build();
        memberClubRepository.save(memberClub);

        // 선호 장르 등록
        List<String> savedGenres = new ArrayList<>();
        if (!request.clubFavorGenre().isEmpty()) {
            request.clubFavorGenre().forEach(genreName -> {
                String genreCode = commonCodeService.convertNameToCommonCode(
                        genreName,
                        CodeGroupEnum.MOVIE_GENRE
                );

                ClubFavorGenre favorGenre = ClubFavorGenre.builder()
                        .club(savedClub)
                        .genreCode(genreCode)
                        .build();
                clubFavorGenreRepository.save(favorGenre);
                savedGenres.add(genreName);
            });
        }

        if (!request.clubImage().isEmpty()) {
            // 이미지 경로 이동
            String oldPath = request.clubImage().replace("https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");
            String newPath = "clubs/" + savedClub.getId() + "/profile/" + oldPath.substring(oldPath.lastIndexOf("/") + 1);
            s3Service.moveImage(oldPath, newPath);
            String newImageUrl = s3Service.getImage(newPath);

            // 이미지 URL 업데이트
            savedClub.updateClubImage(newImageUrl);
        }

        return new CreateClubResponse(
                savedClub.getId(),
                savedClub.getName(),
                savedClub.getDescription(),
                savedClub.getMaxParticipants(),
                1,
                savedClub.isPublic(),
                savedGenres
        );
    }

    @Override
    @Transactional
    public PutClubResponse updateClub(UUID clubId, PutClubRequest request, Member member) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> clubNotFound(clubId));

        validateClubLeader(club, member);

        // 클럽 수정
        club.updateClub(
                request.name(),
                request.description(),
                request.maxParticipants(),
                request.isPublic()
        );
        if (request.clubImage() != null && !request.clubImage().isEmpty()) {
            club.updateClubImage(request.clubImage());
        }

        // 선호 장르 변경
        Map<String, String> genreCodeMap = request.clubFavorGenre().stream()
                .collect(Collectors.toMap(
                        genre -> genre,
                        genre -> commonCodeService.convertNameToCommonCode(genre, CodeGroupEnum.MOVIE_GENRE)
                ));

        List<ClubFavorGenre> existingGenres = clubFavorGenreRepository.findAllByClub(club);
        Set<String> existingCodes = existingGenres.stream()
                .map(ClubFavorGenre::getGenreCode)
                .collect(Collectors.toSet());

        Set<String> newCodes = new HashSet<>(genreCodeMap.values());

        List<ClubFavorGenre> genresToDelete = existingGenres.stream()
                .filter(genre -> !newCodes.contains(genre.getGenreCode()))
                .toList();
        clubFavorGenreRepository.deleteAll(genresToDelete);

        Set<String> codesToAdd = newCodes.stream()
                .filter(code -> !existingCodes.contains(code))
                .collect(Collectors.toSet());

        codesToAdd.forEach(code -> {
            ClubFavorGenre newGenre = ClubFavorGenre.builder()
                    .club(club)
                    .genreCode(code)
                    .build();
            clubFavorGenreRepository.save(newGenre);
        });

        return new PutClubResponse(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getMaxParticipants(),
                club.isPublic(),
                request.clubFavorGenre()
        );
    }

    @Override
    public PutClubLeaderResponse updateClubLeader(UUID clubId, PutClubLeaderRequest request, Member member) {
        return null;
    }

    @Override
    public Void deleteClub(UUID clubId, Member member) {
        return null;
    }

    @Override
    public Void leaveClub(UUID clubId, Member member) {
        return null;
    }

    @Override
    public CreateClubImageListResponse saveClubImages(List<MultipartFile> files, Member member) {
        long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > 10 * 1024 * 1024) {
            throw invalidTotalImageSize();
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            s3Service.validateImageFile(file);
            String uniqueFileName = s3Service.createUniqueFileName(file.getOriginalFilename());
            String filePath = "clubs/" + uniqueFileName;
            String imageUrl = s3Service.saveImage(file, filePath);
            imageUrls.add(imageUrl);
        }

        return new CreateClubImageListResponse(imageUrls);
    }

    @Override
    public CreateClubImageListResponse updateClubImages(UUID clubId, List<MultipartFile> files, Member member) {
        long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > 10 * 1024 * 1024) {
            throw invalidTotalImageSize();
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> clubNotFound(clubId));
        validateClubLeader(club, member);

        String clubImage = club.getClubImage();
        if (clubImage != null && !clubImage.isEmpty()) {
            String oldImagePath = clubImage.replace("https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");
            s3Service.deleteImage(oldImagePath);
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            s3Service.validateImageFile(file);
            String uniqueFileName = s3Service.createUniqueFileName(file.getOriginalFilename());
            String filePath = "clubs/" + club.getId() + "/profile/" + uniqueFileName;
            String imageUrl = s3Service.saveImage(file, filePath);
            imageUrls.add(imageUrl);
            club.updateClubImage(imageUrl);
        }

        return new CreateClubImageListResponse(imageUrls);
    }

    private void validateClubLeader(Club club, Member member) {
        MemberClub memberClub = memberClubRepository.findByClubAndMember(club, member)
                .orElseThrow(ClubException::memberNotInClub);

        if (!memberClub.isLeader()) {
            throw ClubException.notClubLeader();
        }
    }
}
