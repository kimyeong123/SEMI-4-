package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.StatVO;

@Component
public class StatMapper implements RowMapper<StatVO>{

	@Override
	public StatVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		StatVO statVo = new StatVO();
		statVo.setTitle(rs.getString("title"));
		statVo.setValue(rs.getDouble("value"));
		
		return statVo;
	}

	

}
