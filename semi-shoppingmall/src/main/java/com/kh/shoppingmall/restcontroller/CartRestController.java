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
                                       @RequestParam(required = false) String color,
                                       @RequestParam(required = false) String size,
                                       @RequestParam(required = false) Integer optionNo,
                                       HttpSession session) {
        // 로그인 확인
        Object loginIdObj = session.getAttribute("loginId");
        if (loginIdObj == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }
        String memberId = String.valueOf(loginIdObj);

        try {
            // ✅ 옵션 번호 자동 매핑 (color + size 조합)
            if (optionNo == null && color != null && size != null) {
                optionNo = productOptionDao.findOptionNoByColorAndSize(productNo, color, size);
                if (optionNo == null) {
                    throw new TargetNotfoundException("해당 색상/사이즈 조합의 옵션을 찾을 수 없습니다.");
                }
            }

            if (optionNo == null || optionNo <= 0) {
                throw new NeedPermissionException("옵션 정보가 누락되었습니다.");
            }

            // ✅ CartDto 생성
            CartDto cartDto = new CartDto();
            cartDto.setMemberId(memberId);
            cartDto.setProductNo(productNo);
            cartDto.setOptionNo(optionNo);
            cartDto.setCartAmount(cartAmount);

            System.out.println("[🛒 Cart Add Log]");
            System.out.println("  MemberID = " + memberId);
            System.out.println("  ProductNo = " + productNo);
            System.out.println("  OptionNo = " + optionNo);
            System.out.println("  Amount = " + cartAmount);

            cartService.addItem(cartDto);
            return Map.of("result", true);

        } catch (TargetNotfoundException e) {
            e.printStackTrace();
            return Map.of("result", false, "error", e.getMessage());
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
    public boolean removeItem(@RequestParam int productNo,
                              @RequestParam int optionNo,
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
