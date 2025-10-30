package com.kh.shoppingmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CartDetailVO {
	private int cartNo;
	private String memberId;
	private int cartAmount;
	private int productNo;
	private String productName;
	private int productPrice;
	private int optionNo;
	private String optionName;
	private String thumbnailName;
	
	private Integer productThumbnailNo;
}
