package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.WishlistDetailVO;

@Component
public class WishlistDetailVOMapper implements RowMapper<WishlistDetailVO> {

    @Override
    public WishlistDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WishlistDetailVO.builder()
                .wishlistNo(rs.getInt("wishlist_no"))
                .memberId(rs.getString("member_id"))
                .createdAt(rs.getTimestamp("created_at"))
                .productNo(rs.getInt("product_no"))
                .productName(rs.getString("product_name"))
                .productPrice(rs.getInt("product_price"))
                .attachmentNo(rs.getInt("attachment_no"))
                .attachmentName(rs.getString("attachment_name"))
                .build();
    }
}
