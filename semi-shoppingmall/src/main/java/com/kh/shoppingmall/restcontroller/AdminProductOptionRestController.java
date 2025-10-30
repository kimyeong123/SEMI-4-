package com.kh.shoppingmall.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.dao.ProductOptionDao; // DAO 주입
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.error.UnauthorizationException;

import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/admin/product/option")
public class AdminProductOptionRestController {

    @Autowired
    private ProductOptionDao productOptionDao;

    //수정
    @PostMapping("/edit")
    public boolean editOption(@ModelAttribute ProductOptionDto dto, HttpSession session) {
        //관리자 권한 확인
        String loginLevel = (String) session.getAttribute("loginLevel");
        if (!"관리자".equals(loginLevel)) {
            throw new UnauthorizationException("권한이 없습니다.");
        }
        
        try {
            boolean result = productOptionDao.update(dto);
            if (!result) {
                throw new TargetNotfoundException("수정할 옵션을 찾지 못했습니다.");
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("옵션 수정 중 오류 발생", e); 
        }
    }

    //삭제
    @PostMapping("/delete")
    public boolean deleteOption(@RequestParam int optionNo, HttpSession session) {
        // (관리자 권한 확인 로직...)
        
        try {
            // ✨ 받은 optionNo를 DAO의 "단일 삭제" 메소드로 바로 전달! ✨
            boolean result = productOptionDao.delete(optionNo); 
            
            if (!result) {
                throw new TargetNotfoundException("삭제할 옵션을 찾지 못했습니다.");
            }
            return true;
        } catch (Exception e) {
            // (DB에 ON DELETE CASCADE가 없어서 cart/order_detail에서 사용 중이면
            //  DataIntegrityViolationException (ORA-02292)이 발생할 수 있음)
            throw new RuntimeException("옵션 삭제 중 오류 발생", e);
        }
    }

}
