package com.kh.shoppingmall.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dao.OrdersDao;
import com.kh.shoppingmall.dto.OrdersDto;
import com.kh.shoppingmall.vo.OrdersSummaryVO;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrdersController {

    @Autowired
    private OrdersDao ordersDao;

    private List<String> statusList = new ArrayList<>(
    	    List.of("결제완료", "배송준비중", "배송중", "배송완료",
    	            "주문취소", "반품완료", "취소요청", "반품요청")
    	);

    // 주문 목록
    @GetMapping("/list")
    public String list(Model model) {
    	List<OrdersSummaryVO> list = ordersDao.selectListAll();
        model.addAttribute("orderList", list);
        model.addAttribute("statusList", statusList);
        return "/WEB-INF/views/admin/orders/list.jsp"; // ✅ 절대경로 지정
    }

    // 상태 변경
    @PostMapping("/update")
    public String update(
            @RequestParam int ordersNo,
            @RequestParam String ordersStatus) {
        boolean result = ordersDao.update(ordersNo, ordersStatus);
        if (!result) {
            System.out.println(" 주문 상태 변경 실패 (ordersNo=" + ordersNo + ")");
        } else {
            System.out.println(" 주문 상태 변경 완료 (ordersNo=" + ordersNo + ", status=" + ordersStatus + ")");
        }
        return "redirect:/admin/orders/list"; // redirect는 그대로
    }
    // 주문 상세
    @GetMapping("/detail")
    public String detail(
            @RequestParam int ordersNo,
            Model model) {
        OrdersDto ordersDto = ordersDao.selectOneByOrderNo(ordersNo);
        if (ordersDto == null) {
            model.addAttribute("error", "해당 주문이 존재하지 않습니다.");
            return "/WEB-INF/views/admin/orders/list.jsp";
        }

        model.addAttribute("ordersDto", ordersDto);
        return "/WEB-INF/views/admin/orders/detail.jsp"; 
    }
}
