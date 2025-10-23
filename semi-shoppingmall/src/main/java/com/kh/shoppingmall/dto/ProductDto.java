package com.kh.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductDto {
    private int productNo;
    private String productName;
    private int productPrice;
    private String productContent;
    private Integer productThumbnailNo; 
    private Integer parentCategoryNo; 
    private Integer childCategoryNo;  
}
