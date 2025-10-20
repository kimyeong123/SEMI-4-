package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.ProductDto;

@Component
public class ProductMapper implements RowMapper<ProductDto>{

	@Override
	public ProductDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return ProductDto.builder()
				.productNo(rs.getInt("product_no"))
				.productName(rs.getString("product_name"))
				.productPrice(rs.getInt("product_price"))
				.productContent(rs.getString("product_content"))
				.productThumbnailNo(rs.getInt("product_thumbnail_no"))
			.build();
	}
	
	
}
