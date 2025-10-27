package com.kh.shoppingmall.restcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.AttachmentService;

@CrossOrigin
@RestController
@RequestMapping("/rest/csBoard")
public class CsBoardRestController {
	@Autowired
	private AttachmentService attachmentService;

	//글 등록 전에 미리 이미지를 업로드하는 매핑 (임시이미지)
	@PostMapping("/temp")
	public int temp(@RequestParam MultipartFile attach) throws IllegalStateException, IOException {
		if(attach.isEmpty()) {
			throw new TargetNotfoundException("파일이 없습니다");
		}
		return attachmentService.save(attach);
	}
	@PostMapping("/temps")
	public List<Integer> temps(
			@RequestParam(value = "attach") List<MultipartFile> attachList) throws IllegalStateException, IOException {
		List<Integer> numbers = new ArrayList<>();
		for(MultipartFile attach : attachList) {
			if(attach.isEmpty() == false) {
				int attachmentNo = attachmentService.save(attach);
				numbers.add(attachmentNo);
			}
		}
		return numbers;
	}
	
}
