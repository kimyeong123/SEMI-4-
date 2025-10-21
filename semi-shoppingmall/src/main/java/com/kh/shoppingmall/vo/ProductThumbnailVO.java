package com.kh.shoppingmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductThumbnailVO {

	// 1. 상품 기본 정보
    private int productNo;
    private String productName;
    private int productPrice;
    private String productContent;
    private Integer productThumbnailNo; // NULL이 가능하면 Integer 사용
    
    //2. attachment 정보
    private int attachmentNo;
    private String attachmentName;
	
}
