package com.kh.shoppingmall.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.service.WishlistService;
import com.kh.shoppingmall.vo.WishlistDetailVO;

import jakarta.servlet.http.HttpSession;
@CrossOrigin
@Controller
@RequestMapping("/member/wishlist") 
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;
    
    @GetMapping("")
    public String list(HttpSession session, Model model) {
         String currentMemberId = (String) session.getAttribute("loginId");
         if (currentMemberId == null) {
             return "redirect:/login"; // 비회원이면 로그인 페이지로
         }

        List<WishlistDetailVO> wishlist = wishlistService.getWishlistItems(currentMemberId);
        model.addAttribute("wishlist", wishlist);

        return "/WEB-INF/views/member/wishlist.jsp";
    }

    @PostMapping("/add")
    public String addWishlist(HttpSession session, @RequestParam int productNo) {
         String currentMemberId = (String) session.getAttribute("loginId");
         if (currentMemberId == null) return "redirect:/login";

        wishlistService.addItem(currentMemberId, productNo);
        return "redirect:/member/wishlist/";
    }

    @PostMapping("/delete")
    public String deleteWishlist(HttpSession session, @RequestParam int productNo) {
         String currentMemberId = (String) session.getAttribute("loginId");
         if (currentMemberId == null) return "redirect:/login";

        wishlistService.removeItem(currentMemberId, productNo);
        return "redirect:/member/wishlist/";
    }
    
    @PostMapping("/toggle")
    public Map<String, Object> toggleWishlist(@RequestParam int productNo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        String memberId = (String) session.getAttribute("loginId"); // 세션에서 로그인 ID 가져오기
        if (memberId == null) {
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
