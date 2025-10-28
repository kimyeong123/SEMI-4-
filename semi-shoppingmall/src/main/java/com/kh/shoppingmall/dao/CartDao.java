package com.kh.shoppingmall.dao;

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
	
	//C
//	public int sequence() {
//		String sql = "select cart_seq.nextval from dual";
//		return  jdbcTemplate.queryForObject(sql, int.class);
//	}
	
	public void insert(CartDto cartDto) {
		String sql = "insert into cart "
				+ "(cart_no, member_id, product_no, option_no, cart_amount) "
				+ "values "
				+ "(cart_seq.nextval, ?, ?, ?, ?) ";
		
		Object[] params = {
			cartDto.getMemberId(),cartDto.getProductNo(),
			cartDto.getOptionNo(),cartDto.getCartAmount()
			
		};
		
		jdbcTemplate.update(sql, params);
	}
	
	//R
	public List<CartDetailVO> selectList(String memberId){
		String sql = "select * from cart_detail where member_id=?";
		
		Object[] params = { memberId };
		
		return jdbcTemplate.query(sql, cartDetailMapper, params);
	}
	
	// 장바구니에 이미 담긴 상품인지 확인하는 메소드
	public CartDto findItem(String memberId, int productNo, Integer optionNo) {
		String sql = "select * from cart where member_id = ? and product_no = ? and option_no = ?";
		Object[] params = {memberId, productNo, optionNo};
		List<CartDto> list = jdbcTemplate.query(sql, cartMapper, params);
	    return list.isEmpty() ? null : list.get(0);
	}
	
	//U
	public boolean update(CartDto cartDto) {
		String sql ="update cart set "
				+ "cart_amount = ? "
				+ "where cart_no = ?";
		
		Object[] params = {
			cartDto.getCartAmount(),
			cartDto.getCartNo()
		};
		
		return jdbcTemplate.update(sql, params) > 0;
	}	
	
	//D
	public boolean delete(CartDto cartDto) {
		String sql = "delete from cart where member_id = ? and product_no = ? and option_no = ?";
	    Object[] params = {
	            cartDto.getMemberId(),
	            cartDto.getProductNo(), 
	            cartDto.getOptionNo()
	    };
		
		return jdbcTemplate.update(sql, params) > 0;
	}

	public int deleteByMemberId(String ordersId) {
		String sql = "delete from cart where member_id=?";
		
		Object[] params = { ordersId };
		
		//삭제된 행 수를 반환
		return jdbcTemplate.update(sql, params);
	}
	
	public boolean deleteByCartNo(int cartNo) {
	    String sql = "delete from cart where cart_no = ?";
	    Object[] params = { cartNo };
	    return jdbcTemplate.update(sql, params) > 0;
	}
}





