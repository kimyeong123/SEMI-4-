package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.CsBoardDto;

@Component
public class CsBoardMapper implements RowMapper<CsBoardDto> {
	@Override
	public CsBoardDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CsBoardDto.builder()
					.CsBoardNo(rs.getInt("cs_board_no"))
					.CsBoardTitle(rs.getString("cs_board_title"))
					.CsBoardWriter(rs.getString("cs_board_writer"))
					.CsBoardWtime(rs.getTimestamp("cs_board_wtime"))
					.CsBoardEtime(rs.getTimestamp("cs_board_etime"))
					.CsBoardContent(rs.getString("cs_board_content"))
					.CsBoardRead(rs.getInt("cs_board_read"))
					.CsBoardLike(rs.getInt("cs_board_like"))
					.CsBoardReply(rs.getInt("cs_board_reply"))
					.CsBoardNotice(rs.getString("cs_board_notice"))
					
					.CsBoardGroup(rs.getInt("cs_board_group"))
					.CsBoardOrigin(rs.getObject("cs_board_origin", Integer.class))
					.CsBoardDepth(rs.getInt("cs_board_depth"))
				.build();
	}
}
