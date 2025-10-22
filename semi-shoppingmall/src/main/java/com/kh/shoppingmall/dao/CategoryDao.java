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

    // C
    public void insert(CategoryDto categoryDto) {
        String sql = "INSERT INTO category (category_no, category_name, parent_category_no) "
                   + "VALUES (category_seq.nextval, ?, ?)";
        Object[] params = {
            categoryDto.getCategoryName(),
            categoryDto.getParentCategoryNo()
        };
        jdbcTemplate.update(sql, params);
    }

    // R
    public List<CategoryDto> selectList() {
        String sql = "SELECT * FROM category";
        return jdbcTemplate.query(sql, categoryMapper);
    }

    // R
    public CategoryDto selectOne(int categoryNo) {
        String sql = "SELECT * FROM category WHERE category_no=?";
        Object[] params = { categoryNo };
        List<CategoryDto> list = jdbcTemplate.query(sql, categoryMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // 부모 카테고리만 조회
    public List<CategoryDto> selectParentCategories() {
        String sql = "SELECT * FROM category WHERE parent_category_no IS NULL ORDER BY category_no ASC";
        return jdbcTemplate.query(sql, categoryMapper);
    }

    // 특정 부모 카테고리의 하위 카테고리 조회
    public List<CategoryDto> selectChildren(int parentCategoryNo) {
        String sql = "SELECT * FROM category WHERE parent_category_no = ? ORDER BY category_no ASC";
        Object[] params = { parentCategoryNo };
        return jdbcTemplate.query(sql, categoryMapper, params);
    }

    // 자식 카테고리 개수
    public int countByParent(int categoryNo) {
        String sql = "SELECT COUNT(*) FROM category WHERE parent_category_no = ?";
        Object[] params = { categoryNo };
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }

    // U
    public boolean update(CategoryDto categoryDto) {
        String sql = "UPDATE category SET category_name=?, parent_category_no=? WHERE category_no=?";
        Object[] params = {
            categoryDto.getCategoryName(),
            categoryDto.getParentCategoryNo(),
            categoryDto.getCategoryNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // D
    public boolean delete(int categoryNo) {
        String sql = "DELETE FROM category WHERE category_no=?";
        Object[] params = { categoryNo };
        return jdbcTemplate.update(sql, params) > 0;
    }
}
