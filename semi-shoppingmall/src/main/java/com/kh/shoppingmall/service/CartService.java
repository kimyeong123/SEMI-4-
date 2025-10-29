package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.shoppingmall.dao.CartDao;
import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.vo.CartDetailVO;

@Service
public class CartService {

    @Autowired private CartDao cartDao;
    @Autowired private ProductOptionDao productOptionDao;

    // ✅ 1. 장바구니 추가 (옵션 검증 포함)
    public void addItem(CartDto cartDto) {

        // --- 옵션 존재 여부 검증 ---
        if (cartDto.getOptionNo() == null || cartDto.getOptionNo() <= 0) {
            throw new TargetNotfoundException("유효하지 않은 옵션 번호입니다.");
        }

        // --- 실제 옵션 존재하는지 DB에서 한 번 더 확인 ---
        ProductOptionDto option = null;
        try {
            List<ProductOptionDto> optionList = productOptionDao.selectListByProduct(cartDto.getProductNo());
            option = optionList.stream()
                    .filter(o -> o.getOptionNo() == cartDto.getOptionNo())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new TargetNotfoundException("옵션 조회 중 오류 발생");
        }

        if (option == null) {
            throw new TargetNotfoundException("해당 상품의 옵션 정보를 찾을 수 없습니다.");
        }

        // --- 이미 장바구니에 동일 상품+옵션이 있는지 확인 ---
        CartDto existingItem = cartDao.findItem(
                cartDto.getMemberId(),
                cartDto.getProductNo(),
                cartDto.getOptionNo()
        );

        if (existingItem != null) {
            // ✅ 수량 누적
            int newAmount = existingItem.getCartAmount() + cartDto.getCartAmount();
            existingItem.setCartAmount(newAmount);
            cartDao.update(existingItem); // cart_no 기준으로 업데이트
        } else {
            // ✅ 새 항목 추가
            cartDao.insert(cartDto);
        }
    }

    // ✅ 2. 내 장바구니 목록 조회
    public List<CartDetailVO> getCartItems(String memberId) {
        return cartDao.selectList(memberId); // CartDetailVO 뷰 조회
    }

    // ✅ 3. 장바구니 상품 수량 변경
    public boolean updateItemAmount(CartDto cartDto) {
        return cartDao.update(cartDto);
    }

    // ✅ 4. 장바구니 상품 삭제
    public boolean removeItem(CartDto cartDto) {
        return cartDao.delete(cartDto);
    }

    // ✅ 5. 장바구니 비우기 (주문 완료 시)
    public int clearCart(String memberId) {
        return cartDao.deleteByMemberId(memberId);
    }

    // ✅ 6. cart_no 기준 삭제 (관리자용 or 주문완료용)
    public boolean removeItemByCartNo(int cartNo) {
        return cartDao.deleteByCartNo(cartNo);
    }
}
