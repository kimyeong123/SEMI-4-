package com.kh.shoppingmall.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dao.CsBoardDao;
import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dto.CsBoardDto;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.vo.CsBoardListVO;
import com.kh.shoppingmall.vo.PageVO;
import com.kh.shoppingmall.error.NeedPermissionException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/csBoard")
public class CsBoardController {
	
	private final AttachmentService attachmentService;
	@Autowired
	private CsBoardDao csBoardDao;
	@Autowired
	private MemberDao memberDao;
	
	CsBoardController(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}
	
	@RequestMapping("/list")
	public String list(Model model, @ModelAttribute PageVO pageVO) {
		LocalDateTime now = LocalDateTime.now();
		
		
//		boolean isWtimeRecent = ;
		List<CsBoardListVO> csBoardNoticeList = csBoardDao.selectListNotice(pageVO);//공지글
		model.addAttribute("noticeCount", csBoardNoticeList.size());//공지사항 개수를 전달(배경색 칠하기용)
		List<CsBoardListVO> csBoardList = csBoardDao.selectListWithPaging(pageVO);//전체글
		List<CsBoardListVO> result = new ArrayList<>();//합성(공지+전체)
		result.addAll(csBoardNoticeList);
		result.addAll(csBoardList);
		//현재 시간 비교해 플래그 값 설정
		for(CsBoardListVO vo: result) {
			LocalDateTime wTime = vo.getCsBoardWtime().toLocalDateTime();
			vo.setWtimeRecent(ChronoUnit.HOURS.between(wTime, now) < 24);
			
			if(vo.getCsBoardEtime() != null) {
				LocalDateTime eTime = vo.getCsBoardEtime().toLocalDateTime();
				vo.setEtimeRecent(ChronoUnit.HOURS.between(eTime, now) < 24);
			}
			else {
				vo.setEtimeRecent(false);
			}
		}
			
			

		model.addAttribute("csBoardList", result);//검색이든 목록이든 한번에 처리
		
		int dataCount = csBoardDao.count(pageVO);//검색이든 목록이든 카운트를 구해
		pageVO.setDataCount(dataCount);//pageVO에 설정
		
		model.addAttribute("pageVO", pageVO);//전달!
		
		return "/WEB-INF/views/csBoard/list.jsp";
	}
	
//	상세
	@RequestMapping("/detail")
	public String detail(Model model, @RequestParam int csBoardNo, HttpSession session) {
		
		CsBoardDto csBoardDto = csBoardDao.selectOne(csBoardNo);
		if(csBoardDto == null) throw new TargetNotfoundException("존재하지 않는 글 번호");
		model.addAttribute("csBoardDto", csBoardDto);//게시글 정보 첨부

		String loginId = (String) session.getAttribute("loginId");
		String loginLevel = (String) session.getAttribute("loginLevel");
		
		if("Y".equals(csBoardDto.getCsBoardSecret())) {
			boolean isWriter = csBoardDto.getCsBoardWriter() != null && csBoardDto.getCsBoardWriter().equals(loginId);
			boolean isAdmin = "관리자".equals(loginLevel);
			//원글 작성자인지 확인하는 로직
			boolean isOriginWriter = false;
			//현재 글이 답글인 경우만 원글작성자 조회
			if(csBoardDto.getCsBoardDepth() > 0) {
				//csBoardGroup은 원글 번호와 동일(depth는 언제나 1 따라서 최상단 글적은사람이 원글 작성자)
				String originWriterId = csBoardDao.getWriterByBoardNo(csBoardDto.getCsBoardGroup());
				
				if(originWriterId != null && originWriterId.equals(loginId)) {
					isOriginWriter = true;
				}
			}
			
			if(!isWriter && !isAdmin && !isOriginWriter) {
				throw new NeedPermissionException("접근 권한이 없습니다");
			}
		}
		
		if(csBoardDto.getCsBoardDepth() == 0) {
			int replyCount = csBoardDao.countReplies(csBoardDto.getCsBoardGroup());
			boolean hasReply = replyCount > 0;
			//답글 존재여부를 모델에 첨부
			model.addAttribute("hasReply", hasReply);
		}
		
		if(csBoardDto.getCsBoardWriter() != null) {//작성자가 존재한다면(탈퇴하지 않았다면)
			MemberDto memberDto = memberDao.selectOne(csBoardDto.getCsBoardWriter()); 
			model.addAttribute("memberDto", memberDto);//작성자 정보 첨부
		}
		
		return "/WEB-INF/views/csBoard/detail.jsp";
	}
	
//	등록
	@GetMapping("/write")
	public String write(Model model, @RequestParam(required = false) Integer csBoardOrigin) {
		//원글 비밀글 상태 저장 변수
		String parentSecret = "N";
		//답글인지 확인용 변수
		String hasParent = "N";
		
		if(csBoardOrigin != null) {
			CsBoardDto parentDto = csBoardDao.selectOne(csBoardOrigin);
			hasParent = "Y";
			if(parentDto != null) {
				parentSecret = parentDto.getCsBoardSecret();
			}
		}
		
		model.addAttribute("parentSecret", parentSecret);
		model.addAttribute("hasParent", hasParent);
		return "/WEB-INF/views/csBoard/write.jsp";
	}
	//새글과 답글을 구분하여 값을 계산한 뒤 등록
	//→ 새글은 boardOrigin이 null인 경우를 말함
	//		새글일 경우 그룹번호는 글번호로, 상위글번호는 null로, 차수는 0으로 설정
	//→ 답글은 boardOrigin이 null이 아닌 경우를 말함
	//		답글일 경우 그룹번호를 대상글의 그룹번호로, 상위글번호는 대상글의 글번호로, 차수는 대상글의 차수+1로 설정
	@PostMapping("/write")
	public String write(@ModelAttribute CsBoardDto csBoardDto, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");
		csBoardDto.setCsBoardWriter(loginId);
		
		//세션에 있는 등급을 검사해서 관리자가 아닌데 공지사항을 작성하려고 하면 차단 
		//-> 인터셉터를 만들어서 해도 됨
		String loginLevel = (String) session.getAttribute("loginLevel");
//		if(loginLevel.equals("관리자") == false && csBoardDto.getCsBoardNotice().equals("Y"))
		if(!"관리자".equals(loginLevel) && csBoardDto.getCsBoardNotice().equals("Y"))
			throw new NeedPermissionException("공지글을 작성할 권한이 없습니다");
		
		//기존 : 번호를 알아서 만들어서 등록
		//변경 : 번호를 미리 구해와서 boardDto에 합쳐서 등록
		
		int csBoardNo = csBoardDao.getSeq();//번호를 생성해서
		csBoardDto.setCsBoardNo(csBoardNo);//게시글 정보에 합친다
		if(csBoardDto.getCsBoardOrigin() == null) {//새글이라면
			csBoardDto.setCsBoardGroup(csBoardNo);//게시글 그룹번호를 설정한다
			//boardDto.setBoardOrigin(null);
			//boardDto.setBoardDepth(0);
		}
		else {//답글이라면
			CsBoardDto findDto = csBoardDao.selectOne(csBoardDto.getCsBoardOrigin());//상위글정보 조회
			csBoardDto.setCsBoardGroup(findDto.getCsBoardGroup());//대상글의 그룹번호를 그대로 설정
			//boardDto.setBoardOrigin(findDto.getBoardNo());//대상글의 글번호를 그대로 설정(생략가능)
			csBoardDto.setCsBoardDepth(findDto.getCsBoardDepth()+1);//대상글의 차수보다 1 크게 설정
		}
		csBoardDao.insert(csBoardDto);//등록
		//return "redirect:list";
		return "redirect:detail?csBoardNo="+csBoardNo;
	}
	
//	변경된 삭제(글 내부의 이미지를 지운 뒤 글 삭제)
	@RequestMapping("/delete")
	public String delete(@RequestParam int csBoardNo) {
		//글 정보를 불러옴
		CsBoardDto csBoardDto = csBoardDao.selectOne(csBoardNo);
		if(csBoardDto == null) throw new TargetNotfoundException("존재하지 않는 글");
		
		//글 본문에 포함된 모든 <img>를 찾아서 해당하는 이미지의 글번호를 삭제
		//- summernote가 만든 html 형식의 글에서 원하는 항목을 탐색 (Jsoup 사용)
		Document document = Jsoup.parse(csBoardDto.getCsBoardContent());//HTML로 해석해서
		Elements elements = document.select(".custom-image");//<img>를 찾고
		for(Element element : elements) {//하나씩 반복하며
			//파일번호 추출
			//String src = element.attr("src");//src 추출
			//int equal = src.lastIndexOf("=");//=의 위치를 찾아서
			//int attachmentNo = Integer.parseInt(src.substring(equal+1));
			int attachmentNo = Integer.parseInt(element.attr("data-pk"));
			attachmentService.delete(attachmentNo);
		}
		
		//글 삭제
		csBoardDao.delete(csBoardNo);
		
		return "redirect:list";
	}
	//
	@GetMapping("/edit")
	public String edit(@RequestParam int csBoardNo, Model model, HttpSession session) {
	    // 1. 게시글 번호(csBoardNo)를 이용하여 DB에서 해당 게시글 정보를 조회합니다.
	    CsBoardDto csBoardDto = csBoardDao.selectOne(csBoardNo);
		//답글인지 확인용 변수
		String hasParent = "N";		
		
	    // 2. 글이 존재하지 않으면 예외를 발생시킵니다.
	    if(csBoardDto == null) {
	        throw new TargetNotfoundException("존재하지 않는 글입니다.");
	    }
	    
	    if(csBoardDto.getCsBoardDepth() > 0) {
	    	hasParent = "Y";
	    }
	 // 게시글 작성자 ID를 변수에 저장하고 trim() 적용
	    String writerId = csBoardDto.getCsBoardWriter();
	    
	 // 세션 ID를 가져와 trim() 적용
	    String memberId = (String) session.getAttribute("loginId");
	    
	    // 3. 권한 검증 로직
//	    String memberId = (String) session.getAttribute("memberId");
	    if(memberId == null || !memberId.trim().equals(writerId.trim())) {
	    	throw new NeedPermissionException("본인이 작성한 글만 수정 가능합니다");
	    }
	    
	    
	    // 4. 권한 통과 후 조회한 데이터를 모델에 담아 View(수정 폼)로 전달합니다.
	    model.addAttribute("csBoardDto", csBoardDto);

	    //공지사항 및 부모글이 비밀글인지 확인용
//		model.addAttribute("parentSecret", parentSecret);
		model.addAttribute("hasParent", hasParent);
	    
	    // 5. 수정 폼 템플릿 파일(예: csBoard/edit.html)로 이동합니다.
	    return  "/WEB-INF/views/csBoard/edit.jsp"; 
	}
	
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute CsBoardDto csBoardDto) {
		CsBoardDto beforeDto = csBoardDao.selectOne(csBoardDto.getCsBoardNo());
		if(beforeDto == null) throw new TargetNotfoundException("존재하지 않는 글");
		
		//기존 글과 변경전 글의 이미지 번호 구하기 위한 코드
		Set<Integer> before = new HashSet<>();
		Document beforeDocument = Jsoup.parse(beforeDto.getCsBoardContent());
		Elements beforeElements = beforeDocument.select(".custom-image");
		for(Element element : beforeElements) {
			int attachmentNo = Integer.parseInt(element.attr("data-pk"));
			before.add(attachmentNo);
		}
		//기존 글과 변경후 글의 이미지 번호 구하기 위한 코드
		Set<Integer> after = new HashSet<>();
		Document afterDocument = Jsoup.parse(csBoardDto.getCsBoardContent());
		Elements afterElements = afterDocument.select(".custom-image");
		for(Element element : afterElements) {
			int attachmentNo = Integer.parseInt(element.attr("data-pk"));
			after.add(attachmentNo);
		}
		
		//이전 이미지 번호를 구하기(차집합)
		Set<Integer> minus = new HashSet<> (before);
		minus.removeAll(after);
		
		//minus의 내용은 지워버릴 번호들
		for(int attachmentNo : minus) {
			attachmentService.delete(attachmentNo);
		}
		
		
		csBoardDao.update(csBoardDto);
		return "redirect:detail?csboardNo="+csBoardDto.getCsBoardNo();
	}
	
}
