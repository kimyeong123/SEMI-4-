package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CartDto {
	private int cartNo;
	private String memberId;
	private int productNo;
	private int optionNo;
	private int cartAmount;
	private Timestamp cartCreatedAt;
}
