package com.kh.shoppingmall.service;

import java.util.List;

import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dao.ProductCategoryMapDao;
import com.kh.shoppingmall.dao.AttachmentDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
    // 상품 등록
    @Transactional
    public void register(
        ProductDto productDto,
        List<ProductOptionDto> optionList,
        List<Integer> categoryNoList,
        MultipartFile thumbnailFile,
        List<MultipartFile> detailImageList
    ) throws Exception {
        // 1. 썸네일 저장
        int thumbnailNo = attachmentService.save(thumbnailFile);
        productDto.setProductThumbnailNo(thumbnailNo);

        // 2. 상품 저장
        productDao.insert(productDto); // productNo 생성됨
        int productNo = productDto.getProductNo();

        // 3. 옵션 저장
        for (ProductOptionDto option : optionList) {
            option.setProductNo(productNo);
            productOptionDao.insert(option);
        }

        // 4. 카테고리 매핑 저장
        for (Integer categoryNo : categoryNoList) {
            productCategoryMapDao.insert(productNo, categoryNo);
        }

        // 5. 상세 이미지 저장
        for (MultipartFile imageFile : detailImageList) {
            int attachmentNo = attachmentService.save(imageFile);
            attachmentDao.updateProductNo(attachmentNo, productNo);
        }
    }

    // 상품 수정
    @Transactional
    public void update(
        ProductDto productDto,
        List<ProductOptionDto> newOptionList,
        List<Integer> newCategoryNoList,
        MultipartFile newThumbnailFile,
        List<MultipartFile> newDetailImageList,
        List<Integer> deleteAttachmentNoList
    ) throws Exception {
        int productNo = productDto.getProductNo();

        // 1. 썸네일 수정 (파일 있으면 교체)
        if (newThumbnailFile != null && newThumbnailFile.isEmpty() == false) {
            int newThumbnailNo = attachmentService.save(newThumbnailFile);
            productDto.setProductThumbnailNo(newThumbnailNo);
        }

        // 2. 상품 정보 수정
        productDao.update(productDto);

        // 3. 옵션 처리
        List<ProductOptionDto> oldOptionList = productOptionDao.selectListByProduct(productNo);

        // 기존 옵션 중 삭제할 것 찾아서 삭제
        for (ProductOptionDto oldOption : oldOptionList) {
            boolean exists = false;
            for (ProductOptionDto newOption : newOptionList) {
                if (oldOption.getOptionNo() == newOption.getOptionNo()) {
                    exists = true;
                    break;
                }
            }
            if (exists == false) {
                productOptionDao.delete(oldOption.getOptionNo());
            }
        }

        // 새 옵션들 insert or update
        for (ProductOptionDto newOption : newOptionList) {
            newOption.setProductNo(productNo);
            if (newOption.getOptionNo() == 0) {
                productOptionDao.insert(newOption);
            } else {
                productOptionDao.update(newOption);
            }
        }

        // 4. 카테고리 처리
        List<Integer> oldCategoryList = productCategoryMapDao.selectCategoryNosByProductNo(productNo);

        // 삭제할 카테고리
        for (Integer oldCat : oldCategoryList) {
            if (newCategoryNoList.contains(oldCat) == false) {
                productCategoryMapDao.delete(productNo, oldCat);
            }
        }

        // 추가할 카테고리
        for (Integer newCat : newCategoryNoList) {
            if (oldCategoryList.contains(newCat) == false) {
                productCategoryMapDao.insert(productNo, newCat);
            }
        }

        // 5. 상세 이미지 삭제 처리
        if (deleteAttachmentNoList != null) {
            for (Integer attachmentNo : deleteAttachmentNoList) {
                attachmentService.delete(attachmentNo);
            }
        }

        // 6. 상세 이미지 새로 저장
        for (MultipartFile imageFile : newDetailImageList) {
            int attachmentNo = attachmentService.save(imageFile);
            attachmentDao.updateProductNo(attachmentNo, productNo);
        }
    }
    
    @Transactional
    public void updateAverageRatingForProduct(int productNo) {
        // 1. 해당 상품에 대한 리뷰 평점의 평균을 계산 (DAO)
        Double avgRating = productDao.calculateAverageRating(productNo);
        
        // 2. 계산된 평균 평점을 상품 정보에 업데이트 (DAO)
        // null이 나오면 0으로 처리하거나, DB에서 null을 허용하는 방식에 따라 처리
        productDao.updateAverageRating(productNo, avgRating);
    }
}
