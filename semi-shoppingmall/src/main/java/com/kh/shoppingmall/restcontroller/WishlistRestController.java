package com.kh.shoppingmall.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.error.UnauthorizationException;
import com.kh.shoppingmall.service.WishlistService;
import com.kh.shoppingmall.vo.WishlistDetailVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/wishlist")
public class WishlistRestController {

    @Autowired
    private WishlistService wishlistService;

    // 1. 위시리스트 목록 조회
    @PostMapping("/list")
    public List<WishlistDetailVO> list(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        return wishlistService.getWishlistItems(loginId);
    }

    // 2. 찜 추가
    @PostMapping("/add")
    public boolean add(HttpSession session, @RequestParam int productNo) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        return wishlistService.addItem(loginId, productNo); // 성공 여부 boolean 반환
    }

    // 3. 찜 삭제
    @PostMapping("/delete")
    public boolean delete(HttpSession session, @RequestParam int productNo) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        return wishlistService.removeItem(loginId, productNo); // 성공 여부 boolean 반환
    }

    // 4. 찜 여부 확인 (상품 상세 페이지용)
    @PostMapping("/check")
    public boolean check(HttpSession session, @RequestParam int productNo) {
        String loginId = (String) session.getAttribute("loginId");
        return loginId != null && wishlistService.checkItem(loginId, productNo); // boolean 반환
    }
    
    @PostMapping("/toggle")
    @ResponseBody
    public Map<String,Object> toggleWishlist(@RequestParam int productNo, HttpSession session) {
        Map<String,Object> result = new HashMap<>();
        String memberId = (String) session.getAttribute("loginId");
        if(memberId == null) {
            result.put("wishlisted", false);
            result.put("count", 0);
            result.put("error", "login required");
            return result;
        }	
        boolean wishlisted = wishlistService.toggle(memberId, productNo);
        int count = wishlistService.count(productNo);

        result.put("wishlisted", wishlisted);
        result.put("count", count);
        return result; 
    }
}
