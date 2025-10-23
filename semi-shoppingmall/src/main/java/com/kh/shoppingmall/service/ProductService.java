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
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductOptionDao productOptionDao;
    @Autowired
    private ProductCategoryMapDao productCategoryMapDao;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentDao attachmentDao;

    // ---------------- 상품 등록 ----------------
    @Transactional
    public void register(ProductDto productDto, List<ProductOptionDto> optionList, List<Integer> categoryNoList,
                         MultipartFile thumbnailFile, List<MultipartFile> detailImageList) throws Exception {
        int thumbnailNo = attachmentService.save(thumbnailFile); // 썸네일 저장
        productDto.setProductThumbnailNo(thumbnailNo);

        int productNo = productDao.sequence(); // 상품 번호 시퀀스
        productDto.setProductNo(productNo);
        productDao.insert(productDto); // 상품 저장

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

    // ---------------- 상품 수정 ----------------
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

        productDao.update(productDto); // 상품 정보 수정

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
            if (!newCategoryNoList.contains(oldCat)) productCategoryMapDao.delete(productNo, oldCat);
        }
        for (Integer newCat : newCategoryNoList) {
            if (!oldCategoryList.contains(newCat)) productCategoryMapDao.insert(productNo, newCat);
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

    // ---------------- 특정 상품 조회 ----------------
    public ProductDto getProduct(int productNo) {
        return productDao.selectOne(productNo);
    }

    // ---------------- 상품 목록 조회 (검색 포함) ----------------
    public List<ProductDto> getProductList(String column, String keyword) {
        boolean isSearch = column != null && !column.isEmpty() && keyword != null && !keyword.isEmpty();
        if (isSearch) return productDao.selectList(column, keyword);
        else return productDao.selectList();
    }

    // ---------------- 상품 삭제 ----------------
    @Transactional
    public void delete(int productNo) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        // 썸네일 삭제
        Integer thumbnailNo = productDao.findThumbnail(productNo);
        if (thumbnailNo != null) attachmentService.delete(thumbnailNo);

        // 상세 이미지 삭제
        List<Integer> detailAttachments = productDao.findDetailAttachments(productNo);
        for (Integer attachmentNo : detailAttachments) {
            attachmentService.delete(attachmentNo);
        }

        productDao.delete(productNo); // 상품 삭제
    }

    // ---------------- 상품 평균 평점 업데이트 ----------------
    @Transactional
    public void updateAverageRatingForProduct(int productNo) {
        Double avgRating = productDao.calculateAverageRating(productNo);
        productDao.updateAverageRating(productNo, avgRating);
    }

    // ---------------- 상세 이미지 목록 조회 ----------------
    public List<Integer> getDetailAttachments(int productNo) {
        return productDao.findDetailAttachments(productNo);
    }

    // ---------------- 첨부파일 삭제 ----------------
    @Transactional
    public void deleteAttachment(int attachmentNo) {
        attachmentService.delete(attachmentNo);
    }
}
