package com.kh.shoppingmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.service.CartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@PostMapping("/add")
	public String add(@RequestParam int productNo, 
			@RequestParam int optionNo,
            @RequestParam(defaultValue = "1") int amount,
            HttpSession session
						) {
		
		String memberId = (String) session.getAttribute("loginId");
		
		// 1. 로그인 확인
        if (memberId == null) return "redirect:/member/login";
        
        try {
            //CartDto 생성 및 정보 설정
            CartDto cartDto = new CartDto();
            cartDto.setMemberId(memberId);
            cartDto.setProductNo(productNo);
            // (옵션/수량 설정)
            cartDto.setOptionNo(optionNo);
            cartDto.setCartAmount(amount);

            //CartService 호출
            cartService.addItem(cartDto); // Service가 내부적으로 insert or update 처리


            //장바구니 페이지 또는 상품 상세 페이지로 리다이렉트
            return "redirect:/product/detail?productNo=" + productNo; // 예: 원래 상품 상세 페이지로 돌아가기

        } catch (Exception e) {
             return "redirect:/product/detail?productNo=" + productNo; // 예: 원래 상품 상세 페이지
        }
    }
}
