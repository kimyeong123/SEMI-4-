package com.kh.shoppingmall.dto;

import lombok.Data;

@Data
public class OrdersDto {
    private int ordersNo;
    private String ordersId;
    private int ordersTotalPrice;
    private String ordersRecipient;
    private String ordersRecipientContact;
    private String ordersShippingPost, ordersShippingAddress1, ordersShippingAddress2;
    private String ordersStatus;
}
