package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.MemberDto;

@Component
public class MemberMapper implements RowMapper<MemberDto> {
	@Override
	public MemberDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MemberDto.builder()
					.memberId(rs.getString("member_id"))
					.memberPw(rs.getString("member_pw"))
					.memberNickname(rs.getString("member_nickname"))
					.memberBirth(rs.getString("member_birth"))
					.memberContact(rs.getString("member_contact"))
					.memberEmail(rs.getString("member_email"))
					.memberLevel(rs.getString("member_level"))
					.memberPoint(rs.getInt("member_point"))
					.memberPost(rs.getString("member_post"))
					.memberAddress1(rs.getString("member_address1"))
					.memberAddress2(rs.getString("member_address2"))
					.memberJoin(rs.getTimestamp("member_join"))
					.memberLogin(rs.getTimestamp("member_login"))
					.memberChange(rs.getTimestamp("member_change"))
					.member_profile_no(rs.getLong("member_profile_no"))
					.memberName(rs.getString("member_name"))
				.build();
	}
}
