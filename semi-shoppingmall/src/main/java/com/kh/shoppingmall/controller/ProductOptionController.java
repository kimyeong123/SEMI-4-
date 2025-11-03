package com.kh.shoppingmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

import java.util.List;
@CrossOrigin
@Controller
@RequestMapping("/admin/product/option")
public class ProductOptionController {

    @Autowired
    private ProductOptionDao productOptionDao;

    @Autowired
    private ProductDao productDao;

//    // ---------------- 옵션 관리 화면 (GET) ----------------
//    @GetMapping("/manage")
//    public String manage(@RequestParam int productNo, Model model) {
//        ProductDto product = productDao.selectOne(productNo);
//        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품입니다.");
//
//        List<ProductOptionDto> optionList = productOptionDao.selectListByProduct(productNo);
//        model.addAttribute("product", product);
//        model.addAttribute("optionList", optionList);
//
//        // JSP 경로
//        return "/WEB-INF/views/admin/option/manage.jsp";
//    }

    // ---------------- 옵션 등록 (POST) ----------------
    @PostMapping("/add")
    public String add(@ModelAttribute ProductOptionDto productOptionDto) {
        int seq = productOptionDao.sequence();
        productOptionDto.setOptionNo(seq);
        productOptionDao.insert(productOptionDto);
        return "redirect:/admin/product/option/manage?productNo=" + productOptionDto.getProductNo();
    }

//    // ---------------- 옵션 수정 (AJAX) ----------------
//    @PostMapping("/edit")
//    @ResponseBody
//    public void edit(@ModelAttribute ProductOptionDto productOptionDto) {
//        boolean result = productOptionDao.update(productOptionDto);
//        if (!result) {
//            throw new TargetNotfoundException("존재하지 않는 옵션 번호");
//        }
//    }
//
//    // ---------------- 옵션 삭제 ----------------
//    @GetMapping("/delete")
//    public String delete(@RequestParam int optionNo, @RequestParam int productNo) {
//        productOptionDao.delete(optionNo);
//        return "redirect:/admin/product/option/manage?productNo=" + productNo;
//    }
}
