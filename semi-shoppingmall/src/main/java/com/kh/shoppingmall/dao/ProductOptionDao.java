package com.kh.shoppingmall.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.mapper.ProductOptionMapper;

@Repository
public class ProductOptionDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductOptionMapper productOptionMapper;

    // ================= 시퀀스 발급 =================
    public int sequence() {
        String sql = "SELECT option_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // ================= 옵션 등록 =================
    public void insert(ProductOptionDto dto) {
        String sql = "INSERT INTO product_option "
                   + "(option_no, product_no, option_name, option_value, option_stock) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Object[] params = {
            dto.getOptionNo(),
            dto.getProductNo(),
            dto.getOptionName(),
            dto.getOptionValue(),
            dto.getOptionStock()
        };
        jdbcTemplate.update(sql, params);
    }

    // ================= 특정 상품의 옵션 목록 조회 =================
    public List<ProductOptionDto> selectListByProduct(int productNo) {
        String sql = "SELECT * FROM product_option "
                   + "WHERE product_no = ? "
                   + "ORDER BY option_name ASC, option_no ASC";
        return jdbcTemplate.query(sql, productOptionMapper, productNo);
    }

    // ================= 옵션 수정 =================
    public boolean update(ProductOptionDto dto) {
        String sql = "UPDATE product_option "
                   + "SET option_name = ?, option_value = ?, option_stock = ? "
                   + "WHERE option_no = ?";
        Object[] params = {
            dto.getOptionName(),
            dto.getOptionValue(),
            dto.getOptionStock(),
            dto.getOptionNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // ================= 재고만 수정 =================
    public boolean updateStock(int optionNo, int amount) {
        String sql = "UPDATE product_option "
                   + "SET option_stock = option_stock + ? "
                   + "WHERE option_no = ?";
        Object[] params = { amount, optionNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // ================= 옵션 삭제 =================
    public boolean delete(int optionNo) {
        String sql = "DELETE FROM product_option WHERE option_no = ?";
        return jdbcTemplate.update(sql, optionNo) > 0;
    }
}
