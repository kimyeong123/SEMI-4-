package com.kh.shoppingmall.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.dao.StatDao;
import com.kh.shoppingmall.vo.StatVO;

@CrossOrigin
@RestController
@RequestMapping("rest/admin/stat")
public class AdminStatRestController {
	
	@Autowired
	private StatDao statDao;
	
	//회원 날짜별 가입자수 확인
	@PostMapping("/join")
	public List<StatVO> join() {
		return statDao.countByMemberJoin();
	}
	
	//많이 구매한 이용자 확인
	@PostMapping("/member")
	public List<StatVO> member(){
		return statDao.countByMemberId();
	}
	
	//많이 구매된 제품 확인
	@PostMapping("/product")
	public List<StatVO> product(){
		return statDao.countByProductNo();
	}
	
	//주문 상태 확인
	@PostMapping("/status")
	public List<StatVO> status(){
		return statDao.countByOrderStatus();
	}
}
