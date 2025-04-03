package com.dcu.test;

import com.dcu.test.entity.Member;
import com.dcu.test.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final MemberService memberService;

    public GlobalControllerAdvice(MemberService memberService) {
        this.memberService = memberService;
    }

    @ModelAttribute("member")
    public Member getCurrentMember(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return memberService.findMemberByEmail(user.getUsername());
    }
}
