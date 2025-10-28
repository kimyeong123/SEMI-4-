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
										@RequestParam int optionNo,
										@RequestParam(defaultValue = "1") int cartAmount,
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
    
	    cartDto.setOptionNo(optionNo);
	    
	    try {
	        System.out.println("CartDto Log: MemberID=" + cartDto.getMemberId() + 
	                           ", ProductNo=" + cartDto.getProductNo() + 
	                           ", Count=" + cartDto.getCartAmount() +
	                           ", OptionNo=" + cartDto.getOptionNo()); 
	        
	        cartService.addItem(cartDto); 
	        return Map.of("result", true);
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return Map.of("result", false, "error", "장바구니 추가 중 내부 서비스 오류");
	    }
	}

	// 장바구니 수량 변경
	@PostMapping("/update")
	public boolean updateAmount(@ModelAttribute CartDto cartDto, HttpSession session) { 
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
	public boolean removeItem(@RequestParam int productNo, 
	                          @RequestParam int optionNo,  
                              HttpSession session) { 
		
		String memberId = (String) session.getAttribute("loginId");
		if (memberId == null) {
			throw new UnauthorizationException("로그인이 필요합니다.");
		}
		
		try {
			// DAO의 삭제 조건(member_id, product_no, option_no)에 맞추어 DTO 구성
			CartDto cartDto = new CartDto();
			// cartDto.setCartNo(cartNo); // DAO 쿼리가 cartNo를 사용하지 않으므로 제거 (혹은 주석 처리)
			
			cartDto.setMemberId(memberId);
			cartDto.setProductNo(productNo); 
			cartDto.setOptionNo(optionNo);  
			
			// Service 호출
			boolean result = cartService.removeItem(cartDto); 
			
			if (!result) {
				// 삭제할 항목을 찾지 못했을 때 (이미 삭제되었거나 조건 불일치)
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
