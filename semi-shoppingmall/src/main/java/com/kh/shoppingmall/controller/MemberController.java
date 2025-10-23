package com.kh.shoppingmall.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.CartDao;
import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dao.OrdersDao;
import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dao.WishlistDao;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.service.MemberService;
import com.kh.shoppingmall.vo.CartDetailVO;
import com.kh.shoppingmall.vo.OrdersSummaryVO;
import com.kh.shoppingmall.vo.ReviewDetailVO;
import com.kh.shoppingmall.vo.WishlistDetailVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private WishlistDao wishlistDao;
	@Autowired
	private CartDao cartDao;
	@Autowired
	private OrdersDao ordersDao;
	@Autowired
	private ReviewDao reviewDao;
	
	
	@Autowired
	private MemberService memberService;
	@Autowired
	private AttachmentService attachmentService;
	
	//이메일 인증관련은 아직 구현이 안된 관계로 뺌
	
	@GetMapping("/join")
	public String join() {
		return "/WEB-INF/views/member/join.jsp";
	}
	
	@PostMapping("/join")
	public String join(@ModelAttribute MemberDto memberDto, 
			@RequestParam MultipartFile attach) throws IllegalStateException, IOException 
	{
		memberDao.insert(memberDto);
		if(attach.isEmpty() == false) {
			int attachmentNo = attachmentService.save(attach);
			memberDao.connect(memberDto.getMemberId(), attachmentNo);
		}
	
		return "redirect:joinFinish";
	}
	
	@RequestMapping("/joinFinish")
	public String joinFinish() {
		return "/WEB-INF/views/member/joinFinish.jsp";
	}
	
	//상세페이지는 있어야되는지 없어도 되는지 논의 필요 (본인의 정보를 다른 유저가 보는것(관리자 제외))
	@RequestMapping("/detail")
	public String detail(@RequestParam String memberId, Model model) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) {
			throw new TargetNotfoundException("존재하지 않는 멤버");
		}
		model.addAttribute("memberDto", memberDto);
		
		//리뷰 리스트 정도가 적당해보임
		List<ReviewDetailVO> reviewList = reviewDao.selectDetailListByMember(memberId);
		model.addAttribute("reviewList", reviewList);
		
		return "/WEB-INF/views/member/detail.jsp";
	}
	
	@GetMapping("/login")
	public String login()
	{
		return "/WEB-INF/views/member/login.jsp";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute MemberDto memberDto, HttpSession session) {
		//[1] 전달된 아이디로 데이터베이스의 회원정보를 탐색
		MemberDto findDto = memberDao.selectOne(memberDto.getMemberId());
		if(findDto == null) return "redirect:login?error";//로그인페이지(상대)
		
		//(주의) findDto에는 회원의 모든 정보가 있고 , memberDto에는 아이디랑 비밀번호 뿐이다
		
		//[2] 비밀번호 비교
		boolean isLogin = findDto.getMemberPw().equals(memberDto.getMemberPw());
		
		//[3] 성공 실패에 따라 처리
		if(isLogin)  {
			//로그인 성공 시 HttpSession에 이 사용자가 로그인을 성공했음을 데이터로 저장
			session.setAttribute("loginId", findDto.getMemberId());//아이디
			session.setAttribute("loginLevel", findDto.getMemberLevel());//등급
			
			//로그인 시간 갱신
			memberDao.updateMemberLogin(findDto.getMemberId());
			
			return "redirect:/";//메인페이지
		}
		else {
			return "redirect:/member/login?error";//로그인페이지(절대)
//			return "redirect:login?error";//로그인페이지(상대)
		}
	}
	
	
//	로그아웃 매핑
//	- HttpSession에 저장해둔 데이터를 삭제하는 페이지 (loginId, loginLevel)
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loginId");//loginId라는 이름으로 저장된 값을 지우세요
		session.removeAttribute("loginLevel");//loginLevel이라는 이름으로 저장된 값을 지우세요
		return "redirect:/";
	}
	
