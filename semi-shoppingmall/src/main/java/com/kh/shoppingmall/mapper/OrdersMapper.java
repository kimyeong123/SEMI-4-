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
					.ordersTotalPrice(rs.getInt("orders_total_price"))
					.ordersRecipient(rs.getString("orders_recipient"))
					.ordersRecipientContact(rs.getString("orders_recipient_contact"))
					.ordersShippingPost(rs.getString("orders_shipping_post"))
					.ordersShippingAddress1(rs.getString("orders_shipping_address1"))
					.ordersShippingAddress2(rs.getString("orders_shipping_address2"))
					.ordersStatus(rs.getString("orders_status"))
				.build();
	}
}
