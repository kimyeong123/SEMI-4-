package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.dto.OrdersDto;

@Component
public class OrdersMapper implements RowMapper<OrdersDto> {
	@Override
	public OrdersDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return OrdersDto.builder()
					.ordersNo(rs.getInt("orders_no"))
					.ordersId(rs.getString("orders_id"))
					.ordersTotalPrice(rs.getInt("orders_totalprice"))
					.ordersRecipient(rs.getString("orders_recipient"))
					.ordersRecipientContact(rs.getString("orders_recipientcontact"))
					.ordersShippingPost(rs.getString("orders_shippingpost"))
					.ordersShippingAddress1(rs.getString("orders_shippingaddress1"))
					.ordersShippingAddress2(rs.getString("orders_shippingaddress2"))
					.ordersStatus(rs.getString("orders_status"))
					.ordersCreatedAt(rs.getTimestamp("orders_created_at"))
				.build();
	}
}
