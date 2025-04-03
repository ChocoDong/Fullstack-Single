package com.dcu.test.repository;

import com.dcu.test.entity.Product;
import com.dcu.test.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(COALESCE(:condition, NULL) IS NULL OR p.condition IN :condition) AND " +
            "(COALESCE(:status, NULL) IS NULL OR p.status IN :status)")
    Page<Product> findByFilters(
            @Param("keyword") String keyword,
            @Param("condition") List<ProductCategory> condition,
            @Param("status") List<ProductCategory> status,
            Pageable pageable);
}
