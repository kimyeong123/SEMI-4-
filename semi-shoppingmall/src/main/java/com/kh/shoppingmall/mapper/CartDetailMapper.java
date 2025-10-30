package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.CartDetailVO;

@Component
public class CartDetailMapper implements RowMapper<CartDetailVO>{

	@Override
	public CartDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CartDetailVO.builder()
				.cartNo(rs.getInt("cart_no"))
				.memberId(rs.getString("member_id"))
				.cartAmount(rs.getInt("cart_amount"))
				.productNo(rs.getInt("product_no"))
				.productName(rs.getString("product_name"))
				.productPrice(rs.getInt("product_price"))
				.optionNo(rs.getInt("option_no"))
				.optionName(rs.getString("option_name"))
				.thumbnailName(rs.getString("thumbnail_name"))
				.productThumbnailNo(rs.getInt("product_thumbnail_no"))
			.build();
	}

}