//	마이페이지 매핑
	@RequestMapping("/mypage")
	public String mypage(Model model, HttpSession session) {
		//session에서 loginId를 추출하여 정보 조회한 뒤 화면으로 전달
		String loginId = (String) session.getAttribute("loginId");//loginId를 꺼내주세요!
		MemberDto memberDto = memberDao.selectOne(loginId);//로그인된 아이디로 정보 조회
		model.addAttribute("memberDto", memberDto);//화면에 전달

		List<CartDetailVO> cartList = cartDao.selectList(loginId);
		model.addAttribute("cartList", cartList);
		
		List<WishlistDetailVO> wishlistList = wishlistDao.selectDetailListByMemberId(loginId);
		model.addAttribute("wishlistList", wishlistList);
		
		List<OrdersSummaryVO> ordersList = ordersDao.selectSummaryListByMemberId(loginId);
		model.addAttribute("ordersList", ordersList);
		
		List<ReviewDetailVO> reviewList = reviewDao.selectDetailListByMember(loginId);
	    model.addAttribute("reviewList", reviewList);
		
		return "/WEB-INF/views/member/mypage.jsp";
	}
	
//	회원 탈퇴 매핑
	@GetMapping("/drop")
	public String drop() {
		return "/WEB-INF/views/member/drop.jsp";
	}
	@PostMapping("/drop")
	public String drop(HttpSession session, @RequestParam String memberPw) {
		String loginId = (String) session.getAttribute("loginId");//세션의 아이디 정보를 추출
		boolean result = memberService.drop(loginId, memberPw);
		if(result) {//탈퇴 완료
			//로그아웃 처리
			session.removeAttribute("loginId");
			session.removeAttribute("loginLevel");
			return "redirect:goodbye";
		}
		else {//탈퇴 실패(비밀번호 불일치)
			//redirect는 GET으로밖에 보낼 수 없다
			return "redirect:drop?error";
		}
	}
	
	@RequestMapping("/goodbye")
	public String goodbye() {
		return "/WEB-INF/views/member/goodbye.jsp";
	}
	
//	내 정보 수정
	@GetMapping("/edit")
	public String edit(Model model, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");//다운캐스팅
		MemberDto memberDto = memberDao.selectOne(loginId);//정보조회
		model.addAttribute("memberDto", memberDto);//화면으로 전달
		return "/WEB-INF/views/member/edit.jsp";
	}
	@PostMapping("/edit")
	public String edit(@ModelAttribute MemberDto memberDto, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");//다운캐스팅
		MemberDto findDto = memberDao.selectOne(loginId);//정보조회
		boolean isValid = memberDto.getMemberPw().equals(findDto.getMemberPw());//비밀번호 검사
		if(!isValid) {//비밀번호 불일치
			return "redirect:edit?error";
		}
		
		//memberDto 사용 시 (아이디 추가)
		memberDto.setMemberId(loginId);//아이디를 추가 설정해야함
		memberDao.updateMember(memberDto);
		
		//findDto 사용 시 (변경항목을 교체) - 관리자랑 사용자를 통합해서 만들 경우 좋음
		//findDto.setMemberNickname(memberDto.getMemberNickname());
		//findDto.setMemberEmail(memberDto.getMemberEmail());
		//findDto.setMemberBirth(memberDto.getMemberBirth());
		//findDto.setMemberContact(memberDto.getMemberContact());
		//findDto.setMemberPost(memberDto.getMemberPost());
		//findDto.setMemberAddress1(memberDto.getMemberAddress1());
		//findDto.setMemberAddress2(memberDto.getMemberAddress2());
		//memberDao.updateMemberByAdmin(findDto);
		return "redirect:mypage";
	}
	
	
//	비밀번호 변경 매핑
	@GetMapping("/password")
	public String password() {
		return "/WEB-INF/views/member/password.jsp";
	}
	@PostMapping("/password")
	public String password(HttpSession session, 
			@RequestParam String currentPw, @RequestParam String changePw) {
		String loginId = (String) session.getAttribute("loginId");//로그인 아이디 확인
		MemberDto memberDto = memberDao.selectOne(loginId);//DB 테이블정보 조회
		boolean isValid = memberDto.getMemberPw().equals(currentPw);//현재 비밀번호 검사
		if(isValid == false) return "redirect:password?error";
		
		memberDao.updateMemberPw(loginId, changePw);
//		or
//		memberDto.setMemberPw(changePw);
//		memberDao.updateMemberPw(memberDto);
		
		return "redirect:mypage";
	}
	
	//(+추가) 첨부파일을 반환하는 매핑
	@GetMapping("/profile")
	public String profile(@RequestParam String memberId) {
		try {
			int attachmentNo = memberDao.findAttachment(memberId);
			return "redirect:/attachment/download?attachmentNo="+attachmentNo;
		}
		catch(Exception e) {
			return "redirect:/images/error/no-image.png";
		}
	}
	
}
