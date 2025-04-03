package com.dcu.test.dto;

import com.dcu.test.entity.ProductCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class ProductUpdateDTO {
    private Long id;
    private MultipartFile image;
    private String originalImage;
    private String title;
    private Integer price;
    private String company;
    private LocalDate manufactureDate;
    private String description;
    private String condition;
    private String status;
}
