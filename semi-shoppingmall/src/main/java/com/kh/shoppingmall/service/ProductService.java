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

    // ---------------- 상품 등록 ----------------
    @Transactional
    public int register(ProductDto productDto, List<ProductOptionDto> optionList,
                        List<Integer> categoryNoList, MultipartFile thumbnailFile) throws Exception {

        // ① 썸네일 저장
        int thumbnailNo = attachmentService.save(thumbnailFile);
        productDto.setProductThumbnailNo(thumbnailNo);

        // ② 상품 번호 발급 + 등록
        int productNo = productDao.sequence();
        productDto.setProductNo(productNo);
        productDao.insert(productDto);
        System.out.println("✅ [Product 등록 완료] productNo = " + productNo);

        // ③ 옵션 등록
        if (optionList != null && !optionList.isEmpty()) {
            for (ProductOptionDto option : optionList) {
                String name = option.getOptionName();
                List<String> values = option.getOptionValueList();

                if (name == null || name.trim().isEmpty()) continue;
                if (values == null || values.isEmpty()) continue;

                for (String val : values) {
                    if (val == null || val.trim().isEmpty()) continue;
                    int optionNo = productOptionDao.sequence();

                    ProductOptionDto dto = ProductOptionDto.builder()
                            .optionNo(optionNo)
                            .productNo(productNo)
                            .optionName(name)
                            .optionValue(val)
                            .optionStock(0) // 기본 재고 0
                            .build();

                    productOptionDao.insert(dto);
                    System.out.println("↳ 옵션 등록 완료: " + name + " - " + val);
                }
            }
        } else {
            System.out.println("⚠️ 옵션 없음 (optionList == null 또는 비어 있음)");
        }

        // ④ 카테고리 매핑 등록
        if (categoryNoList != null && !categoryNoList.isEmpty()) {
            for (Integer categoryNo : categoryNoList) {
                if (categoryNo != null) {
                    productCategoryMapDao.insert(productNo, categoryNo);
                    System.out.println("✅ 카테고리 매핑 등록 완료: " + categoryNo);
                }
            }
        }

        System.out.println("✅ 상품 등록 전체 완료!");
        return productNo;
    }

    // ---------------- 상품 옵션 조회 ----------------
    public List<ProductOptionDto> getOptionsByProduct(int productNo) {
        return productOptionDao.selectListByProduct(productNo);
    }

    // ---------------- 상품 수정 ----------------
    @Transactional
    public void update(ProductDto productDto, List<ProductOptionDto> newOptionList,
                       List<Integer> newCategoryNoList, MultipartFile newThumbnailFile,
                       List<Integer> deleteAttachmentNoList) throws Exception {

        int productNo = productDto.getProductNo();
        ProductDto current = productDao.selectOne(productNo);
        Integer oldThumb = (current != null) ? current.getProductThumbnailNo() : null;

        // 썸네일 교체
        if (newThumbnailFile != null && !newThumbnailFile.isEmpty()) {
            int newThumb = attachmentService.save(newThumbnailFile);
            productDto.setProductThumbnailNo(newThumb);
            productDao.update(productDto);
            if (oldThumb != null && oldThumb > 0 && !oldThumb.equals(newThumb)) {
                attachmentService.delete(oldThumb);
            }
        } else {
            productDao.update(productDto);
        }

        // 카테고리 매핑 수정
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

        // 첨부파일 삭제
        if (deleteAttachmentNoList != null) {
            for (Integer no : deleteAttachmentNoList) {
                attachmentService.delete(no);
            }
        }
    }

    // ---------------- 상품 단일 조회 ----------------
    public ProductDto getProduct(int productNo) {
        return productDao.selectOne(productNo);
    }

    // ---------------- 상품 삭제 ----------------
    @Transactional
    public void delete(int productNo) {
        productCategoryMapDao.deleteAllByProductNo(productNo);
        productDao.delete(productNo);
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
            for (CategoryDto c : children) categoryNos.add(c.getCategoryNo());
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
}
