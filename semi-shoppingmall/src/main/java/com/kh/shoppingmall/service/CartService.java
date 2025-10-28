package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.shoppingmall.dao.CartDao;
import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.vo.CartDetailVO;

@Service
public class CartService {
    @Autowired private CartDao cartDao;

    // 1. 장바구니 추가 (핵심 로직)
    public void addItem(CartDto cartDto) {
        // 이미 담긴 상품인지 확인
        CartDto existingItem = cartDao.findItem(
        		cartDto.getMemberId(), 
        		cartDto.getProductNo(), 
        		cartDto.getOptionNo()
        	);

        if (existingItem != null) {
            // 이미 있으면: 수량만 업데이트
            int newAmount = existingItem.getCartAmount() + cartDto.getCartAmount();
            existingItem.setCartAmount(newAmount);
            cartDao.update(existingItem); // cart_no 기준으로 업데이트하는 DAO 메소드
        } else {
            // 없으면: 새로 추가
            cartDao.insert(cartDto);
        }
    }

    // 2. 내 장바구니 목록 조회
    public List<CartDetailVO> getCartItems(String memberId) {
        return cartDao.selectList(memberId); // 뷰(View)를 조회하는 DAO 메소드
    }

    // 3. 장바구니 상품 수량 변경
    public boolean updateItemAmount(CartDto cartDto) {
        return cartDao.update(cartDto); // cart_no 기준으로 업데이트
    }

    // 4. 장바구니 상품 삭제
    public boolean removeItem(CartDto cartDto) {
        return cartDao.delete(cartDto); // member_id와 option_no 기준 또는 cart_no 기준
    }

    // 5. 장바구니 비우기 (주문 완료 시)
    public int clearCart(String memberId) {
        return cartDao.deleteByMemberId(memberId);
    }
    
    public boolean removeItemByCartNo(int cartNo) {
        // (CartDao에도 deleteByCartNo(int cartNo) 메소드가 추가되어야 함)
        return cartDao.deleteByCartNo(cartNo); 
    }
}