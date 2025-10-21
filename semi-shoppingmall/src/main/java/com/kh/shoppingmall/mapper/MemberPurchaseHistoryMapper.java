package com.kh.shoppingmall.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.shoppingmall.vo.MemberPurchaseHistoryVO;

@Component
public class MemberPurchaseHistoryMapper implements RowMapper<MemberPurchaseHistoryVO> {
	@Override
	public MemberPurchaseHistoryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MemberPurchaseHistoryVO.builder()
					.ordersId(rs.getString("orders_id"))
					.productNo(rs.getInt("product_no"))
					.productName(rs.getString("product_name"))
					.productPrice(rs.getInt("product_price"))
					.productContent(rs.getString("product_content"))
					.productThumbnailNo(rs.getInt("product_thumbnail_no"))
					.attachmentNo(rs.getInt("attachment_no"))
					.attachmentName(rs.getString("attachment_name"))
				.build();
	}

}
