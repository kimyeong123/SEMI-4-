package com.kh.shoppingmall.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.BannerDao;
import com.kh.shoppingmall.dto.BannerDto;
import com.kh.shoppingmall.service.AttachmentService;
@CrossOrigin
@Controller
@RequestMapping("/admin/banner")
public class BannerController {

    @Autowired
    private BannerDao bannerDao;

    @Autowired
    private AttachmentService attachmentService;

    // ---------------- 배너 목록 ----------------
    @GetMapping("/list")
    public String list(Model model) {
        List<BannerDto> bannerList = bannerDao.selectList();
        model.addAttribute("bannerList", bannerList);
        return "/WEB-INF/views/admin/banner/list.jsp";
    }

    // ---------------- 배너 등록 폼 ----------------
    @GetMapping("/add")
    public String addForm() {
        return "/WEB-INF/views/admin/banner/add.jsp";
    }

    // ---------------- 배너 등록 처리 (파일 업로드 포함) ----------------
    @PostMapping("/add")
    public String add(@ModelAttribute BannerDto dto,
                      @RequestParam(required = false) MultipartFile attach) throws IOException {

        // 파일이 업로드된 경우
        if (attach != null && !attach.isEmpty()) {
            int attachmentNo = attachmentService.save(attach); // 파일 저장하고 번호 리턴
            dto.setBannerAttachmentNo(attachmentNo);           // 배너 DTO에 연결
        }

        bannerDao.insert(dto);
        return "redirect:/admin/banner/list";
    }

    // ---------------- 배너 삭제 ----------------
    @GetMapping("/delete")
    public String delete(@RequestParam int bannerNo) {
        bannerDao.delete(bannerNo);
        return "redirect:/admin/banner/list";
    }

    // ---------------- 배너 수정 폼 ----------------
    @GetMapping("/edit")
    public String editForm(@RequestParam int bannerNo, Model model) {
        BannerDto bannerDto = bannerDao.selectOne(bannerNo);
        model.addAttribute("bannerDto", bannerDto);
        return "/WEB-INF/views/admin/banner/edit.jsp";
    }

    // ---------------- 배너 수정 처리 (파일 변경 가능) ----------------
    @PostMapping("/edit")
    public String edit(@ModelAttribute BannerDto dto,
                       @RequestParam(required = false) MultipartFile attach) throws IOException {

        // 새 파일을 업로드한 경우 (기존 파일 교체)
        if (attach != null && !attach.isEmpty()) {
            int attachmentNo = attachmentService.save(attach);
            dto.setBannerAttachmentNo(attachmentNo);
        }

        bannerDao.update(dto);
        return "redirect:/admin/banner/list";
    }
}
