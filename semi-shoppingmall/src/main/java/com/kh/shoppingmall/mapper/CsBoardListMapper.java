package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.CsBoardListVO;

@Component
public class CsBoardListMapper implements RowMapper<CsBoardListVO>{
	@Override
	public CsBoardListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CsBoardListVO.builder()
				.CsBoardNo(rs.getInt("cs_board_no"))
				.CsBoardTitle(rs.getString("cs_board_title"))
				.CsBoardWriter(rs.getString("cs_board_writer"))
				.CsBoardWtime(rs.getTimestamp("cs_board_wtime"))
				.CsBoardEtime(rs.getTimestamp("cs_board_etime"))
				//컨텐츠 삭제
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
