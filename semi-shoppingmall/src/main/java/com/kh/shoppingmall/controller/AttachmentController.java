package com.kh.shoppingmall.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;

@Controller
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentDao attachmentDao;

    // 다운로드용
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

    // 브라우저 표시용
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
                                .inline()  // 브라우저 화면에 표시
                                .filename(attachmentDto.getAttachmentName(), StandardCharsets.UTF_8)
                                .build().toString()
                )
                .body(resource);
    }
}
