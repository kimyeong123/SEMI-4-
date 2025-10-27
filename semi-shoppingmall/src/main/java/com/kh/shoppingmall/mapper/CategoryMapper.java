package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.CategoryDto;

@Component
public class CategoryMapper implements RowMapper<CategoryDto> {
	@Override
	public CategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer parentCategoryNo = rs.getObject("parent_category_no", Integer.class);

        return CategoryDto.builder()
                // 모두 소문자 스네이크 케이스로 통일
                .categoryNo(rs.getInt("category_no")) 
                .categoryName(rs.getString("category_name"))
                .parentCategoryNo(parentCategoryNo) 
                .categoryOrder(rs.getInt("category_order"))
            .build();
	}
}