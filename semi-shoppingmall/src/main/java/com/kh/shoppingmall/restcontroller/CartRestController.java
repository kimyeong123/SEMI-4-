package com.kh.shoppingmall.restcontroller;

import java.util.Map;

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

	// 장바구니 추가
	@PostMapping("/add")
	public Map<String, Object> addItem(@RequestParam int productNo, 
	                                   @RequestParam int cartAmount,
	                                   @RequestParam(required = false) Integer optionNo, // 👈 이 부분을 추가했습니다.
	                                   HttpSession session) {
	    Object loginIdObj = session.getAttribute("loginId");
	        
	    if (loginIdObj == null) {
	        throw new UnauthorizationException("로그인이 필요합니다."); 
	    }
	    String memberId = String.valueOf(loginIdObj);
	    
	    CartDto cartDto = new CartDto();
	    cartDto.setMemberId(memberId);
	    cartDto.setProductNo(productNo);
	    cartDto.setCartAmount(cartAmount);
	    
	    if (optionNo == null || optionNo <= 0) {
	        cartDto.setOptionNo(null); 
	    } else {
	        cartDto.setOptionNo(optionNo);
	    }
	    // --- CartDto 설정 끝 ---
	    
	    try {
	        System.out.println("CartDto Log: MemberID=" + cartDto.getMemberId() + 
	                           ", ProductNo=" + cartDto.getProductNo() + 
	                           ", Count=" + cartDto.getCartAmount() +
	                           ", OptionNo=" + cartDto.getOptionNo()); // 👈 로그에 옵션 번호 추가
	        
	        cartService.addItem(cartDto); 
	        return Map.of("result", true);
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return Map.of("result", false, "error", "장바구니 추가 중 내부 서비스 오류");
	    }
	}

	// 장바구니 수량 변경
	@PostMapping("/update")
	public boolean updateAmount(@ModelAttribute CartDto cartDto, HttpSession session) { // cartNo, cartAmount 포함
		String memberId = (String) session.getAttribute("loginId");
		if (memberId == null) {
			throw new UnauthorizationException("로그인이 필요합니다.");
		}
		try {
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

	// 장바구니 삭제
	@PostMapping("/delete")
	public boolean removeItem(@RequestParam int productNo, // 👈 추가: 클라이언트로부터 productNo를 받음
	                          @RequestParam int optionNo,  // 👈 추가: 클라이언트로부터 optionNo를 받음
                              HttpSession session) { 
		
		String memberId = (String) session.getAttribute("loginId");
		if (memberId == null) {
			throw new UnauthorizationException("로그인이 필요합니다.");
		}
		
		try {
			CartDto cartDto = new CartDto();
			
			cartDto.setMemberId(memberId);
			cartDto.setProductNo(productNo); 
			cartDto.setOptionNo(optionNo);   
			
			boolean result = cartService.removeItem(cartDto); 

			if (!result) {
				throw new TargetNotfoundException("해당 장바구니 항목을 찾을 수 없습니다.");
			}
			return true;
		} catch (TargetNotfoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("장바구니 삭제 중 오류가 발생했습니다.");
		}
	}
}