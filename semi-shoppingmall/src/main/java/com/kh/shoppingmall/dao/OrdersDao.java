package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.OrdersDto;
import com.kh.shoppingmall.mapper.OrdersMapper;
import com.kh.shoppingmall.mapper.OrdersSummaryMapper;
import com.kh.shoppingmall.vo.OrdersSummaryVO;

@Repository
public class OrdersDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private OrdersMapper ordersMapper;
	@Autowired
	private OrdersSummaryMapper ordersSummaryMapper;
	
	public int sequence() {
		String sql = "select orders_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	} 
	
	public void insert(OrdersDto ordersDto) {
		String sql = "insert into orders("
				+ "orders_no, orders_id, orders_totalprice, "
				+ "orders_recipient, orders_recipientcontact, "
				+ "orders_shippingpost, "
				+ "orders_shippingaddress1, orders_shippingaddress2, "
				+ "orders_status"
				+ ") values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Object[] params = {
				ordersDto.getOrdersNo(), ordersDto.getOrdersId(), ordersDto.getOrdersTotalPrice(), 
				ordersDto.getOrdersRecipient(), ordersDto.getOrdersRecipientContact(), 
				ordersDto.getOrdersShippingPost(), 
				ordersDto.getOrdersShippingAddress1(), ordersDto.getOrdersShippingAddress2(), 
				ordersDto.getOrdersStatus()
		};
		
		jdbcTemplate.update(sql, params);
	}
	
	// 1. R(Select): 내 주문 내역 목록 조회 (member_id 기준)
	public List<OrdersDto> selectListByMemberId(String ordersId) {
		String sql = "select * from orders where orders_id = ? order by orders_no desc";

		Object[] param = {ordersId};
		return jdbcTemplate.query(sql, ordersMapper, param);
	}
	
	// 2. R(Select): 주문 1건 기본 상세조회 (orders_no 기준)
	// 이 함수는 OrdersDto의 기본 정보만 가져옴. 나중에 OrderDetailsVO로 확장 필요.
	public OrdersDto selectOneByOrderNo(int ordersNo) {
		String sql = "select * from orders where orders_no = ?";
		Object[] param = {ordersNo};
		List<OrdersDto> list = jdbcTemplate.query(sql, ordersMapper, param);
		return list.isEmpty() ? null : list.get(0);		
	}
	
	public boolean update (int ordersNo, String ordersStatus) {
		String sql = "update orders set orders_status = ? where orders_no = ?";
		Object[] params = {ordersStatus, ordersNo};
		
		return jdbcTemplate.update(sql, params) > 0;
		
	}
	
	//멤버 아이디 지우는 기능(추가됨)
	public boolean clearMemberId(String memberId) {
		String sql = "update orders set orders_id = null where orders_id = ? ";
		Object[] param = {memberId};
		
		return jdbcTemplate.update(sql, param) > 0;
	}
	
	
}
