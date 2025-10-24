package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewDto {
	private int reviewNo;
	private int productNo;
	private String memberId;
	private String reviewContent;
	private int reviewRating;
	private Timestamp reviewCreatedAt;
	
	private String memberNickname;
	private String memberProfileName;
}
