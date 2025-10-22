package com.kh.shoppingmall.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.error.UnauthorizationException;
import com.kh.shoppingmall.service.CartService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/rest/cart") 
public class CartRestController {

    @Autowired
    private CartService cartService;

    //장바구니 추가
    @PostMapping("/")
    public void addItem(@ModelAttribute CartDto cartDto, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }
        try {
            cartDto.setMemberId(memberId);
            cartService.addItem(cartDto);
        } catch (Exception e) {
            throw new RuntimeException("장바구니 추가 중 오류가 발생했습니다.");
        }
    }

    //장바구니 수량 변경
    @PostMapping("/update")
    public boolean updateAmount(@ModelAttribute CartDto cartDto, HttpSession session) { // cartNo, cartAmount 포함
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }
        try {
            //cartDto.getCartNo()가 정말 로그인한 회원의 것인지 확인 필요
            cartDto.setMemberId(memberId);
            boolean result = cartService.updateItemAmount(cartDto); 
            if (!result) {
                throw new TargetNotfoundException("해당 장바구니 항목을 찾을 수 없습니다.");
            }
            return true;
        } catch (TargetNotfoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("장바구니 수량 변경 중 오류가 발생했습니다.");
        }
    }

    //장바구니 삭제
    @PostMapping("/delete") // 또는 @GetMapping("/delete") 사용 가능
    public boolean removeItem(@RequestParam int cartNo, HttpSession session) { // @RequestParam 사용
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }
        try {
            //cartNo가 정말 로그인한 회원의 것인지 확인 필요
            CartDto cartDto = new CartDto();
            cartDto.setCartNo(cartNo);
            cartDto.setMemberId(memberId);
            boolean result = cartService.removeItem(cartDto); // DTO 방식 사용

            if (!result) {
                throw new TargetNotfoundException("해당 장바구니 항목을 찾을 수 없습니다.");
            }
            return true;
        } catch (TargetNotfoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("장바구니 삭제 중 오류가 발생했습니다.");
        }
    }
}
