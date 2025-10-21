package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.WishlistDetailVO;


@Component
public class WishlistDetailVOMapper implements RowMapper<WishlistDetailVO>{

	@Override
	public WishlistDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return WishlistDetailVO.builder()
				.wishlistNo(rs.getInt("wishlist_no"))
				.memberId(rs.getString("member_Id"))
				.createdAt(rs.getTimestamp("created_At"))
			.build();
	}
	
}
