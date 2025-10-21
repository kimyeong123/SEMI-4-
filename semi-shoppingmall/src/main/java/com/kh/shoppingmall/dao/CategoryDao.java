package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.mapper.CategoryMapper;

@Repository
public class CategoryDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	//C
//	public int sequence() {
//		String sql = "select category_seq from dual";
//		return jdbcTemplate.queryForObject(sql, int.class);
//	}
	
	public void insert(CategoryDto categoryDto) {
		String sql = "insert into category "
				+ "(category_no, category_name, parent_category_no) "
				+ "values "
				+ "(category_seq.nextval, ?, ?)";
		
		Object[] params = {
				categoryDto.getCategoryName(),
				categoryDto.getParentCategoryNo()
		};
		
		jdbcTemplate.update(sql, params);
	}
	
	//R
	//전체 조회
	public List<CategoryDto> selectList(){
		String sql = "select * from category";
		
		return jdbcTemplate.query(sql, categoryMapper);
	}
	
	//상세 조회
	public CategoryDto selectOne(int categoryNo) {
		String sql = "select * from category where category_no=?";
		
		Object[] params = { categoryNo };
		
		List<CategoryDto> list = jdbcTemplate.query(sql, categoryMapper, params);
		
		return list.isEmpty() ? null : list.get(0);
	}
	//U
	public boolean update(CategoryDto categoryDto) {
		String sql = "update category set "
				+ "category_name=?, parent_category_no=? "
				+ "where category_no=?";
		
		Object[] params = {
				categoryDto.getCategoryName(),
				categoryDto.getParentCategoryNo(),
				categoryDto.getCategoryNo()
		};
		
		return jdbcTemplate.update(sql, params) > 0;
	}
	//D
	//카테고리(category.parent_category_no)가 남아있으면 
	//**DB 외래 키 오류(ORA-02292)**를 발생시킵니다. 
	//이는 CategoryService에서 삭제 전에 사용 중인지 확인하는 로직이 필요한 부분입니다.
	public boolean delete(int categoryNo) {
		String sql = "delete from category where category_no=?";
		
		Object[] params = { categoryNo };
		
		return jdbcTemplate.update(sql, params) > 0;
	}
}






