package com.kh.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrdersDto {
    private int ordersNo;
    private String ordersId;
    private int ordersTotalPrice;
    private String ordersRecipient;
    private String ordersRecipientContact;
    private String ordersShippingPost, ordersShippingAddress1, ordersShippingAddress2;
    private String ordersStatus;
}
