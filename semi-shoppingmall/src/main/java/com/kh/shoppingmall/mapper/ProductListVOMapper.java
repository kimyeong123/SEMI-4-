package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.ProductListVO;

@Component
public class ProductListVOMapper implements RowMapper<ProductListVO> {

    @Override
    public ProductListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductListVO.builder()
                .productNo(rs.getInt("product_no"))
                .productName(rs.getString("product_name"))
                .productPrice(rs.getInt("product_price"))
                .productContent(rs.getString("product_content"))
                .productThumbnailNo((Integer) rs.getObject("product_thumbnail_no")) // null 가능
                .thumbnailName(rs.getString("thumbnail_name"))
                .categories(rs.getString("categories"))  // LISTAGG 결과 문자열
                .build();
    }
}
