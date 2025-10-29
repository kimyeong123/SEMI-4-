package com.kh.shoppingmall.vo;

import java.util.List;

import lombok.Data;

@Data
public class ProductVO {
    private String productName;
    private int productPrice;
    private String productContent;
    private List<OptionVO> optionList;
}
