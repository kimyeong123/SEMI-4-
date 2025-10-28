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
	    
	    // --- CartDto 설정 시작 ---
	    CartDto cartDto = new CartDto();
	    cartDto.setMemberId(memberId);
	    cartDto.setProductNo(productNo);
	    cartDto.setCartAmount(cartAmount);
	    
	    // 💡 하드코딩된 '8' 대신, 사용자가 요청 파라미터로 보낸 optionNo를 사용합니다.
	    if (optionNo == null || optionNo <= 0) {
	        // 옵션 선택이 필수라면 여기서 예외를 발생시킬 수 있습니다.
	        // throw new IllegalArgumentException("상품 옵션이 선택되지 않았습니다."); 
	        
	        // 하지만 일단 요청 파라미터는 받아왔으므로, 옵션이 없는 상품이거나
	        // 옵션 선택이 필수가 아닌 경우를 고려하여, null 또는 0으로 처리될 수 있도록 남겨둡니다.
	        // (참고: 상품에 옵션이 없는 경우, DB 상에서 해당 필드가 0이나 null일 수 있습니다.)
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
	        // 클라이언트(프론트엔드)에서 오류를 확인할 수 있도록 더 자세한 오류 메시지를 반환할 수 있습니다.
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
			// cartDto.getCartNo()가 정말 로그인한 회원의 것인지 확인 필요
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
			// DAO의 삭제 조건(member_id, product_no, option_no)에 맞추어 DTO 구성
			CartDto cartDto = new CartDto();
			// cartDto.setCartNo(cartNo); // DAO 쿼리가 cartNo를 사용하지 않으므로 제거 (혹은 주석 처리)
			
			cartDto.setMemberId(memberId);
			cartDto.setProductNo(productNo); // 👈 수정: 전달받은 productNo 설정
			cartDto.setOptionNo(optionNo);   // 👈 수정: 전달받은 optionNo 설정
			
			// Service 호출 시, DAO가 사용하는 delete(CartDto dto) 메서드 이름과 맞추는 것이 좋습니다.
			// 여기서는 removeItem을 그대로 사용한다고 가정합니다.
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
