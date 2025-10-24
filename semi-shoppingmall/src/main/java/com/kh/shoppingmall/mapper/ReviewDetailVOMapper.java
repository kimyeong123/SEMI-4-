package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.ReviewDetailVO;

@Component
public class ReviewDetailVOMapper implements RowMapper<ReviewDetailVO> {

    @Override
    public ReviewDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewDetailVO.builder()
                .reviewNo(rs.getInt("review_no"))
                .productNo(rs.getInt("product_no"))
                .productName(rs.getString("product_name"))       
                .reviewContent(rs.getString("review_content"))
                .reviewRating(rs.getInt("review_rating"))
                .reviewCreatedAt(rs.getTimestamp("review_created_at"))
                .memberId(rs.getString("member_id"))
                .memberNickname(rs.getString("member_nickname"))       
                .memberProfileName(rs.getString("member_profile_name"))
                .build();
    }

}
