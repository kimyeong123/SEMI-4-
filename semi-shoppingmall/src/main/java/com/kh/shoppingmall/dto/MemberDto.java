package com.kh.shoppingmall.dto;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = {"memberPw"})
public class MemberDto {
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberBirth;
	private String memberContact;
	private String memberEmail;
	private String memberLevel;
	private int memberPoint;
	private String memberPost, memberAddress1, memberAddress2;
	private Timestamp memberJoin, memberLogin, memberChange;
	private long member_profile_no;
}