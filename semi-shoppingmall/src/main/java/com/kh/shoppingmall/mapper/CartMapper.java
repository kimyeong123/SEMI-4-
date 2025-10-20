package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.CartDto;

@Component
public class CartMapper implements RowMapper<CartDto> {
	@Override
	public CartDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CartDto.builder()
					.cartNo(rs.getInt("cart_no"))
					.memberId(rs.getString("member_id"))
					.productNo(rs.getInt("product_no"))
					.optionNo(rs.getInt("option_no"))
					.cartAmount(rs.getInt("cart_amount"))
					.cartCreatedAt(rs.getTimestamp("cart_created_at"))
				.build();
	}
}
