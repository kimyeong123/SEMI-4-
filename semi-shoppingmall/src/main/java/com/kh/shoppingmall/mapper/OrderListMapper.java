package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.OrderListVO;

@Component
public class OrderListMapper implements RowMapper<OrderListVO>{

	@Override
	public OrderListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return OrderListVO.builder()
				.ordersNo(rs.getInt("orders_no"))
				.ordersCreatedAt(rs.getTimestamp("orders_created_at"))
				.ordersTotalPrice(rs.getInt("orders_totalprice"))
				.ordersStatus(rs.getString("orders_status"))
				.ordersId(rs.getString("orders_id"))
				.productName(rs.getString("product_name"))
				.orderAmount(rs.getInt("order_amount"))
				.pricePerItem(rs.getInt("price_per_item"))
				.optionName(rs.getString("option_name"))
				.thumbnailName(rs.getString("thumbnail_name"))
				.productNo(rs.getInt("product_no"))
				.optionNo(rs.getInt("option_no"))
				.productThumbnailNo(rs.getInt("product_thumbnail_no"))
			.build(); 
	}
	
}
