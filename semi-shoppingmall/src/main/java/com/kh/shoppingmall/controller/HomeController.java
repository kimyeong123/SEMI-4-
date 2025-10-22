package com.kh.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

		@RequestMapping("/")//가장 짧은 주소 부여
		public String home() {
			return "/WEB-INF/views/home.jsp";
		}
}
