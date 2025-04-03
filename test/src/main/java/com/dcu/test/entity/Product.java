package com.dcu.test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalDate manufactureDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "product_condition")
    private ProductCategory condition;

    @Enumerated(EnumType.STRING)
    private ProductCategory status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
//
//    // 회원과 연결
//    @ManyToOne(fetch = FetchType.LAZY) // 여러 상품이 한 회원에게
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member; // 상품 등록 유저
}
