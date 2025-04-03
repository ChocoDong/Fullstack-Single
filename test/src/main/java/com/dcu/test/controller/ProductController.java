package com.dcu.test.controller;

import com.dcu.test.dto.ProductCreateDTO;
import com.dcu.test.dto.ProductDTO;
import com.dcu.test.dto.ProductDeleteDTO;
import com.dcu.test.dto.ProductUpdateDTO;
import com.dcu.test.entity.Member;
import com.dcu.test.entity.Product;
import com.dcu.test.service.FileService;
import com.dcu.test.service.MemberService;
import com.dcu.test.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final MemberService memberService;
    private final FileService fileService;

    // 상품 목록 페이지
    @GetMapping({"/", "/productList"})
    public String productList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) List<String> condition,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 6);

        // 필터 값이 null일 경우 빈 리스트로 처리 (NPE 방지)
        List<String> safeCondition = (condition != null) ? condition : Collections.emptyList();
        List<String> safeStatus = (status != null) ? status : Collections.emptyList();

        // 필터 조건 적용하여 상품 목록 조회
        Page<ProductDTO> productPage = productService.productListPagination(keyword, safeCondition, safeStatus, pageable);

        // 모델에 데이터 추가
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        // 필터 목록 추가
        model.addAttribute("conditionList", List.of("새상품", "중고상품"));
        model.addAttribute("statusList", List.of("판매중", "판매완료"));
        model.addAttribute("selectedCondition", safeCondition);
        model.addAttribute("selectedStatus", safeStatus);

        return "product/productList"; // 템플릿 경로 확인
    }

    @GetMapping("/products")
    public Page<Product> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> condition,
            @RequestParam(required = false) List<String> status,
            Pageable pageable) {

        return productService.findProducts(keyword, condition, status, pageable);
    }

    // 상품 등록 페이지
    @GetMapping("/productRegister")
    public String productRegister() {
        return "product/productRegistration";
    }

    //상품 등록 처리
    @PostMapping("/productRegistration")
    public String productRegister(@ModelAttribute ProductCreateDTO productCreateDTO) throws IOException {
        String imagePath = fileService.imageSave(productCreateDTO.getImage());
        productService.productSave(productCreateDTO, imagePath);
        return "redirect:/productList";
    }
//    @PostMapping("/productRegistration")
//    public String productRegister(@ModelAttribute ProductCreateDTO productCreateDTO) throws IOException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            return "redirect:/memberLogin";
//        }
//        Member member = (Member) authentication.getPrincipal();
//
//        String imagePath = fileService.imageSave(productCreateDTO.getImage());
//        productService.productSave(productCreateDTO, imagePath, member); // 유저 정보도 함께 저장
//
//        return "redirect:/productList";
//    }


    // 상품 상세 페이지 (현재 로그인한 유저 정보 추가)
    @GetMapping("/productDetail/{id}")
    public String productDetail(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal Member member) { // 현재 로그인한 유저 정보
        Optional<ProductDTO> product = productService.productFindById(id);

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("currentUserId", member != null ? member.getId() : null); // 현재 로그인한 유저 ID 전달
            return "product/productDetail";
        } else {
            return "redirect:/productList";
        }
    }


    // ✅ 상품 수정 페이지
    @GetMapping("/productEdit/{id}")
    public String productEdit(@PathVariable Long id, Model model) {
        Optional<ProductDTO> product = productService.productFindById(id);

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product/productEdit";
        } else {
            return "redirect:/productList";
        }
    }

    // 상품 수정 처리
    @PostMapping("/productEdit")
    public String productEdit(@ModelAttribute ProductUpdateDTO productUpdateDTO) {
        productService.productUpdate(productUpdateDTO);
        return "redirect:/productDetail/" + productUpdateDTO.getId();
    }

    // 상품 삭제 처리
    @PostMapping("/productDelete")
    public String productDelete(@ModelAttribute ProductDeleteDTO productDeleteDTO) {
        productService.productDelete(productDeleteDTO);
        return "redirect:/productList";
    }
}
