package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.ProductThumbnailVO;

@Component
public class ProductThumbnailVOMapper implements RowMapper<ProductThumbnailVO> {

    @Override
    public ProductThumbnailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductThumbnailVO.builder()
                .productNo(rs.getInt("product_no"))
                .productName(rs.getString("product_name"))
                .productPrice(rs.getInt("product_price"))
                .productContent(rs.getString("product_content"))
                .productThumbnailNo((Integer) rs.getObject("product_thumbnail_no"))  // null 가능
                .attachmentNo(rs.getInt("attachment_no"))
                .attachmentName(rs.getString("attachment_name"))
                .build();
    }
}
