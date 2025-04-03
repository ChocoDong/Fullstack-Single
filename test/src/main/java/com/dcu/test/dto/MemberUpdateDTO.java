package com.dcu.test.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class MemberUpdateDTO {
    private String name;
    private LocalDate birth;
    private String nickname;
    private String newPassword;
    private MultipartFile profileImage;
}
