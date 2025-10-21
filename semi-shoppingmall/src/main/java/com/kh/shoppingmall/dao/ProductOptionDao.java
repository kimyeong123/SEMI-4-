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

    // 시퀀스 생성
    public int sequence() {
        String sql = "select product_option_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // 특정 상품의 옵션 목록 조회
    public List<ProductOptionDto> selectListByProduct(int productNo) {
        String sql = "select * from product_option where product_no = ? order by option_no";
        Object[] params = {productNo};
        return jdbcTemplate.query(sql, productOptionMapper, params);
    }

    // 옵션 등록   option_name = (ex) 사이즈, 색상 option_value = (ex) 265, 빨강
    public void insert(ProductOptionDto productOptionDto) {
        String sql = "insert into product_option(option_no, product_no, option_name, option_value, option_stook) "
                   + "values (?, ?, ?, ?, ?)";
        Object[] params = {
            productOptionDto.getOptionNo(),
            productOptionDto.getProductNo(),
            productOptionDto.getOptionName(),
            productOptionDto.getOptionValue(),
            productOptionDto.getOptionStook()
        };
        jdbcTemplate.update(sql, params);
    }

    // 옵션 수정
    public boolean update(ProductOptionDto productOptionDto) {
        String sql = "update product_option "
                   + "set option_name = ?, option_value = ?, option_stook = ? "
                   + "where option_no = ?";
        Object[] params = {
            productOptionDto.getOptionName(),
            productOptionDto.getOptionValue(),
            productOptionDto.getOptionStook(),
            productOptionDto.getOptionNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 옵션 삭제
    public boolean delete(int optionNo) {
        String sql = "delete from product_option where option_no = ?";
        Object[] params = {optionNo};
        return jdbcTemplate.update(sql, params) > 0;
    }
}
