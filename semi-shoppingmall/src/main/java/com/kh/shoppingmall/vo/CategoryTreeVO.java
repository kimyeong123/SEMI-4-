// 예시: CategoryTreeVO.java
package com.kh.shoppingmall.vo;

import java.util.ArrayList;
import java.util.List;

import com.kh.shoppingmall.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryTreeVO {
    private int categoryNo;
    private String categoryName;
    private Integer parentCategoryNo; // 부모 번호 (null 가능)
    private List<CategoryTreeVO> children; // 자식 목록

    // DTO를 받아서 VO를 만드는 생성자
    public CategoryTreeVO(CategoryDto categoryDto) {
        this.categoryNo = categoryDto.getCategoryNo();
        this.categoryName = categoryDto.getCategoryName();
        this.parentCategoryNo = categoryDto.getParentCategoryNo();
        this.children = new ArrayList<>(); // 초기화
    }
}