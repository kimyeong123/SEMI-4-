package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.shoppingmall.dao.BannerDao;
import com.kh.shoppingmall.dto.BannerDto;

@Controller
public class HomeController {

    @Autowired
    private BannerDao bannerDao;

    @RequestMapping("/") // 메인 페이지
    public String home(Model model) {
        // DB에서 배너 목록 불러오기
        List<BannerDto> bannerList = bannerDao.selectList();

        // JSP로 데이터 전달
        model.addAttribute("bannerList", bannerList);

        // JSP 경로 반환
        return "/WEB-INF/views/home.jsp";
    }
}
