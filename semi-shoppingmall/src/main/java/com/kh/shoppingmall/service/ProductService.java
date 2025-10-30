package com.kh.shoppingmall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dao.CategoryDao;
import com.kh.shoppingmall.dao.ProductCategoryMapDao;
import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dao.WishlistDao;
import com.kh.shoppingmall.dao.CartDao; // ✅ 새로 추가 (cart 삭제용)
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ProductOptionDao productOptionDao;
	@Autowired
	private ProductCategoryMapDao productCategoryMapDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private WishlistDao wishlistDao;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private CartDao cartDao; // ✅ 추가

	// ---------------- 상품 등록 (SKU 방식) ----------------
	@Transactional
    public int register(ProductDto productDto, 
                        List<ProductOptionDto> optionList, // (수정) 이게 바로 SKU 목록
                        List<Integer> categoryNoList, 
                        MultipartFile thumbnailFile
    ) throws Exception {

        // 1. 썸네일 저장
        int thumbnailNo = attachmentService.save(thumbnailFile);
        productDto.setProductThumbnailNo(thumbnailNo);

        // 2. 상품 번호 발급 + 등록
        int productNo = productDao.sequence(); // DAO에 sequence()가 있다고 가정
        productDto.setProductNo(productNo);
        productDao.insert(productDto);

        // 3. 옵션(SKU) 등록 (Service는 전달받은 SKU 목록을 그대로 저장)
        if (optionList != null && !optionList.isEmpty()) {
            for (ProductOptionDto sku : optionList) {
                // Controller가 sku.getOptionName() (예: "S / 치즈")과 
                // sku.getOptionStock() (예: 50)을 DTO에 담아서 전달했다고 가정
                
                int optionNo = productOptionDao.sequence(); // DAO에서 시퀀스 받아옴
                sku.setOptionNo(optionNo);
                sku.setProductNo(productNo); // 상품 번호 연결

                productOptionDao.insert(sku); // DAO는 SKU 정보를 그대로 DB에 저장
            }
        }

        // 4. 카테고리 매핑 등록
        if (categoryNoList != null && !categoryNoList.isEmpty()) {
            for (Integer categoryNo : categoryNoList) {
                productCategoryMapDao.insert(productNo, categoryNo);
            }
        }

        return productNo;
    }

	// ---------------- 상품 옵션 조회 ----------------
	public List<ProductOptionDto> getOptionsByProduct(int productNo) {
		return productOptionDao.selectListByProduct(productNo);
	}

	// ---------------- 상품 수정 (SKU 방식) ----------------
	@Transactional
    public void update(
            ProductDto productDto,
            List<ProductOptionDto> newOptionList, // (수정) 새 SKU 목록
            List<Integer> newCategoryNoList,
            MultipartFile newThumbnailFile,
            List<Integer> deleteAttachmentNoList
    ) throws Exception {

        int productNo = productDto.getProductNo();
        
        // 1. 썸네일 교체 로직 (이전 코드 참고)
        ProductDto currentProduct = productDao.selectOne(productNo);
        Integer oldThumbnailNo = (currentProduct != null) ? currentProduct.getProductThumbnailNo() : null;

        if (newThumbnailFile != null && !newThumbnailFile.isEmpty()) {
            int newThumbnailNo = attachmentService.save(newThumbnailFile);
            productDto.setProductThumbnailNo(newThumbnailNo);
            if (oldThumbnailNo != null && oldThumbnailNo > 0 && oldThumbnailNo != newThumbnailNo) {
                attachmentService.delete(oldThumbnailNo);
            }
        }
        productDao.update(productDto); // 2. 상품 기본 정보 업데이트

        // 3. 카테고리 매핑 수정 (기존 로직 사용 가능)
        List<Integer> oldCategoryList = productCategoryMapDao.selectCategoryNosByProductNo(productNo);
        // ... (oldCategoryList와 newCategoryNoList 비교해서 delete/insert) ...
        for (Integer oldCat : oldCategoryList) {
            if (!newCategoryNoList.contains(oldCat)) productCategoryMapDao.delete(productNo, oldCat);
        }
        for (Integer newCat : newCategoryNoList) {
            if (!oldCategoryList.contains(newCat)) productCategoryMapDao.insert(productNo, newCat);
        }

        // 4. 옵션(SKU) 처리 (Delete & Insert 방식)
        productOptionDao.deleteByProduct(productNo); // 1. 해당 상품의 기존 옵션(SKU) 모두 삭제
        
        if (newOptionList != null && !newOptionList.isEmpty()) {
            for (ProductOptionDto sku : newOptionList) { // 2. 새 옵션(SKU) 목록 다시 등록
                int optionNo = productOptionDao.sequence();
                sku.setOptionNo(optionNo);
                sku.setProductNo(productNo);
                productOptionDao.insert(sku);
            }
        }
        
        // 5. 상세 이미지 삭제 처리
        if (deleteAttachmentNoList != null) {
            for (Integer attachmentNo : deleteAttachmentNoList) {
                attachmentService.delete(attachmentNo);
            }
        }
	}

	// ---------------- 상품 단일 조회 ----------------
	public ProductDto getProduct(int productNo) {
		return productDao.selectOne(productNo);
	}

	// ---------------- 상품 삭제 (CASCADE 활용) ----------------
	 @Transactional
	    public void delete(int productNo) {
	        System.out.println("🚨 상품 삭제 시작: productNo = " + productNo);

	        // 1. 삭제할 파일 정보 수집 (썸네일)
	        ProductDto product = productDao.selectOne(productNo);
	        if (product == null) {
	            throw new TargetNotfoundException("상품이 존재하지 않습니다.");
	        }
	        Integer thumbnailNo = product.getProductThumbnailNo();

	        // 2. 삭제할 파일 정보 수집 (상세 이미지)
	        List<AttachmentDto> detailImages = attachmentDao.selectListByProductNo(productNo); // (AttachmentDao에 이 메소드 필요)

	        //(FK_MAP_PRODUCT 제약조건 오류 해결)
	        productCategoryMapDao.deleteByProductNo(productNo); 
	        System.out.println("✅ 카테고리 매핑 삭제 완료.");
	        
	        // ✨ (추가!) (FK_OPTION_PRODUCT 제약조건 오류 해결) ✨
	        productOptionDao.deleteByProduct(productNo);
	        System.out.println("✅ 상품 옵션(SKU) 삭제 완료.");

	        // 4. 상품 삭제 (DB)
	        productDao.delete(productNo);
	        System.out.println("✅ DB 상품 삭제 완료."); // CASCADE로 하위 데이터 자동 삭제 (map 제외)

	        // 5. 썸네일 "물리 파일" 삭제
	        if (thumbnailNo != null && thumbnailNo > 0) {
	            try {
	                attachmentService.delete(thumbnailNo); 
	            } catch (Exception e) {
	                System.err.println("썸네일 파일 삭제 중 오류: " + e.getMessage());
	            }
	        }

	        // 6. 상세 이미지 "물리 파일" 삭제
	        for (AttachmentDto attach : detailImages) {
	            try {
	                attachmentService.delete(attach.getAttachmentNo());
	            } catch (Exception e) {
	                System.err.println("상세 이미지 파일 삭제 중 오류: " + e.getMessage());
	            }
	        }
	        System.out.println("✅ 상품 삭제 전체 완료: productNo = " + productNo);
	    
    }

	// ---------------- 위시리스트 카운트 ----------------
	public Map<Integer, Integer> getWishlistCounts() {
		return wishlistDao.selectProductWishlistCounts();
	}

	// ---------------- 필터 검색 ----------------
	public List<ProductDto> getFilteredProducts(String column, String keyword, Integer categoryNo, String order) {
		List<ProductDto> list;
		if (categoryNo != null) {
			List<Integer> categoryNos = new ArrayList<>();
			categoryNos.add(categoryNo);
			List<CategoryDto> children = categoryDao.selectChildren(categoryNo);
			for (CategoryDto c : children)
				categoryNos.add(c.getCategoryNo());
			list = productDao.selectByCategories(categoryNos, column, order);
		} else {
			list = getProductList(column, keyword, order);
		}

		for (ProductDto p : list)
			p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo()));

		return list;
	}

	public List<ProductDto> getProductList(String column, String keyword, String order) {
		boolean isSearch = column != null && !column.isEmpty() && keyword != null && !keyword.isEmpty();
		if (isSearch)
			return productDao.selectList(column, keyword, order);
		else
			return productDao.selectList(column, null, order);
	}
	
	//컨트롤러가 호출할 SKU 전용 업데이트 메소드
	@Transactional
    public boolean updateOptions(int productNo, List<ProductOptionDto> newOptionList) {
        try {
            // 1. 해당 상품의 기존 옵션(SKU) 모두 삭제
            // (참고: cart, order_detail 등에 ON DELETE CASCADE가 걸려있어야 함)
            productOptionDao.deleteByProduct(productNo);
            
            // 2. 새 옵션(SKU) 목록 다시 등록
            if (newOptionList != null && !newOptionList.isEmpty()) {
                for (ProductOptionDto sku : newOptionList) {
                    // 컨트롤러의 래퍼 클래스(VO)가 폼에서 optionName과 optionStock을 받아옴
                    
                    int optionNo = productOptionDao.sequence(); // 새 시퀀스 발급
                    sku.setOptionNo(optionNo);
                    sku.setProductNo(productNo); // 상품 번호 연결
                    
                    // DAO는 SKU 정보를 (option_no, product_no, option_name, option_stock) 4개 컬럼에 저장
                    productOptionDao.insert(sku); 
                }
            }
            return true; // 성공
        } catch (Exception e) {
            // log.error("옵션 업데이트 중 오류 발생", e); // 실제로는 로깅 필요
            // @Transactional에 의해 롤백됨
            throw new RuntimeException("옵션 업데이트 중 오류가 발생했습니다.", e); // 예외를 던져 롤백 확실히
        }
    }
}
