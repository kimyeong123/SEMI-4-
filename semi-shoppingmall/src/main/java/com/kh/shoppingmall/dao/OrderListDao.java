package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.mapper.OrderListMapper;
import com.kh.shoppingmall.vo.OrderListVO;

@Repository
public class OrderListDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private OrderListMapper orderListMapper;
	
	public List<OrderListVO> selectList(String ordersId){
		String sql = "select * from order_list_summary where orders_id=?";
		
		Object[] params = { ordersId };
		
		return jdbcTemplate.query(sql, orderListMapper, params);
	}
}






