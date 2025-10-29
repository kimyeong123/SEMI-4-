package com.kh.shoppingmall.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.NeedPermissionException;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.ProductService;

@RestController
@RequestMapping("/rest/product")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductOptionDao productOptionDao;

    // ---------------- 전체 목록 조회 ----------------
    @GetMapping("/list")
    public List<ProductDto> list(@RequestParam(required = false) String column,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false, defaultValue = "desc") String order) {
        return productService.getProductList(column, keyword, order);
    }

    // ---------------- 특정 상품 조회 ----------------
    @GetMapping("/detail/{productNo}")
    public ProductDto detail(@PathVariable int productNo) {
        ProductDto productDto = productService.getProduct(productNo);
        if (productDto == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");
        return productDto;
    }

    // ---------------- 상품 등록 ----------------
    @PostMapping("/add")
    public ProductDto add(@RequestParam String productName,
                          @RequestParam int productPrice,
                          @RequestParam String productContent,
                          @RequestParam List<Integer> categoryNoList,
                          @RequestParam List<ProductOptionDto> optionList,
                          @RequestParam MultipartFile thumbnailFile) {

        ProductDto productDto = new ProductDto();
        productDto.setProductName(productName);
        productDto.setProductPrice(productPrice);
        productDto.setProductContent(productContent);

        try {
            productService.register(productDto, optionList, categoryNoList, thumbnailFile);
        } catch (Exception e) {
            throw new NeedPermissionException("상품 등록 중 오류 발생: " + e.getMessage());
        }

        return productDto;
    }

    // ---------------- 상품 수정 ----------------
    @PostMapping("/edit/{productNo}")
    public ProductDto edit(@PathVariable int productNo,
                           @RequestParam String productName,
                           @RequestParam int productPrice,
                           @RequestParam String productContent,
                           @RequestParam List<Integer> categoryNoList,
                           @RequestParam List<ProductOptionDto> newOptionList,
                           @RequestParam(required = false) MultipartFile newThumbnailFile,
                           @RequestParam(required = false) List<Integer> deleteAttachmentNoList) {

        ProductDto productDto = productService.getProduct(productNo);
        if (productDto == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        productDto.setProductName(productName);
        productDto.setProductPrice(productPrice);
        productDto.setProductContent(productContent);

        try {
            productService.update(productDto, newOptionList, categoryNoList, newThumbnailFile, deleteAttachmentNoList);
        } catch (Exception e) {
            throw new NeedPermissionException("상품 수정 중 오류 발생: " + e.getMessage());
        }

        return productDto;
    }

    // ---------------- 상품 삭제 ----------------
    @PostMapping("/delete/{productNo}")
    public String delete(@PathVariable int productNo) {
        try {
            productService.delete(productNo);
        } catch (Exception e) {
            throw new NeedPermissionException("상품 삭제 중 오류 발생: " + e.getMessage());
        }
        return "삭제 완료";
    }

    @GetMapping("/children")
    public String getChildOptions(@RequestParam int parentOptionNo) {
        List<ProductOptionDto> list = productOptionDao.selectChildOptions(parentOptionNo);

        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            sb.append("<option disabled>하위 옵션 없음</option>");
        } else {
            for (ProductOptionDto opt : list) {
                sb.append("<option value='").append(opt.getOptionNo()).append("' data-stock='")
                  .append(opt.getOptionStock()).append("'>")
                  .append(opt.getOptionValue());
                if (opt.getOptionStock() <= 0) sb.append(" (품절)");
                sb.append("</option>");
            }
        }
        return sb.toString();
    }
}
