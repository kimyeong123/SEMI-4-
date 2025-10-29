package com.kh.shoppingmall.vo;

import java.util.List;
import lombok.Data;

@Data
public class OptionVO {
    private String optionName;
    private List<String> optionValueList;
}
