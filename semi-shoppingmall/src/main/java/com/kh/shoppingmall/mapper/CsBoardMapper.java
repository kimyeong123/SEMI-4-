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
					.csBoardNo(rs.getInt("cs_board_no"))
					.csBoardTitle(rs.getString("cs_board_title"))
					.csBoardWriter(rs.getString("cs_board_writer"))
					.csBoardWtime(rs.getTimestamp("cs_board_wtime"))
					.csBoardEtime(rs.getTimestamp("cs_board_etime"))
					.csBoardContent(rs.getString("cs_board_content"))
//					.csBoardRead(rs.getInt("cs_board_read"))
//					.csBoardLike(rs.getInt("cs_board_like"))
//					.csBoardReply(rs.getInt("cs_board_reply"))
					.csBoardNotice(rs.getString("cs_board_notice"))
					
					.csBoardGroup(rs.getInt("cs_board_group"))
					.csBoardOrigin(rs.getObject("cs_board_origin", Integer.class))
					.csBoardDepth(rs.getInt("cs_board_depth"))
					
					.csBoardSecret(rs.getString("cs_board_secret"))
				.build();
	}
}
