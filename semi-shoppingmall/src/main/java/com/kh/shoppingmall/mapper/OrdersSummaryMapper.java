package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.OrdersSummaryVO;

@Component
public class OrdersSummaryMapper implements RowMapper<OrdersSummaryVO> {
	@Override
	public OrdersSummaryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return OrdersSummaryVO.builder()
					.ordersNo(rs.getInt("orders_no"))
					.ordersId(rs.getString("orders_id"))
					.totalPrice(rs.getInt("total_price"))
					.ordersRecipient(rs.getString("orders_recipient"))
					.ordersRecipientContact(rs.getString("orders_recipient_contact"))
					.ordersShippingPost(rs.getString("orders_shipping_post"))
					.ordersShippingAddress1(rs.getString("orders_shipping_address1"))
					.ordersShippingAddress2(rs.getString("orders_shipping_address2"))
					.ordersStatus(rs.getString("orders_status"))
					.orderAmount(rs.getInt("order_amount"))
					.pricePerItem(rs.getInt("price_per_item"))
					.productNo(rs.getInt("product_no"))
					.productName(rs.getString("product_name"))
					.optionName(rs.getString("option_name"))
					.optionValue(rs.getString("option_value"))
					.thumbnailName(rs.getString("thumbnail_name"))
				.build();
	}
}
