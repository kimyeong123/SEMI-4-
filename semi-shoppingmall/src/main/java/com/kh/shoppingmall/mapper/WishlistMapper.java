package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.WishlistDto;

@Component
public class WishlistMapper implements RowMapper<WishlistDto>{

	@Override
	public WishlistDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return WishlistDto.builder()
				.wishlistNo(rs.getInt("wishlist_no"))
				.memberId(rs.getString("member_id"))
				.productNo(rs.getInt("product_no"))
				.createdAt(rs.getTimestamp("created_at"))
			.build();
	}

}
