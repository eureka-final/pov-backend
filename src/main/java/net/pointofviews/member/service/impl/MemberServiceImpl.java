package net.pointofviews.member.service.impl;

import static net.pointofviews.member.exception.MemberException.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.member.domain.MemberFavorGenre;
import net.pointofviews.member.dto.request.PutMemberGenreListRequest;
import net.pointofviews.member.dto.request.PutMemberImageRequest;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.request.PutMemberNoticeRequest;
import net.pointofviews.member.dto.response.PutMemberGenreListResponse;
import net.pointofviews.member.dto.response.PutMemberImageResponse;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.dto.response.PutMemberNoticeResponse;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberFavorGenreRepository;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final MemberFavorGenreRepository memberFavorGenreRepository;
	private final JwtProvider jwtProvider;

	@Override
	public CreateMemberResponse signup(CreateMemberRequest request) {
		return null;
	}

	@Override
	public LoginMemberResponse login(LoginMemberRequest request) {
		// 이메일로 회원 조회
		Member member = memberRepository.findByEmail(request.email())
			.orElseThrow(MemberException::memberNotFound);

		if (!member.getSocialType().name().equals(request.socialType())) {
			throw invalidSocialType();
		}

		// 응답 생성
		return new LoginMemberResponse(
			member.getId(),
			member.getEmail(),
			member.getNickname(),
			member.getRoleType().name()
		);
	}

	@Override
	public void deleteMember() {

	}

	@Override
	@Transactional
	public PutMemberGenreListResponse updateGenre(Member loginMember, PutMemberGenreListRequest request) {

		Member member = memberRepository.findById(loginMember.getId())
			.orElseThrow(() -> memberNotFound(loginMember.getId()));

		deleteExistingGenres(member);

		List<String> newFavorGenres = saveNewGenres(request, member);

		return new PutMemberGenreListResponse(newFavorGenres);
	}

	private void deleteExistingGenres(Member member) {
		List<MemberFavorGenre> originalFavorGenres = memberFavorGenreRepository.findAllByMemberId(member.getId());

		if (!originalFavorGenres.isEmpty()) {
			memberFavorGenreRepository.deleteAllByMemberId(member.getId());
		}
	}

	private List<String> saveNewGenres(PutMemberGenreListRequest request, Member member) {

		return request.genres().stream()
			.peek(genreName -> {
				String genreCode = memberFavorGenreRepository.findGenreCodeByGenreName(genreName);

				if (genreCode == null) {
					throw memberGenreBadRequest(genreName);
				}

				MemberFavorGenre memberFavorGenre = MemberFavorGenre.builder()
					.member(member)
					.genreCode(genreCode)
					.build();

				memberFavorGenreRepository.save(memberFavorGenre);
			}).collect(Collectors.toList());
	}

	@Override
	public PutMemberImageResponse updateImage(PutMemberImageRequest request) {
		return null;
	}

	@Override
	@Transactional
	public PutMemberNicknameResponse updateNickname(Member loginMember, PutMemberNicknameRequest request) {

		Member member = memberRepository.findById(loginMember.getId())
			.orElseThrow(() -> memberNotFound(loginMember.getId()));

		if (memberRepository.existsByNickname(request.nickname())) {
			throw nicknameDuplicate();
		}

		member.updateNickname(request.nickname());

		return new PutMemberNicknameResponse(member.getNickname());
	}

	@Override
	public PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request) {
		return null;
	}
}
