package com.kh.shoppingmall.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOptionCombinationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // (시퀀스 생성)
    public int sequence() {
        String sql = "SELECT product_option_combination_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // (조합 등록 — 현재는 선택적, 나중에 색상+사이즈 조합용으로 확장 가능)
    public void insert(int combinationNo, int productNo, Integer colorItemNo, Integer sizeItemNo, int stock) {
        String sql = "INSERT INTO product_option_combination(combination_no, product_no, color_option_item_no, size_option_item_no, combination_stock) "
                   + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, combinationNo, productNo, colorItemNo, sizeItemNo, stock);
    }
}
