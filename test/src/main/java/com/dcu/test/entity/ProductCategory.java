package com.dcu.test.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public enum ProductCategory {
    새상품("새상품"), 중고상품("중고상품"), 판매중("판매중"), 판매완료("판매완료");

    private final String value;

    ProductCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProductCategory fromValue(String value) {
        return Arrays.stream(ProductCategory.values())
                .filter(category -> category.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 상품 상태: " + value));
    }

    // 등록 시 선택 가능
    public static List<ProductCategory> getRegistrationOptions() {
        return List.of(새상품, 중고상품);
    }

    // 수정 시 선택 가능
    public static List<ProductCategory> getUpdateOptions() {
        return List.of(판매중, 판매완료);
    }
}
