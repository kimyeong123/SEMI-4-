package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AttachmentDto {
	private int attachmentNo;
	private String attachmentName;
	private String attachmentType;
	private long attachmentSize;
	private Timestamp attachmentTime;
	private int productNo;
	private int reviewNo;
}