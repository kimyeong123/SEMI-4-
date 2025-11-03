package com.kh.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
@CrossOrigin
@Controller
@RequestMapping("/admin")
public class AdminController {
	@RequestMapping("/home")
	public String home() {
		return "/WEB-INF/views/admin/home.jsp";
	}
}