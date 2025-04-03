package com.dcu.test.dto;

import com.dcu.test.entity.ProductCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class ProductCreateDTO {
    private MultipartFile image;
    private String title;
    private Integer price;
    private String company;
    private String description;
    private String condition;
    private Long member_Id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate manufactureDate;
}
