package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.ProductOptionDto;

@Component
public class ProductOptionMapper implements RowMapper<ProductOptionDto>{

	@Override
	public ProductOptionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		return ProductOptionDto.builder()
				.optionNo(rs.getInt("option_no"))
				.optionName(rs.getString("option_name"))
				.optionValue(rs.getString("option_value"))
				.optionStook(rs.getInt("option_stook"))
			.build();
	}

}
