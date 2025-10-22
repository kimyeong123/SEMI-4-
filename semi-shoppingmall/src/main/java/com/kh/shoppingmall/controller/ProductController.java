package com.kh.shoppingmall.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.service.ProductService;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.error.TargetNotfoundException;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private AttachmentService attachmentService;

    // 상품 등록 페이지
    @GetMapping("/add")
    public String add() {
        return "/WEB-INF/views/product/add.jsp";
    }

    // 상품 등록 처리
    @PostMapping("/add")
    public String add(
            @ModelAttribute ProductDto productDto,
            @RequestParam List<Integer> categoryNoList,
            @RequestParam List<ProductOptionDto> optionList,
            @RequestParam MultipartFile thumbnailFile,
            @RequestParam List<MultipartFile> detailImageList
    ) throws IllegalStateException, IOException {
        try {
            productService.register(productDto, optionList, categoryNoList, thumbnailFile, detailImageList);
            return "redirect:addFinish";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // 등록 완료 페이지
    @RequestMapping("/addFinish")
    public String addFinish() {
        return "/WEB-INF/views/product/addFinish.jsp";
    }

    // 상품 목록 (검색 포함)
    @RequestMapping("/list")
    public String list(Model model,
            @RequestParam(required = false) String column,
            @RequestParam(required = false) String keyword) {

        boolean isSearch = column != null && keyword != null;

        // ★수정: ProductDto 반환으로 통일
        List<ProductDto> productList = isSearch
                ? productDao.selectList(column, keyword)  // 검색용
                : productDao.selectList();               // 전체 목록

        model.addAttribute("productList", productList);
        return "/WEB-INF/views/product/list.jsp";
    }

    // 상품 상세
    @RequestMapping("/detail")
    public String detail(@RequestParam int productNo, Model model) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) {
            throw new TargetNotfoundException("존재하지 않는 상품 번호");
        }
        model.addAttribute("productDto", productDto);
        return "/WEB-INF/views/product/detail.jsp";
    }

    // 상품 수정 페이지
    @GetMapping("/edit")
    public String edit(@RequestParam int productNo, Model model) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) {
            throw new TargetNotfoundException("존재하지 않는 상품 번호");
        }
        model.addAttribute("productDto", productDto);
        return "/WEB-INF/views/product/edit.jsp";
    }

    // 상품 수정 처리
    @PostMapping("/edit")
    public String edit(
            @ModelAttribute ProductDto productDto,
            @RequestParam List<ProductOptionDto> newOptionList,
            @RequestParam List<Integer> newCategoryNoList,
            @RequestParam(required = false) MultipartFile newThumbnailFile,
            @RequestParam(required = false) List<MultipartFile> newDetailImageList,
            @RequestParam(required = false) List<Integer> deleteAttachmentNoList
    ) throws IllegalStateException, IOException {
        try {
            productService.update(productDto, newOptionList, newCategoryNoList, newThumbnailFile, newDetailImageList, deleteAttachmentNoList);
            return "redirect:detail?productNo=" + productDto.getProductNo();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // 상품 삭제
    @RequestMapping("/delete")
    public String delete(@RequestParam int productNo) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) {
            throw new TargetNotfoundException("존재하지 않는 상품 번호");
        }

        try {
            Integer thumbnailNo = productDao.findThumbnail(productNo);
            if (thumbnailNo != null) attachmentService.delete(thumbnailNo);

            List<Integer> detailAttachmentList = productDao.findDetailAttachments(productNo);
            for (Integer attachmentNo : detailAttachmentList) attachmentService.delete(attachmentNo);

            productDao.delete(productNo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:list";
    }

    // 썸네일 이미지 다운로드
    @GetMapping("/image")
    public String image(@RequestParam int productNo) {
        try {
            Integer attachmentNo = productDao.findThumbnail(productNo);
            if (attachmentNo == null) return "redirect:/images/error/no-image.png";
            return "redirect:/attachment/download?attachmentNo=" + attachmentNo;
        } catch (Exception e) {
            return "redirect:/images/error/no-image.png";
        }
    }
}
