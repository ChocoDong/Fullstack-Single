package com.dcu.test.service;

import com.dcu.test.dto.MemberDTO;
import com.dcu.test.dto.MemberUpdateDTO;
import com.dcu.test.entity.Member;
import com.dcu.test.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "/upload/images";

    // 이메일 중복 확인
    public boolean memberFindByEmailIsPresent(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    // 회원 가입
    @Transactional
    public void signUp(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    // 이메일로 사용자 정보 조회
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));
    }

    // 회원 정보 업데이트
    public void updateMember(Long id, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        member.setName(memberUpdateDTO.getName());
        member.setBirth(memberUpdateDTO.getBirth());
        member.setNickname(memberUpdateDTO.getNickname());

        // 🔹 비밀번호 변경이 있는 경우만 업데이트
        if (memberUpdateDTO.getNewPassword() != null && !memberUpdateDTO.getNewPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(memberUpdateDTO.getNewPassword()));
        }

        // 🔹 프로필 이미지 저장 (파일이 있을 경우만 처리)
        MultipartFile profileImage = memberUpdateDTO.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = saveProfileImage(profileImage);
            member.setProfileImage(fileName);
        }

        memberRepository.save(member);
    }

    // 🔹 프로필 이미지 저장 메서드
    private String saveProfileImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + extension;
            File destinationFile = new File(UPLOAD_DIR + "/" + newFileName);
            file.transferTo(destinationFile);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // 회원 정보 조회
    public Member getMemberInfo(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }
}
