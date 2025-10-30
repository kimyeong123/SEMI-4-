package com.kh.shoppingmall.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderListVO {
	
	private int ordersNo; 
	private Timestamp ordersCreatedAt;
	private int ordersTotalPrice;
	private String ordersStatus;
	private String ordersId;
	
	private String productName;
	
	private Integer orderAmount;
	private Integer pricePerItem;
	private String optionName;
	private String thumbnailName;
	
	private Integer productNo;
	private Integer optionNo;
	private Integer productThumbnailNo;
	
}
