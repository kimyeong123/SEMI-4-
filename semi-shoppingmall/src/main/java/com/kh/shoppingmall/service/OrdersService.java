package com.kh.shoppingmall.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.shoppingmall.dao.CartDao;
import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dao.OrderDetailDao;
import com.kh.shoppingmall.dao.OrdersDao;
import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.dto.OrderDetailDto;
import com.kh.shoppingmall.dto.OrdersDto;
import com.kh.shoppingmall.dto.ProductDto;

import jakarta.servlet.http.HttpSession;

@Service
public class OrdersService {

	@Autowired
	private OrderDetailDao orderDetailDao;

	@Autowired
	private OrdersDao ordersDao;

	@Autowired
	private ProductOptionDao productOptionDao;

	@Autowired
	private CartDao cartDao;

	// 장바구니에 담은 제품의 가격이 변동될 경우 다시 조회하는 경우 사용
	@Autowired
	private ProductDao productDao;

	// 포인트 적립이 필요할 경우 사용
	@Autowired
	private MemberDao memberDao;

	// 총 주문 금액 계산
	private int calculateTotalPrice(List<CartDto> cartItems) {
		int totalPrice = 0;

		for (CartDto cartItem : cartItems) {
			ProductDto product = productDao.selectOne(cartItem.getProductNo());

			if (product != null) {
				int itemPrice = product.getProductPrice() * cartItem.getCartAmount();

				totalPrice += itemPrice;
			} else {
				throw new RuntimeException("상품 정보를 찾을 수 없습니다: " + cartItem.getProductNo());
			}
		}

		// 최종 금액 반환
		return totalPrice;
	}

	@Transactional
	public int createOrders(OrdersDto ordersDto, List<CartDto> cartItems, HttpSession session) {

		// 1. ordersNo 생성
		// 주문하는 사람 조회
		String ordersId = (String) session.getAttribute("loginId");
		ordersDto.setOrdersId(ordersId);

		// 주문번호 받아오기
		int ordersNo = ordersDao.sequence();
		ordersDto.setOrdersNo(ordersNo);

		// 주문 상태 설정 (예: '결제완료')
		ordersDto.setOrdersStatus("결제완료");

		// 총 금액 계산 로직 (CartItems 기반으로 계산)
		int totalPrice = calculateTotalPrice(cartItems); // 별도 메소드로 계산
		ordersDto.setOrdersTotalPrice(totalPrice);

		// orders 테이블에 insert
		ordersDao.insert(ordersDto);

		// 2. order_detail 테이블에 상품 정보 insert
		List<OrderDetailDto> orderDetailList = new ArrayList<>();
		for (CartDto cartItem : cartItems) {

			// orderDetailDto 생성
			OrderDetailDto orderDetailDto = new OrderDetailDto();

			// 주문번호 설정
			orderDetailDto.setOrderNo(ordersNo);

			// CartDto의 정보를 OrderDetailDto로 복사
			orderDetailDto.setProductNo(cartItem.getProductNo());
			orderDetailDto.setOptionNo(cartItem.getOptionNo());
			orderDetailDto.setOrderAmount(cartItem.getCartAmount());

			// 주문 당시 가격 설정
			ProductDto product = productDao.selectOne(cartItem.getProductNo()); // 예시: 재조회
			if (product != null) {
				orderDetailDto.setPricePerItem(product.getProductPrice());
			} else {
				throw new RuntimeException("상품 가격 정보를 찾을 수 없습니다: " + cartItem.getProductNo());
			}
			
			// 생성된 시퀀스로 order_detail_no insert
			orderDetailDto.setOrderDetailNo(orderDetailDao.sequence());
		
			// 리스트에 추가
			orderDetailList.add(orderDetailDto);
		}

		// DAO의 batchInsert 호출
		orderDetailDao.batchInsert(orderDetailList);

		// 3. 재고 차감
		for (CartDto cartItem : cartItems) {
			productOptionDao.updateStock(cartItem.getOptionNo(), -cartItem.getCartAmount());
		}

		// 4. 장바구니 비우기
		cartDao.deleteByMemberId(ordersId);

		// 생성된 주문 번호 반환
		return ordersNo; 
	}

}
