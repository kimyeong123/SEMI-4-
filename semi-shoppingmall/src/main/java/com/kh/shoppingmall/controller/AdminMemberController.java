package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dao.OrdersDao;
import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.dto.OrdersDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.vo.PageVO;
import com.kh.shoppingmall.vo.ReviewDetailVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {
	@Autowired
	private MemberDao memberDao;

	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ReviewDao reviewDao;
	
	@Autowired
	private OrdersDao ordersDao;
	
	@RequestMapping("/list")
	public String list(Model model, @ModelAttribute(value =  "pageVO") PageVO pageVO)
	{
		model.addAttribute("memberList", memberDao.selectListWithPaging(pageVO));
		pageVO.setDataCount(memberDao.count(pageVO));
//		model.addAttribute("pageVO", pageVO); @ModelAttribute에 value설정 시 생략 가능
		return "/WEB-INF/views/admin/member/list.jsp";
	}
	
//	@RequestMapping("/list")
//	public String list(Model model, 
//			@RequestParam(required = false) String column, 
//			@RequestParam(required = false) String keyword)
//	{
//		List<MemberDto> memberList;
//		boolean isSearch = column != null && keyword != null;
//		if(isSearch)
//		{
//			memberList = memberDao.selectList(column, keyword);
//			model.addAttribute("memberList", memberList);
//		}
//		else
//		{
//			memberList = memberDao.selectList();
//			model.addAttribute("memberList", memberList);
//		}
//		
//		return "/WEB-INF/views/admin/member/list.jsp";
//	}
	
	@RequestMapping("/detail")
	public String detail(@RequestParam String memberId, Model model, HttpSession session) //, HttpSession session
	{
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null)
		{
			throw new TargetNotfoundException("존재하지 않는 멤버");
		}
		model.addAttribute("memberDto", memberDto);

		//  해당 회원의 주문 내역 조회
	    List<OrdersDto> ordersList = ordersDao.selectListByMemberId(memberId);
	    model.addAttribute("ordersList", ordersList);

	    //  해당 회원의 리뷰 내역 조회
	    List<ReviewDetailVO> reviewList = reviewDao.selectDetailListByMember(memberId);
	    model.addAttribute("reviewList", reviewList);

		// 2. 💡 로그인한 관리자 정보 조회 (추가된 로직)
	    String loginId = (String) session.getAttribute("loginId"); // 세션에서 로그인 ID를 가져옴
	    if (loginId != null) {
	        // 로그인 ID로 DB에서 회원 전체 정보(Level 포함)를 다시 조회
	        MemberDto loginUserDto = memberDao.selectOne(loginId); 
	        if (loginUserDto != null) {
	            // JSP에서 'loginUserLevel'이라는 이름으로 레벨을 사용할 수 있도록 모델에 추가
	            model.addAttribute("loginUserLevel	", loginUserDto.getMemberLevel());
	            // 필요한 경우 로그인 사용자 ID도 모델에 다시 추가 (JSP 코드 호환성을 위함)
	            model.addAttribute("loginId", loginId);
	        }
	    }
		
		
//		if(session.getAttribute(memberId) != null) session.removeAttribute(memberId);
//		session.setAttribute("currentId", memberId);
//		model.addAttribute("boardList", boardDao.selectListByMember(memberId));
//		model.addAttribute("buyList", buyDao.selectListByMemberId(memberId));
		return "/WEB-INF/views/admin/member/detail.jsp";
	}
	
	
	@GetMapping("/edit")
	public String edit(@RequestParam String memberId, Model model)
	{
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotfoundException("존재하지 않는 회원 정보");
		model.addAttribute("memberDto", memberDto); //화면전달
		return "/WEB-INF/views/admin/member/edit.jsp";
	}
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute MemberDto memberDto)
	{
		memberDao.updateMemberByAdmin(memberDto);
		//return "/WEB-INF/views/admin/member/detail.jsp";
		return "redirect:detail?memberId="+memberDto.getMemberId();
	}
	
	@GetMapping("/drop") 
	public String drop(@RequestParam String memberId) //HttpSession session
	{
		//String address = "/admin/member/detail?memberId=";
		String address = "redirect:/admin/member/detail?memberId=";
		//String id = (String) session.getAttribute("currentId");
		//String currentAddress = address+id;
		
		String askDrop = "&drop";
		//return "redirect:"+currentAddress+askDrop;
		return address+memberId+askDrop;
	}
	
	@PostMapping("/realDrop")
	public String realDrop(@RequestParam String memberId)
	{
		//String dropId = (String) session.getAttribute("currentId");
		
		//if(dropId == null) return "redirect:/error/all";
		if(memberId == null) return "redirect:/error/all";
		memberDao.delete(memberId);
		try {
			int attchmentNo = memberDao.findAttachment(memberId);
			attachmentService.delete(attchmentNo);
		} catch(Exception e) {}
		//session.removeAttribute("currentId");
		//return "redirect:/admin/member/list";
		return "redirect:list";
	}
	
	
	// 기존의 /drop, /realDrop 대신 단일 POST API를 만듭니다.
	// ⚠️ 반드시 이 컨트롤러는 @RestController 이거나, 메서드에 @ResponseBody가 붙어야 합니다.
	@PostMapping("/delete") // 새로운, 명확한 API 주소
	@ResponseBody // (만약 클래스가 @Controller라면 이 어노테이션을 붙여야 함)
	public boolean adminMemberDelete(@RequestParam String memberId) {
	    
	    // 1. 필요한 권한 검증 로직 (세션 등)을 추가해야 안전합니다. (현재 생략됨)
	    // if (!isAdmin()) return false; 
	    
	    if(memberId == null) {
	        return false; // ID가 없으면 실패 반환
	    }

	    try {
	        // 2. 회원 데이터 삭제
	        memberDao.delete(memberId);
	        
	        // 3. 첨부파일/프로필 삭제 (선택적)
	        try {
	            int attchmentNo = memberDao.findAttachment(memberId);
	            if (attchmentNo > 0) {
	                 attachmentService.delete(attchmentNo);
	            }
	        } catch(Exception e) {
	            // 프로필이 없는 경우는 정상으로 간주하고 로깅만 할 수 있습니다.
	        }
	        
	        return true; // 성공 시 true 반환 (클라이언트의 success(result)로 전달됨)
	        
	    } catch (Exception e) {
	        // DB 오류 등 예외 발생 시
	        return false;
	    }
	}
	
	
	
	
}