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
        return jdbcTemplate.queryForObject(sql, Integer.class);
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
    
    public List<OrdersDto> selectListByMemberId(String ordersId) {
        String sql = "select * from orders where orders_id = ? order by orders_no desc";
        Object[] params = { ordersId };
        return jdbcTemplate.query(sql, ordersMapper, params);
    }
    
    public List<OrdersSummaryVO> selectSummaryListByMemberId(String ordersId) {
        String sql = "select * from order_summary where orders_id = ? order by orders_no desc";
        Object[] params = { ordersId };
        return jdbcTemplate.query(sql, ordersSummaryMapper, params);
    }
    
    public OrdersDto selectOneByOrderNo(int ordersNo) {
        String sql = "select * from orders where orders_no = ?";
        Object[] params = { ordersNo };
        List<OrdersDto> list = jdbcTemplate.query(sql, ordersMapper, params);
        return list.isEmpty() ? null : list.get(0);        
    }
    
    public List<OrdersSummaryVO> selectOrderSummary(int ordersNo) {
        String sql = "SELECT * FROM order_summary WHERE orders_no = ?";
        Object[] params = { ordersNo };
        return jdbcTemplate.query(sql, ordersSummaryMapper, params);
    }
    
    public boolean update(int ordersNo, String ordersStatus) {
        String sql = "update orders set orders_status = ? where orders_no = ?";
        Object[] params = { ordersStatus, ordersNo };
        return jdbcTemplate.update(sql, params) > 0;
    }
    
    public boolean clearMemberId(String memberId) {
        String sql = "update orders set orders_id = null where orders_id = ?";
        Object[] params = { memberId };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 관리자용 전체 주문 목록 
    public List<OrdersSummaryVO> selectListAll() {
        String sql = "SELECT * FROM order_summary ORDER BY orders_no DESC";
        return jdbcTemplate.query(sql, ordersSummaryMapper);
    }
}
