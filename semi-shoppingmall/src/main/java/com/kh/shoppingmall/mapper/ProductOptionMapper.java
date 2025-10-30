package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.ProductOptionDto;

@Component
public class ProductOptionMapper implements RowMapper<ProductOptionDto> {

    @Override
    public ProductOptionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductOptionDto.builder()
                .optionNo(rs.getInt("option_no"))
                .productNo(rs.getInt("product_no"))   
                .optionName(rs.getString("option_name"))
                .optionStock(rs.getInt("option_stock"))
                .build();
    }
}
