package com.kh.shoppingmall.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistDetailVO {
    private int wishlistNo;
    private String memberId;
    private Timestamp createdAt;

    private int productNo;
    private String productName;
    private int productPrice;
    private int attachmentNo;
    private String attachmentName;
}
