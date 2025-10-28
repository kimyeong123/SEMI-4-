package com.kh.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequestMapping("/admin/stat")
public class AdminStatController {
	
	@GetMapping("/all")
	public String all() {
		return "/WEB-INF/views/admin/stat/all.jsp";
	}
}
