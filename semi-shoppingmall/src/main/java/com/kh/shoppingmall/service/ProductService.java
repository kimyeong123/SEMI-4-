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
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;

@Service
public class ProductService {

    @Autowired private ProductDao productDao;
    @Autowired private CategoryDao categoryDao;
    @Autowired private ProductOptionDao productOptionDao;
    @Autowired private ProductCategoryMapDao productCategoryMapDao;
    @Autowired private AttachmentDao attachmentDao;
    @Autowired private WishlistDao wishlistDao;
    @Autowired private AttachmentService attachmentService;
    @Autowired private ReviewService reviewService;

    // ================= 상품 등록 =================
    @Transactional
    public int register(ProductDto productDto, List<ProductOptionDto> optionList, List<Integer> categoryNoList,
                        MultipartFile thumbnailFile, List<MultipartFile> detailImageList) throws Exception {

        // 1️⃣ 썸네일 저장
        int thumbnailNo = attachmentService.save(thumbnailFile);
        productDto.setProductThumbnailNo(thumbnailNo);

        // 2️⃣ 상품 등록
        int productNo = productDao.sequence();
        productDto.setProductNo(productNo);
        productDao.insert(productDto);

        // 3️⃣ 옵션 등록
        for (ProductOptionDto option : optionList) {
            option.setProductNo(productNo);
            option.setOptionNo(productOptionDao.sequence());
            productOptionDao.insert(option);
        }

        // 4️⃣ 카테고리 매핑
        for (Integer categoryNo : categoryNoList) {
            productCategoryMapDao.insert(productNo, categoryNo);
        }

        // 5️⃣ 상세 이미지 등록
        for (MultipartFile imageFile : detailImageList) {
            if (imageFile != null && !imageFile.isEmpty()) {
                int attachmentNo = attachmentService.save(imageFile);
                attachmentDao.updateProductNo(attachmentNo, productNo);
            }
        }

        return productNo;
    }

    // ✅ 옵션 리스트 조회
    public List<ProductOptionDto> getOptionsByProduct(int productNo) {
        return productOptionDao.selectListByProduct(productNo);
    }

    // ================= 상품 수정 =================
    @Transactional
    public void update(ProductDto productDto, List<ProductOptionDto> newOptionList, List<Integer> newCategoryNoList,
                       MultipartFile newThumbnailFile, List<MultipartFile> newDetailImageList,
                       List<Integer> deleteAttachmentNoList) throws Exception {

        int productNo = productDto.getProductNo();
        ProductDto current = productDao.selectOne(productNo);
        Integer oldThumb = (current != null) ? current.getProductThumbnailNo() : null;

        // 썸네일 교체
        if (newThumbnailFile != null && !newThumbnailFile.isEmpty()) {
            int newThumb = attachmentService.save(newThumbnailFile);
            productDto.setProductThumbnailNo(newThumb);
            if (oldThumb != null && oldThumb > 0 && !oldThumb.equals(newThumb)) {
                attachmentService.delete(oldThumb);
            }
        }

        // 상품 기본정보 수정
        productDao.update(productDto);

        // 카테고리 매핑 갱신
        List<Integer> oldCats = productCategoryMapDao.selectCategoryNosByProductNo(productNo);
        for (Integer old : oldCats) {
            if (!newCategoryNoList.contains(old)) {
                productCategoryMapDao.delete(productNo, old);
            }
        }
        for (Integer newCat : newCategoryNoList) {
            if (!oldCats.contains(newCat)) {
                productCategoryMapDao.insert(productNo, newCat);
            }
        }

        // 상세 이미지 삭제
        if (deleteAttachmentNoList != null) {
            for (Integer no : deleteAttachmentNoList) {
                attachmentService.delete(no);
            }
        }

        // 새 상세 이미지 등록
        for (MultipartFile f : newDetailImageList) {
            if (f != null && !f.isEmpty()) {
                int attNo = attachmentService.save(f);
                attachmentDao.updateProductNo(attNo, productNo);
            }
        }
    }

