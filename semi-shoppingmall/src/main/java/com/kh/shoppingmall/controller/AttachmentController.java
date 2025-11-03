package com.kh.shoppingmall.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;
@CrossOrigin
@Controller
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentDao attachmentDao;

    // ✅ [추가] 업로드 처리 (summernote 포함)
    @PostMapping("/upload")
    @ResponseBody
    public List<Integer> upload(@RequestParam("attach") List<MultipartFile> attachList) throws IOException {
        List<Integer> attachmentNoList = new ArrayList<>();
        for (MultipartFile file : attachList) {
            if (file.isEmpty()) continue;
            int attachmentNo = attachmentService.save(file); // ← AttachmentService에서 파일 저장
            attachmentNoList.add(attachmentNo);
        }
        return attachmentNoList; // AJAX 응답용 (JSON 배열)
    }

    // ✅ 다운로드용
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestParam int attachmentNo) throws IOException {
        AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
        if (attachmentDto == null) throw new TargetNotfoundException("존재하지 않는 파일");

        ByteArrayResource resource = attachmentService.load(attachmentNo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                .header(HttpHeaders.CONTENT_TYPE, attachmentDto.getAttachmentType())
                .contentLength(attachmentDto.getAttachmentSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename(attachmentDto.getAttachmentName(), StandardCharsets.UTF_8)
                                .build().toString()
                )
                .body(resource);
    }

    // ✅ 브라우저 표시용
    @GetMapping("/view")
    public ResponseEntity<ByteArrayResource> view(@RequestParam int attachmentNo) throws IOException {
        AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
        if (attachmentDto == null) throw new TargetNotfoundException("존재하지 않는 파일");

        ByteArrayResource resource = attachmentService.load(attachmentNo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                .header(HttpHeaders.CONTENT_TYPE, attachmentDto.getAttachmentType())
                .contentLength(attachmentDto.getAttachmentSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .inline() // 브라우저에 바로 표시
                                .filename(attachmentDto.getAttachmentName(), StandardCharsets.UTF_8)
                                .build().toString()
                )
                .body(resource);
    }
}
