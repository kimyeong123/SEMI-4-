package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// 등록
	public void insert(int productNo, int categoryNo) {
		String sql = "insert into productCategory(product_no, category_no) values(?, ?)";
		Object[] params = {productNo, categoryNo};
		jdbcTemplate.update(sql, params);
	}
	public boolean check(int productNo, int categoryNo) {
	    String sql = "select count(*) from productCategory where product_no = ? and category_no = ?";
	    Object[] params = {productNo, categoryNo};
	    return jdbcTemplate.queryForObject(sql, Integer.class, params) > 0;
	}
	public boolean delete(int productNo, int categoryNo) {
		String sql = "delete productCategory where product_no=? and category_no=?";
		Object[] params = {productNo, categoryNo};
		return jdbcTemplate.update(sql, params) > 0;
	}
	public List<Integer> selectCategoryNosByProductNo(int productNo) {
	    String sql = "select category_no from productCategory where product_no = ? order by category_no asc"; 
	    Object[] params = {productNo};
	    return jdbcTemplate.queryForList(sql, Integer.class, params);
	}
	public List<Integer> selectProductNosByCategoryNo(int categoryNo) {
	    String sql = "SELECT product_no FROM productCategory WHERE category_no = ? ORDER BY product_no ASC";
	    Object[] params = {categoryNo};
	    return jdbcTemplate.queryForList(sql, Integer.class, params);
	}
	
}
