package com.dcu.test.dto;

import com.dcu.test.entity.Product;
import com.dcu.test.entity.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 추가
public class ProductDTO {
    private Long id;
    private String image;
    private String title;
    private Integer price;
    private String company;
    private LocalDate manufactureDate;
    private LocalDateTime createdAt;
    private String description;
    private ProductCategory condition; // 새상품 / 중고상품
    private ProductCategory status;    // 판매중 / 판매완료

    public ProductDTO(Long id, String image, String title, Integer price, String company,
                      LocalDate manufactureDate, LocalDateTime createdAt, String description,
                      ProductCategory condition, ProductCategory status) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.price = price;
        this.company = company;
        this.manufactureDate = manufactureDate;
        this.createdAt = createdAt;
        this.description = description;
        this.condition = condition;
        this.status = status;
    }

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null; // product가 null이면 null 반환
        }

        return new ProductDTO(
                product.getId(),
                product.getImage(),
                product.getTitle(),
                product.getPrice(),
                product.getCompany(),
                product.getManufactureDate(),
                product.getCreatedAt(),
                product.getDescription(),
                product.getCondition(),
                product.getStatus()
        );
    }
}
