package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dao.ProductCategoryMapDao;
import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductOptionDao productOptionDao;
    @Autowired
    private ProductCategoryMapDao productCategoryMapDao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private AttachmentService attachmentService;

    // ================= 상품 등록 =================
    @Transactional
    public void register(ProductDto productDto, List<ProductOptionDto> optionList, List<Integer> categoryNoList,
                         MultipartFile thumbnailFile, List<MultipartFile> detailImageList) throws Exception {

        int thumbnailNo = attachmentService.save(thumbnailFile);
        productDto.setProductThumbnailNo(thumbnailNo);

        int productNo = productDao.sequence();
        productDto.setProductNo(productNo);
        productDao.insert(productDto);

        // 옵션 저장
        for (ProductOptionDto option : optionList) {
            option.setProductNo(productNo);
            option.setOptionNo(productOptionDao.sequence());
            productOptionDao.insert(option);
        }

        // 카테고리 매핑 저장
        for (Integer categoryNo : categoryNoList) {
            productCategoryMapDao.insert(productNo, categoryNo);
        }

        // 상세 이미지 저장
        for (MultipartFile imageFile : detailImageList) {
            int attachmentNo = attachmentService.save(imageFile);
            attachmentDao.updateProductNo(attachmentNo, productNo);
        }
    }

    // ================= 상품 수정 =================
    @Transactional
    public void update(ProductDto productDto, List<ProductOptionDto> newOptionList, List<Integer> newCategoryNoList,
                       MultipartFile newThumbnailFile, List<MultipartFile> newDetailImageList,
                       List<Integer> deleteAttachmentNoList) throws Exception {

        int productNo = productDto.getProductNo();

        // 기존 썸네일 조회
        ProductDto currentProduct = productDao.selectOne(productNo);
        Integer oldThumbnailNo = (currentProduct != null) ? currentProduct.getProductThumbnailNo() : null;

        // 썸네일 교체
        if (newThumbnailFile != null && !newThumbnailFile.isEmpty()) {
            int newThumbnailNo = attachmentService.save(newThumbnailFile);
            productDto.setProductThumbnailNo(newThumbnailNo);

            if (oldThumbnailNo != null && oldThumbnailNo > 0 && oldThumbnailNo != newThumbnailNo) {
                attachmentService.delete(oldThumbnailNo);
            }
        }

        productDao.update(productDto);

        // 옵션 처리
        List<ProductOptionDto> oldOptionList = productOptionDao.selectListByProduct(productNo);
        for (ProductOptionDto oldOption : oldOptionList) {
            boolean exists = newOptionList.stream()
                    .anyMatch(newOption -> newOption.getOptionNo() == oldOption.getOptionNo());
            if (!exists) productOptionDao.delete(oldOption.getOptionNo());
        }

        for (ProductOptionDto newOption : newOptionList) {
            newOption.setProductNo(productNo);
            if (newOption.getOptionNo() == 0) {
                productOptionDao.insert(newOption);
            } else {
                productOptionDao.update(newOption);
            }
        }

        // 카테고리 처리
        List<Integer> oldCategoryList = productCategoryMapDao.selectCategoryNosByProductNo(productNo);
        for (Integer oldCat : oldCategoryList) {
            if (!newCategoryNoList.contains(oldCat))
                productCategoryMapDao.delete(productNo, oldCat);
        }
        for (Integer newCat : newCategoryNoList) {
            if (!oldCategoryList.contains(newCat))
                productCategoryMapDao.insert(productNo, newCat);
        }

        // 삭제할 상세 이미지 처리
        if (deleteAttachmentNoList != null) {
            for (Integer attachmentNo : deleteAttachmentNoList) {
                attachmentService.delete(attachmentNo);
            }
        }

        // 새 상세 이미지 저장
        for (MultipartFile imageFile : newDetailImageList) {
            int attachmentNo = attachmentService.save(imageFile);
            attachmentDao.updateProductNo(attachmentNo, productNo);
        }
    }

    // ================= 특정 상품 조회 =================
    public ProductDto getProduct(int productNo) {
        return productDao.selectOne(productNo);
    }

    // ================= 상품 목록 조회 =================
    public List<ProductDto> getProductList(String column, String keyword) {
        boolean isSearch = column != null && !column.isEmpty() && keyword != null && !keyword.isEmpty();
        return isSearch ? productDao.selectList(column, keyword) : productDao.selectList();
    }

    // ================= 상품 삭제 =================
    @Transactional
    public void delete(int productNo) {
        // 첨부파일 삭제
        List<Integer> attachmentIds = productDao.findDetailAttachments(productNo);
        for (Integer attachmentNo : attachmentIds) {
            attachmentDao.delete(attachmentNo);
        }

        // 리뷰 삭제
        List<Integer> reviewIds = productDao.findReviewsByProduct(productNo);
        for (Integer reviewNo : reviewIds) {
            productDao.deleteReview(reviewNo);
        }

        // 위시리스트 삭제
        List<Integer> wishlistIds = productDao.findWishlistIdsByProduct(productNo);
        for (Integer wishlistNo : wishlistIds) {
            productDao.deleteWishlist(wishlistNo);
        }

        // 상품 삭제
        productDao.delete(productNo);
    }

    // ================= 평점 갱신 =================
    public void refreshAvgRating(int productNo) {
        Double avg = reviewDao.selectAverageRating(productNo);
        productDao.updateAverageRating(productNo, avg != null ? avg : 0.0);
    }
}
