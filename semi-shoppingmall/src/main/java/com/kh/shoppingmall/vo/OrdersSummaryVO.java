package com.kh.shoppingmall.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrdersSummaryVO {
	private int ordersNo;
	private String ordersId;
	private int ordersTotalPrice;
	private String ordersRecipient;
	private String ordersRecipientContact;
	private String ordersShippingPost;
	private String ordersShippingAddress1, ordersShippingAddress2;
	private String ordersStatus;
	
	//order_detail 참조
	private int orderAmount;
	private int pricePerItem;
	
	//product 참조
	private int productNo;
	private String productName;
	
	//product_option 참조
	private String optionName;
	
	//썸네일 불러오기
	private String thumbnailName;
	
	private Integer productThumbnailNo;
	
	// 주문 생성 시간
	private Timestamp ordersCreatedAt;
}
