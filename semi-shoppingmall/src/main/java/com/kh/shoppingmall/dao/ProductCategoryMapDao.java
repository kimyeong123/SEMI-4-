package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryMapDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// 등록
	public void insert(int productNo, int categoryNo) {
		String sql = "insert into product_category_map(product_no, category_no) values(?, ?)";
		Object[] params = {productNo, categoryNo};
		jdbcTemplate.update(sql, params);
	}
	public boolean check(int productNo, int categoryNo) {
	    String sql = "select count(*) from product_category_map where product_no = ? and category_no = ?";
	    Object[] params = {productNo, categoryNo};
	    return jdbcTemplate.queryForObject(sql, Integer.class, params) > 0;
	}
	public boolean delete(int productNo, int categoryNo) {
		String sql = "delete from product_category_map where product_no=? and category_no=?";
		Object[] params = {productNo, categoryNo};
		return jdbcTemplate.update(sql, params) > 0;
	}
	public List<Integer> selectCategoryNosByProductNo(int productNo) {
	    String sql = "select category_no from product_category_map where product_no = ? order by category_no asc"; 
	    Object[] params = {productNo};
	    return jdbcTemplate.queryForList(sql, Integer.class, params);
	}
	public List<Integer> selectProductNosByCategoryNo(int categoryNo) {
	    String sql = "select product_no from product_category_map where category_no = ? order by product_no ASC";
	    Object[] params = {categoryNo};
	    return jdbcTemplate.queryForList(sql, Integer.class, params);
	}
	public int countByCategory(Integer categoryNo) {
	    String sql = "select count(*) from product_category_map where category_no = ?";
	    Object[] params = {categoryNo};
	    return jdbcTemplate.queryForObject(sql, Integer.class, params);
	}
}
