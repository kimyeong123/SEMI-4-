package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.ReviewDto;

@Component
public class ReviewMapper implements RowMapper<ReviewDto>{

	@Override
	public ReviewDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		return ReviewDto.builder()
				.reviewNo(rs.getInt("review_no"))
				.productNo(rs.getInt("product_no"))
				.memberId(rs.getString("member_id"))
				.reviewContent(rs.getString("review_content"))
				.reviewRating(rs.getInt("review_rating"))
				.reviewCreatedAt(rs.getTimestamp("review_created_at"))
			.build();
	}

}
