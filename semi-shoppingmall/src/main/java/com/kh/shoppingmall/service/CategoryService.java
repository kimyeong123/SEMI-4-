package com.kh.shoppingmall.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.shoppingmall.dao.CategoryDao;
import com.kh.shoppingmall.dao.ProductCategoryMapDao;
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.vo.CategoryTreeVO;

@Service
public class CategoryService {
    @Autowired 
    private CategoryDao categoryDao;
    @Autowired 
    private ProductCategoryMapDao productCategoryMapDao;


 // 1. 카테고리 추가 (관리자) - 자동 순서 계산 로직 추가
    public void addCategory(CategoryDto categoryDto) {
        
        // 등록하려는 카테고리가 최상위(부모) 카테고리인지 확인 (parentCategoryNo가 null일 경우)
        if (categoryDto.getParentCategoryNo() == null) {
            
            // 1. 현재 DB에 등록된 최상위 카테고리의 최대 순서 번호를 조회
            //    (CategoryDao에 selectMaxCategoryOrder() 메서드가 구현되어 있어야 합니다.)
            int maxOrder = categoryDao.selectMaxCategoryOrder();
            
            // 2. 새로운 카테고리의 순서 번호는 MAX + 1로 자동 지정
            categoryDto.setCategoryOrder(maxOrder + 1);
        }
        
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
    
    // 부모 카테고리만 조회
    public List<CategoryDto> getParentCategories() {
        return categoryDao.selectParentCategories();
    }

    // 특정 부모 카테고리에 속한 하위 카테고리 조회
    public List<CategoryDto> getChildrenByParent(int parentCategoryNo) {
        return categoryDao.selectChildren(parentCategoryNo);
    }
    
    
    // 카테고리 계층 처리 (메뉴 순서 정렬 로직 추가)
    public List<CategoryTreeVO> getCategoryTree(){
    	
    	// 모든 카테고리 호출 (DAO의 selectList 쿼리에 ORDER BY가 적용되어 있어야 함)
    	List<CategoryDto> categoryList = categoryDao.selectList();
    	
    	// 계층 구조로 변환
    	Map<Integer, CategoryTreeVO> menuMap = new HashMap<>(); // 번호로 노드를 빠르게 찾기 위한 Map
    	List<CategoryTreeVO> rootMenu = new ArrayList<>(); // 최상위 메뉴만 담는 리스트
    	
    	// Dto를 VO로 변환
    	for(CategoryDto categoryDto : categoryList) {
    		menuMap.put(categoryDto.getCategoryNo(), new CategoryTreeVO(categoryDto));
    	}
    	
    	// 부모-자식 관계 설정
    	for(CategoryTreeVO menu : menuMap.values()) {
    		Integer parentNo = menu.getParentCategoryNo();
    		
    		// 부모 번호가 없으면 최상위 메뉴에 추가
    		if(parentNo == null || parentNo == 0) {
    			rootMenu.add(menu);
    		}
    		else {
    			CategoryTreeVO parentMenu = menuMap.get(parentNo);
    			if(parentMenu != null) {
    				parentMenu.getChildren().add(menu);
    			}
    		}
    	}
    	
        // 최상위 메뉴를 categoryOrder 기준으로 정렬
        Collections.sort(rootMenu, new Comparator<CategoryTreeVO>() {
            @Override
            public int compare(CategoryTreeVO o1, CategoryTreeVO o2) {
                return Integer.compare(o1.getCategoryDto().getCategoryOrder(), o2.getCategoryDto().getCategoryOrder());
            }
        });
    	
    	// 최상위 메뉴 반환
    	return rootMenu;
    }
}