    // ================= 상품 조회 =================
    public ProductDto getProduct(int productNo) {
        return productDao.selectOne(productNo);
    }

    public List<ProductDto> getProductList(String column, String keyword) {
        boolean isSearch = column != null && !column.isEmpty() && keyword != null && !keyword.isEmpty();
        return isSearch ? productDao.selectList(column, keyword) : productDao.selectList();
    }

    // ================= 상품 삭제 =================
    @Transactional
    public void delete(int productNo) {

        // 1️⃣ 옵션 삭제
        List<ProductOptionDto> optionList = productOptionDao.selectListByProduct(productNo);
        for (ProductOptionDto opt : optionList) {
            productOptionDao.delete(opt.getOptionNo());
        }

        // 2️⃣ 카테고리 매핑 삭제
        productCategoryMapDao.deleteAllByProductNo(productNo);

        // 3️⃣ 첨부파일 삭제
        List<Integer> attachmentIds = productDao.findDetailAttachments(productNo);
        for (Integer no : attachmentIds) {
            attachmentDao.delete(no);
        }

        // 4️⃣ 리뷰 삭제
        List<Integer> reviewIds = productDao.findReviewsByProduct(productNo);
        for (Integer no : reviewIds) {
            productDao.deleteReview(no);
        }

        // 5️⃣ 위시리스트 삭제
        List<Integer> wishIds = productDao.findWishlistIdsByProduct(productNo);
        for (Integer no : wishIds) {
            productDao.deleteWishlist(no);
        }

        // 6️⃣ 마지막으로 상품 삭제
        productDao.delete(productNo);
    }

    // ================= 위시리스트 개수 =================
    public Map<Integer, Integer> getWishlistCounts() {
        return wishlistDao.selectProductWishlistCounts();
    }

 // ================= 필터 조회 (정렬 기능 추가) =================
    public List<ProductDto> getFilteredProducts(String column, String keyword, Integer categoryNo, String order) {
        List<ProductDto> list;

        if (categoryNo != null) {
            // 1. 카테고리 필터링 로직
            List<Integer> categoryNos = new ArrayList<>();
            categoryNos.add(categoryNo);
            
            // 하위 카테고리 번호 수집
            List<CategoryDto> children = categoryDao.selectChildren(categoryNo);
            for (CategoryDto c : children) {
                categoryNos.add(c.getCategoryNo());
            }
            list = productDao.selectByCategories(categoryNos, column, order); 
            
        } else {
            // 2. 검색/전체 목록 조회 로직
            list = getProductList(column, keyword, order); 
        }

        // 3. 평점 조회 로직
        for (ProductDto p : list) {
            p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo()));
        }
        return list;
    }

    public List<ProductDto> getProductList(String column, String keyword, String order) {
        // 1. 검색 여부 확인
        boolean isSearch = column != null && !column.isEmpty() && keyword != null && !keyword.isEmpty();
        
        if (isSearch) {
            // 2. 검색 시에는 검색 컬럼, 키워드, 정렬 방향을 DAO로 전달
            return productDao.selectList(column, keyword, order);
        } else {
            // 3. 검색이 아닐 경우 (전체 목록)에도 정렬 기준(기본값)과 방향을 DAO로 전달
            // 이 때, column은 정렬 기준으로 사용되므로 "product_no" 같은 기본값을 명시적으로 전달하는 것이 좋습니다.
            return productDao.selectList(null, null, order);
            
            // 참고: 만약 DAO의 selectList(column, keyword) 메서드를 유지하고 싶다면,
            // 해당 메서드가 DAO 내부에서 selectList(column, keyword, order)를 호출하도록 이미 수정했으므로,
            // 기존의 코드를 사용해도 됩니다. (하지만 3개 인자를 받는 핵심 메서드를 직접 호출하는 것이 더 명확합니다.)
        }
    }
}
