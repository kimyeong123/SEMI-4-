package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.mapper.WishlistDetailVOMapper;
import com.kh.shoppingmall.vo.WishlistDetailVO;

@Repository
public class WishlistDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private WishlistDetailVOMapper wishlistDetailVOMapper;

	// CRUD
	public void insert(String memberId, int productNo) {
		String sql = "insert into wishlist(" + "wishlist_no, member_id, product_no, created_at) "
				+ "values(wishlist_seq.nextval, ?, ?, systimestamp)";
		Object[] params = { memberId, productNo };
		jdbcTemplate.update(sql, params);
	}

	// 검사
	public boolean check(String memberId, int productNo) {
		if (memberId == null)
			return false;
		String sql = "select count(*) from wishlist where member_id=? and product_no=?";
		Object[] params = { memberId, productNo };
		int count = jdbcTemplate.queryForObject(sql, int.class, params);
		return count > 0;
	}

	public boolean delete(String memberId, int productNo) {
		String sql = "delete from wishlist where member_id=? and product_no=?";
		Object[] params = { memberId, productNo };
		return jdbcTemplate.update(sql, params) > 0;
	}

	// 상품이 받은 좋아요 개수
	public int countByProductNo(int productNo) {
		String sql = "select count(*) from wishlist where product_no = ?";
		Object[] params = { productNo };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}

	public int countByMemberId(String memberId) {
		String sql = "select count(*) from wishlist where member_id = ?";
		Object[] params = { memberId };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}

	public List<Integer> selectListByMemberId(String memberId) {
		String sql = "select product_no from wishlist where member_id = ?";
		Object[] params = { memberId };
		return jdbcTemplate.queryForList(sql, int.class, params);
	}
	
	public List<WishlistDetailVO> selectDetailListByMemberId(String memberId) {
	    String sql = "select * from wishlist_detail where member_id = ?"; // 뷰(View) 조회
	    Object[] params = {memberId};
	    return jdbcTemplate.query(sql, wishlistDetailVOMapper, params);
	}
}
