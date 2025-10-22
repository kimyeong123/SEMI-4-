package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.shoppingmall.dao.CategoryDao;
import com.kh.shoppingmall.dao.ProductCategoryMapDao;
import com.kh.shoppingmall.dto.CategoryDto;

@Service
public class CategoryService {
    @Autowired private CategoryDao categoryDao;
    @Autowired private ProductCategoryMapDao productCategoryMapDao;

    // 1. 카테고리 추가 (관리자)
    public void addCategory(CategoryDto categoryDto) {
        categoryDao.insert(categoryDto);
    }

    // 2. 전체 카테고리 목록 조회 (메뉴 구성용)
    public List<CategoryDto> getAllCategories() {
        return categoryDao.selectList();
    }

    // 3. 카테고리 정보 수정 (관리자)
    public boolean updateCategory(CategoryDto categoryDto) {
        return categoryDao.update(categoryDto);
    }

    // 4. 카테고리 삭제 (관리자 - 핵심 로직)
    @Transactional
    public void deleteCategory(int categoryNo) {
        int childCount = categoryDao.countByParent(categoryNo);
        if (childCount > 0) {
            throw new RuntimeException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        int productCount = productCategoryMapDao.countByCategory(categoryNo);
        if (productCount > 0) {
            throw new RuntimeException("해당 카테고리에 속한 상품이 존재하여 삭제할 수 없습니다.");
        }

        boolean deleted = categoryDao.delete(categoryNo);
        if (!deleted) {
            throw new RuntimeException("카테고리 삭제 중 오류가 발생했습니다.");
        }
    }
    
    // 부모 카테고리만 조회하는 메서드 추가
    public List<CategoryDto> getParentCategories() {
        return categoryDao.selectParentCategories();
    }

    // 특정 부모 카테고리에 속한 하위 카테고리 조회
    public List<CategoryDto> getChildrenByParent(int parentCategoryNo) {
        return categoryDao.selectChildren(parentCategoryNo);
    }
}