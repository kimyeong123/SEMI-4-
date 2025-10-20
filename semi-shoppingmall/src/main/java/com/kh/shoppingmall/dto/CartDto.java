package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CartDto {
	private int cartNo;
	private String memberId;
	private int productNo;
	private int optionNo;
	private int a;
	private Timestamp createdAt;
}