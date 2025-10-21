package com.kh.shoppingmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CartDetailVO {
	private int orderDetailNo;
	private int orderNo;
	private int productNo;
	private int optionNo;
	private int orderAmount;
	private int pricePerItem;
}
