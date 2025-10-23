package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.service.WishlistService;
import com.kh.shoppingmall.vo.WishlistDetailVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/wishlist") 
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;
    
    @GetMapping("")
    public String list(HttpSession session, Model model) {
        // String currentMemberId = (String) session.getAttribute("loginId");
        // if (currentMemberId == null) {
        //     return "redirect:/login"; // 비회원이면 로그인 페이지로
        // }

        // 임시로 테스트용 memberId 설정
        String currentMemberId = "testmember1";

        List<WishlistDetailVO> wishlist = wishlistService.getWishlistItems(currentMemberId);
        model.addAttribute("wishlist", wishlist);

        return "/WEB-INF/views/member/wishlist.jsp";
    }

    @PostMapping("/add")
    public String addWishlist(HttpSession session, @RequestParam int productNo) {
        // String currentMemberId = (String) session.getAttribute("loginId");
        // if (currentMemberId == null) return "redirect:/login";

        String currentMemberId = "testMember"; // 테스트용

        wishlistService.addItem(currentMemberId, productNo);
        return "redirect:/member/wishlist/";
    }

    @PostMapping("/delete")
    public String deleteWishlist(HttpSession session, @RequestParam int productNo) {
        // String currentMemberId = (String) session.getAttribute("loginId");
        // if (currentMemberId == null) return "redirect:/login";

        String currentMemberId = "testMember"; // 테스트용

        wishlistService.removeItem(currentMemberId, productNo);
        return "redirect:/member/wishlist/";
    }
}
