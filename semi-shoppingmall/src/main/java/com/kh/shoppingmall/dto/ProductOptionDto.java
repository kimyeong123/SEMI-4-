package com.kh.shoppingmall.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductOptionDto {
	private int optionNo;
	private int productNo;
	private String optionName;
	private String optionValue;
	private int optionStock;
	private List<String> optionValueList;
}
