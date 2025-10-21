package com.kh.shoppingmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrdersVO {
	private int ordersNo;
	private String ordersId;
	private int totalPrice;
	private String ordersRecipient;
	private String ordersRecipientContact;
	private String ordersShippingPost;
	private String ordersShippingAddress1, ordersShippingAddress2;
	private String orders_status;
}
