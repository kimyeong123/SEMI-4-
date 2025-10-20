package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.ProductCategoryDto;

@Component
public class ProductCategoryMapper implements RowMapper<ProductCategoryDto>{

	@Override
	public ProductCategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return ProductCategoryDto.builder()
				.productNo(rs.getInt("product_no"))
				.categoryNo(rs.getInt("categoryNo"))
			.build();
	}

}
