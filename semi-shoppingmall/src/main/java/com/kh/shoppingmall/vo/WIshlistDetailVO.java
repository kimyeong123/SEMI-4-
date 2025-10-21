package com.kh.shoppingmall.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class WIshlistDetailVO {
	private int wishlistNo;
	private String memberId;
	private Timestamp createdAt;
}
