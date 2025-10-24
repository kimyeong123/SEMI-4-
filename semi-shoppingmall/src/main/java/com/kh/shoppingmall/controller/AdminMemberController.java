package com.kh.shoppingmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;
//import com.kh.shoppingmall.vo.PageVO;
import com.kh.shoppingmall.vo.PageVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {
	@Autowired
	private MemberDao memberDao;

	@Autowired
	private AttachmentService attachmentService;
	
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
	
	
}