// 예시: CategoryTreeVO.java
package com.kh.shoppingmall.vo;

import java.util.ArrayList;
import java.util.List;

import com.kh.shoppingmall.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//com.kh.shoppingmall.vo.CategoryTreeVO.java
//... (기존 import 및 @Data 등 Lombok 어노테이션)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeVO {
	private CategoryDto categoryDto;
	private int categoryNo;
	private String categoryName;
	private Integer parentCategoryNo; // 부모 번호 (null 가능)
	private List<CategoryTreeVO> children; // 자식 목록

	// (이 메서드는 유지합니다.)
	public CategoryDto getCategoryDto() {
		return categoryDto;
	}

	// ⭐ DTO를 받아서 VO를 만드는 생성자 (수정된 부분) ⭐
	public CategoryTreeVO(CategoryDto categoryDto) {
		// 1. CategoryDto 객체 자체를 필드에 저장합니다. (필수!)
		this.categoryDto = categoryDto;

		// 2. 나머지 필드는 DTO에서 복사하거나, categoryDto 필드를 통해 간접 접근할 수 있으므로 제거 가능합니다.
		// (여기서는 DTO에 있는 값을 한 번 더 복사하는 대신, DTO를 통해 접근하도록 권장합니다.)
		this.categoryNo = categoryDto.getCategoryNo();
		this.categoryName = categoryDto.getCategoryName();
		this.parentCategoryNo = categoryDto.getParentCategoryNo();

		this.children = new ArrayList<>(); // 초기화
	}
}