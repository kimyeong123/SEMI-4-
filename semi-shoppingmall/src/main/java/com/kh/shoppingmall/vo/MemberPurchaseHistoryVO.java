package com.kh.shoppingmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class MemberPurchaseHistoryVO {

    private String ordersId;
    private int productNo;
    private String productName;
    private int productPrice;
    private String productContent;
    private int productThumbnailNo;
    private int attachmentNo;
    private String attachmentName;
}
