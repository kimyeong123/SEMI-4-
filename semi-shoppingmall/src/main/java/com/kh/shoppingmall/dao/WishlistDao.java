package com.kh.shoppingmall.dao;

import java.util.List;
import java.util.Map;

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
		String sql = "SELECT "
		           + "    T1.wishlist_no, T1.member_id, T1.created_at, T1.product_no, "
		           + "    (SELECT MIN(option_no) FROM product_option WHERE product_no = T1.product_no) AS option_no, " 
		           + "    T1.product_name, T1.product_price, T1.attachment_no, T1.attachment_name "
		           + "FROM "
		           + "    wishlist_detail T1 "
		           + "WHERE "
		           + "    T1.MEMBER_ID = ?"; 
		return jdbcTemplate.query(sql, wishlistDetailVOMapper, memberId);
	}


	//갱신메서드 추가(회원 탈퇴시 갱신목적)
	public boolean updateProductWishlistCount (int productNo) {
		//1. 위시리스트 테이블에서 상품의 총 개수 확인
		int count = this.countByProductNo(productNo);
		
		//2. 테이블의 위시리스트 카운트 필드 업데이트
		String sql = "update product set product_wishlist_count = ? "
				+ "where product_no = ?";
		Object[] params = {count, productNo};
		return jdbcTemplate.update(sql, params) > 0;
	}
	
	//회원 위시리스트 내역 삭제 메소드
	public boolean deleteByMemberId(String memberId) {
		String sql = "delete from wishlist where member_id=?";
		
		Object[] params = { memberId };
		
		return jdbcTemplate.update(sql, params) > 0;
		
	}

	public Map<Integer, Integer> selectProductWishlistCounts() {
	    String sql = "SELECT product_no, COUNT(*) AS cnt FROM wishlist GROUP BY product_no";

	    // query 결과를 Map 형태로 변환
	    return jdbcTemplate.query(sql, rs -> {
	        Map<Integer, Integer> map = new java.util.HashMap<>();
	        while (rs.next()) {
	            map.put(rs.getInt("product_no"), rs.getInt("cnt"));
	        }
	        return map;
	    });
	}

    public int deleteByProductNo(int productNo) {
        String sql = "DELETE FROM wishlist WHERE product_no = ?";
        return jdbcTemplate.update(sql, productNo);
    }
	
}