package com.kh.shoppingmall.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOptionItemDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // (시퀀스 생성)
    public int sequence() {
        String sql = "SELECT product_option_item_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // (옵션 아이템 등록)
    public void insert(int itemNo, int groupNo, String optionValue) {
        String sql = "INSERT INTO product_option_item(option_item_no, option_group_no, option_value) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, itemNo, groupNo, optionValue);
    }
}
