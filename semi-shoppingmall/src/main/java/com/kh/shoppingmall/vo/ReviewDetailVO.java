package com.kh.shoppingmall.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ReviewDetailVO {

	//1. 기본 정보
	private int reviewNo;
	private int productNo;
	private String reviewContent;
	private int reviewRating;
	private Timestamp reviewCreatedAt;
	
	//2. 멤버 정보
	private String memberId;
	private String memberNickname;
	private String memberProfileName;
	
}
