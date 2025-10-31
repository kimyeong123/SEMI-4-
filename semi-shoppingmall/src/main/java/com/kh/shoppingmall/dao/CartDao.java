package com.kh.shoppingmall.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.CartDto;
import com.kh.shoppingmall.mapper.CartDetailMapper;
import com.kh.shoppingmall.mapper.CartMapper;
import com.kh.shoppingmall.vo.CartDetailVO;

@Repository
public class CartDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private CartDetailMapper cartDetailMapper;
	
	
	// ==========================
	// C (등록)
	// ==========================
	public void insert(CartDto cartDto) {
		String sql = "INSERT INTO cart "
				+ "(cart_no, member_id, product_no, option_no, cart_amount) "
				+ "VALUES (cart_seq.nextval, ?, ?, ?, ?)";
		
		Object[] params = {
			cartDto.getMemberId(),
			cartDto.getProductNo(),
			cartDto.getOptionNo(),
			cartDto.getCartAmount()
		};
		
		jdbcTemplate.update(sql, params);
	}
	
	
	// ==========================
	// R (조회)
	// ==========================
	public List<CartDetailVO> selectList(String memberId) {
		String sql = "SELECT * FROM cart_detail WHERE member_id = ?";
		Object[] params = { memberId };
		return jdbcTemplate.query(sql, cartDetailMapper, params);
	}
	
	// 장바구니에 이미 담긴 상품인지 확인
	public CartDto findItem(String memberId, int productNo, Integer optionNo) {
		String sql = "SELECT * FROM cart WHERE member_id = ? AND product_no = ? AND option_no = ?";
		Object[] params = { memberId, productNo, optionNo };
		List<CartDto> list = jdbcTemplate.query(sql, cartMapper, params);
	    return list.isEmpty() ? null : list.get(0);
	}
	
	//본인 확인을 위해 cartNo로 조회하는 메소드 추가
	public CartDto selectOneByCartNo(int cartNo) {
	    String sql = "SELECT * FROM cart WHERE cart_no = ?";
	    Object[] params = { cartNo };
	    List<CartDto> list = jdbcTemplate.query(sql, cartMapper, params);
	    return list.isEmpty() ? null : list.get(0);
	}
	
	
	// ==========================
	// U (수정)
	// ==========================
	public boolean update(CartDto cartDto) {
		String sql = "UPDATE cart SET cart_amount = ? WHERE cart_no = ?";
		Object[] params = {
			cartDto.getCartAmount(),
			cartDto.getCartNo()
		};
		return jdbcTemplate.update(sql, params) > 0;
	}	
	
	
	// ==========================
	// D (삭제)
	// ==========================
	public boolean delete(CartDto cartDto) {
		String sql = "DELETE FROM cart WHERE member_id = ? AND product_no = ? AND option_no = ?";
	    Object[] params = {
	        cartDto.getMemberId(),
	        cartDto.getProductNo(), 
	        cartDto.getOptionNo()
	    };
		return jdbcTemplate.update(sql, params) > 0;
	}

	public int deleteByMemberId(String ordersId) {
		String sql = "DELETE FROM cart WHERE member_id = ?";
		Object[] params = { ordersId };
		return jdbcTemplate.update(sql, params);
	}
	
	public boolean deleteByCartNo(int cartNo) {
	    String sql = "DELETE FROM cart WHERE cart_no = ?";
	    Object[] params = { cartNo };
	    return jdbcTemplate.update(sql, params) > 0;
	}
	
	
	// ✅ ✅ ✅ 추가된 부분 (상품 삭제 시 장바구니 관련 레코드 제거)
	public int deleteByProductNo(int productNo) {
	    String sql = "delelte from cart "
	    		+ "where option_no in "
	    		+ "select option_no FROM product_option WHERE product_no = ? "
	    		+ ")";
	    return jdbcTemplate.update(sql, productNo);
	}

	
	public int deleteByOptionNoList(List<Integer> optionNoList) {
	    if (optionNoList == null || optionNoList.isEmpty()) {
	        return 0; // 삭제할 것이 없음
	    }
	    
	    // 1. IN 절에 들어갈 ? プレースホルダー 생성 (예: "?,?,?")
	    String placeholders = String.join(",", Collections.nCopies(optionNoList.size(), "?"));

	    // 2. SQL 쿼리 생성
	    String sql = "DELETE FROM cart WHERE option_no IN (" + placeholders + ")";
	    
	    // 3. List<Integer>를 Object[] 배열로 변환
	    Object[] params = optionNoList.toArray();
	    
	    return jdbcTemplate.update(sql, params);
	}
}
