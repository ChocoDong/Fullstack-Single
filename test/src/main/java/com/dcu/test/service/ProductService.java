package com.dcu.test.service;

import com.dcu.test.dto.ProductCreateDTO;
import com.dcu.test.dto.ProductDTO;
import com.dcu.test.dto.ProductDeleteDTO;
import com.dcu.test.dto.ProductUpdateDTO;
import com.dcu.test.entity.Product;
import com.dcu.test.entity.ProductCategory;
import com.dcu.test.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;

    // 상품 등록
    public void productSave(ProductCreateDTO productCreateDTO, String imagePath) {
        Product product = new Product();
        product.setImage(imagePath);
        product.setTitle(productCreateDTO.getTitle());
        product.setPrice(productCreateDTO.getPrice());
        product.setCompany(productCreateDTO.getCompany());
        product.setManufactureDate(productCreateDTO.getManufactureDate());
        product.setDescription(productCreateDTO.getDescription());

        // 새상품 / 중고상품 설정
        product.setCondition(ProductCategory.fromValue(productCreateDTO.getCondition()));

        // 판매중으로 자동 설정
        product.setStatus(ProductCategory.판매중);

        // 등록 유저 정보 저장
//        product.setMember(member);

        productRepository.save(product);
    }

    // 상품 삭제
    public void productDelete(ProductDeleteDTO productDeleteDTO) {
        Product product = productRepository.findById(productDeleteDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));
        String imagePath = product.getImage();
        if (imagePath != null && imagePath.startsWith("/upload/images/")) {
            try {
                fileService.fileDelete(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 삭제 중 오류 발생", e);
            }
        }
        productRepository.deleteById(product.getId());
    }

    // 상품 검색
    public List<ProductDTO> productSearch(String keyword, List<ProductCategory> conditions, List<ProductCategory> statuses) {
        Pageable pageable = Pageable.unpaged();
        Page<Product> productsPage = (keyword != null && !keyword.trim().isEmpty())
                ? productRepository.findByTitleContainingIgnoreCase(keyword, pageable)
                : productRepository.findAll(pageable);

        return productsPage.getContent().stream()
                .filter(product -> (conditions == null || conditions.contains(product.getCondition())) &&
                        (statuses == null || statuses.contains(product.getStatus())))
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 id 상품 조회
    public Optional<ProductDTO> productFindById(Long id) {
        return productRepository.findById(id).map(ProductDTO::fromEntity);
    }

    // 상품 목록 페이지네이션
    public Page<ProductDTO> productListPagination(String keyword, List<String> condition, List<String> status, Pageable pageable) {
        List<ProductCategory> conditionEnum = condition != null && !condition.isEmpty() ?
                condition.stream().map(ProductCategory::fromValue).collect(Collectors.toList()) :
                null;

        List<ProductCategory> statusEnum = status != null && !status.isEmpty() ?
                status.stream().map(ProductCategory::fromValue).collect(Collectors.toList()) :
                null;

        Page<Product> productPage = productRepository.findByFilters(keyword, conditionEnum, statusEnum, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    // ProductService.java
    public Page<Product> findProducts(String keyword, List<String> conditionStrings, List<String> statusStrings, Pageable pageable) {
        // 문자열 리스트를 ProductCategory Enum 리스트로 변환
        List<ProductCategory> conditions = conditionStrings != null ?
                conditionStrings.stream()
                        .map(str -> {
                            try {
                                return ProductCategory.valueOf(str);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                null;

        List<ProductCategory> statuses = statusStrings != null ?
                statusStrings.stream()
                        .map(str -> {
                            try {
                                return ProductCategory.valueOf(str);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                null;

        return productRepository.findByFilters(keyword, conditions, statuses, pageable);
    }


    // 상품 수정
    public void productUpdate(ProductUpdateDTO productUpdateDTO) {
        Product product = productRepository.findById(productUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        String imagePath = productUpdateDTO.getOriginalImage();
        if (productUpdateDTO.getImage() != null && !productUpdateDTO.getImage().isEmpty()) {
            try {
                fileService.fileDelete(productUpdateDTO.getOriginalImage());
                imagePath = fileService.imageSave(productUpdateDTO.getImage());
            } catch (IOException e) {
                throw new RuntimeException("이미지 처리 중 오류 발생", e);
            }
        }

        product.setImage(imagePath);
        product.setTitle(productUpdateDTO.getTitle());
        product.setPrice(productUpdateDTO.getPrice());
        product.setCompany(productUpdateDTO.getCompany());
        product.setManufactureDate(productUpdateDTO.getManufactureDate());
        product.setDescription(productUpdateDTO.getDescription());

        product.setCondition(ProductCategory.fromValue(productUpdateDTO.getCondition()));

        product.setStatus(ProductCategory.fromValue(productUpdateDTO.getStatus()));

        productRepository.save(product);
    }
}
