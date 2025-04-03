package com.dcu.test.controller;

import com.dcu.test.dto.MemberDTO;
import com.dcu.test.dto.MemberUpdateDTO;
import com.dcu.test.entity.Member;
import com.dcu.test.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/memberSignUp")
    String memberSignUp() {
        return "member/memberSignUp";
    }

    @PostMapping("/memberSignUp")
    String memberSignUp(String email, String password, String passwordConfirm, String name, LocalDate birth, String nickname, Model model) {
        // 이메일 공백 및 정규식(Regular Expression)을 이용한 형식 확인
        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            addAttributeValidationResult(model, email, name, birth, "유효한 이메일을 입력해주세요.", nickname);
            return "member/memberSignUp";
        }

        // 이메일 중복 확인
        if (memberService.memberFindByEmailIsPresent(email)) {
            addAttributeValidationResult(model, email, name, birth, "이미 등록된 이메일입니다.", nickname);
            return "member/memberSignUp";
        }

        // 비밀번호 공백 및 길이 확인
        if (password == null || password.trim().length() < 8) {
            addAttributeValidationResult(model, email, name, birth, "비밀번호는 8자 이상이어야 합니다.", nickname);
            return "member/memberSignUp";
        }

        // 비밀번호 일치 여부 확인
        if (!password.equals(passwordConfirm)) {
            addAttributeValidationResult(model, email, name, birth, "비밀번호가 일치하지 않습니다.", nickname);
            return "member/memberSignUp";
        }

        // 이름 공백 확인
        if (name == null || name.trim().isEmpty()) {
            addAttributeValidationResult(model, email, name, birth, "이름을 입력해주세요.", nickname);
            return "member/memberSignUp";
        }

        // 생년월일 공백 및 미래 날짜 확인
        if (birth == null || birth.isAfter(LocalDate.now())) {
            addAttributeValidationResult(model, email, name, birth, "올바른 생년월일을 입력해주세요.", nickname);
            return "member/memberSignUp";
        }

        // 닉네임 공백 확인
        if (nickname == null || nickname.trim().isEmpty()) {
            addAttributeValidationResult(model, email, name, birth, "이름을 입력해주세요.", nickname);
            return "member/memberSignUp";
        }

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setName(name);
        member.setBirth(birth);
        member.setNickname(nickname);

        memberService.signUp(member);
        return "redirect:/memberLogin";
    }

    @GetMapping("/memberLogin")
    String memberLogin() {
        return "member/memberLogin";
    }

    @GetMapping("/memberPage")
    String memberPage(@AuthenticationPrincipal User user, Model model) {
        // model로 정보 넘기기
        Member member = memberService.findMemberByEmail(user.getUsername());
        model.addAttribute("member", member);

        return "member/memberPage";
    }

    // 유효성 검증 결과 메서드
    private void addAttributeValidationResult(Model model, String email, String name, LocalDate birth, String message, String nickname) {
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("birth", birth);
        model.addAttribute("message", message);
        model.addAttribute("nickname", nickname);
    }

    // 회원 정보 수정 페이지
    @GetMapping("/memberEdit")
    public String editMemberPage(@RequestParam("id") Long id, Model model) {
        Member member = memberService.getMemberInfo(id);
        model.addAttribute("member", member);
        return "member/memberEdit";
    }

    // 회원 정보 업데이트
    @PostMapping("/memberEdit")
    public String updateMemberInfo(@RequestParam("id") Long id,
                                   @ModelAttribute MemberUpdateDTO memberUpdateDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            memberService.updateMember(id, memberUpdateDTO);
            redirectAttributes.addFlashAttribute("success", "회원 정보가 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 정보 수정에 실패했습니다.");
        }
        return "redirect:/memberPage";
    }

}

