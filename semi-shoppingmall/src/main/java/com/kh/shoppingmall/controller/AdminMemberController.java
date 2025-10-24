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
	public String detail(@RequestParam String memberId, Model model) //, HttpSession session
	{
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null)
		{
			throw new TargetNotfoundException("존재하지 않는 멤버");
		}
		model.addAttribute("memberDto", memberDto);
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