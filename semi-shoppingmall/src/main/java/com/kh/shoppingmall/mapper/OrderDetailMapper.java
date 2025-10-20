package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.OrderDetailDto;

@Component
public class OrderDetailMapper implements RowMapper<OrderDetailDto>{

	@Override
	public OrderDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return OrderDetailDto.builder()
				.orderDetailNo(rs.getInt("order_detail_no"))
				.orderNo(rs.getInt("order_no"))
				.productNo(rs.getInt("product_no"))
				.optionNo(rs.getInt("option_no"))
				.orderAmount(rs.getInt("order_amount"))
				.pricePerItem(rs.getInt("price_per_item"))
			.build();
	}
	
}
