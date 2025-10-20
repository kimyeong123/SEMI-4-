package com.kh.shoppingmall.dto;

import lombok.Data;

@Data
public class CategoryDto {
	private int categoryNo;
	private String categoryName;
	private int parentCategoryNo;
}
