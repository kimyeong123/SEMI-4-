package com.kh.shoppingmall.restcontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.error.NeedPermissionException;
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

    @Autowired
    private ProductOptionDao productOptionDao;

    // ✅ 장바구니 추가 (색상 + 사이즈 지원)
    @PostMapping("/add")
    public Map<String, Object> addItem(@RequestParam int productNo,
                                       @RequestParam int cartAmount,
                                       // ❌ color, size 파라미터 제거
                                       // @RequestParam(required = false) String color,
                                       // @RequestParam(required = false) String size,
                                       @RequestParam int optionNo, // ✨ Integer -> int (필수)
                                       HttpSession session) {
        // 로그인 확인
        Object loginIdObj = session.getAttribute("loginId");
        if (loginIdObj == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }
        String memberId = String.valueOf(loginIdObj);

        try {
            // ❌ 옵션 번호 자동 매핑 로직 (findOptionNoByColorAndSize) 전체 삭제
            
            // ✅ CartDto 생성 (SKU 방식)
            CartDto cartDto = new CartDto();
            cartDto.setMemberId(memberId);
            cartDto.setProductNo(productNo);
            cartDto.setOptionNo(optionNo); // ✨ JSP에서 받은 SKU(조합)의 optionNo
            cartDto.setCartAmount(cartAmount);

            System.out.println("[🛒 Cart Add Log]");
            System.out.println("  MemberID = " + memberId);
            System.out.println("  ProductNo = " + productNo);
            System.out.println("  OptionNo = " + optionNo); // ✨ SKU(조합) 번호
            System.out.println("  Amount = " + cartAmount);

            cartService.addItem(cartDto); // Service는 내부적으로 findItem(memberId, productNo, optionNo) 호출
            return Map.of("result", true);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("result", false, "error", "장바구니 추가 중 내부 오류: " + e.getMessage());
        }
    }

    // ✅ 장바구니 수량 변경
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

    // ✅ 장바구니 삭제
    @PostMapping("/delete")
    public boolean removeItem(@RequestParam int cartNo, // ❌ productNo, optionNo 대신 cartNo 받기
                              HttpSession session) {

        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        try {
            // ✨ CartService의 removeItemByCartNo 호출 (DAO의 deleteByCartNo 사용) ✨
            boolean result = cartService.removeItemByCartNo(cartNo, memberId); // (Service에 이 메소드 추가 권장)

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
