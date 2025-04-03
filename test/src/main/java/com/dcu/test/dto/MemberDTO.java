package com.dcu.test.dto;

import com.dcu.test.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private String username;
    private String email;
    private String password;
    private String name;
    private String birth;
    private String nickname;

}
