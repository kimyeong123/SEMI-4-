package com.kh.shoppingmall.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOptionGroupDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // (시퀀스 생성)
    public int sequence() {
        String sql = "SELECT product_option_group_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // (옵션 그룹 등록)
    public void insert(int groupNo, int productNo, String groupName) {
        String sql = "INSERT INTO product_option_group(option_group_no, product_no, option_group_name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, groupNo, productNo, groupName);
    }
}
