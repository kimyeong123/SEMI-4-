package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.mapper.StatMapper;
import com.kh.shoppingmall.vo.StatVO;

@Repository
public class StatDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private StatMapper statMapper;
	
	//날짜별 가입자 수 확인
	public List<StatVO> countByMemberJoin(){
		String sql = "select to_char(trunc(member_join), 'YYYY-MM-DD') title, count(*) value "
				+ "from member group by trunc(member_join) order by value desc, title asc";
		
		return jdbcTemplate.query(sql, statMapper);
	}
	
	//많이 구매한 이용자 확인
	public List<StatVO> countByMemberId(){
		String sql = "select m.member_nickname title, count(*) value "
	               + "from orders o "
	               + "join member m on o.orders_id = m.member_id "
	               + "group by m.member_nickname "
	               + "order by value DESC, title ASC";
		
		return jdbcTemplate.query(sql, statMapper);
	}
	
	//많이 구매된 제품 순위 확인
	public List<StatVO> countByProductNo(){
		String sql = "select p.product_name title, count(*) value "
	               + "from order_detail od "
	               + "join product p on od.product_no = p.product_no "
	               + "group by p.product_name "
	               + "order by value desc, title asc";
		
		return jdbcTemplate.query(sql, statMapper);
	}
	
	// 주문 상태별 주문 건수 확인
	public List<StatVO> countByOrderStatus(){
	    String sql = "select orders_status title, count(*) value "
	               + "from orders "
	               + "group by orders_status "
	               + "order by value desc, title asc";

	    return jdbcTemplate.query(sql, statMapper);
	}
}
