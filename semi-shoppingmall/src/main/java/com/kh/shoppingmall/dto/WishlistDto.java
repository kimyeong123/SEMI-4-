package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WishlistDto {
	private int wishlistNo;
	private String memberId;
	private int productNo;
	private Timestamp createdAt;
}
