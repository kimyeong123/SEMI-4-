package com.kh.shoppingmall.dao;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.mapper.ProductMapper;

@Repository
public class ProductDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductMapper productMapper;
    
    //시퀀스 생성
    public int sequence() {
        String sql = "select product_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    // 상품 등록
    public void insert(ProductDto dto) {
        String sql = "insert into product(product_no, product_name, product_price, product_content, product_thumbnail_no) "
                   + "values(?, ?, ?, ?, ?)";
        Object[] params = {
            dto.getProductNo(),
            dto.getProductName(),
            dto.getProductPrice(),
            dto.getProductContent(),
            dto.getProductThumbnailNo()
        };
        jdbcTemplate.update(sql, params);
    }

    // 상품 수정
    public boolean update(ProductDto dto) {
        String sql = "update product set "
                   + "product_name=?, product_price=?, product_content=?, product_thumbnail_no=? "
                   + "where product_no=?";
        Object[] params = {
            dto.getProductName(),
            dto.getProductPrice(),
            dto.getProductContent(),
            dto.getProductThumbnailNo(),
            dto.getProductNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 상품 삭제
    public boolean delete(int productNo) {
        String sql = "delete from product where product_no=?";
        Object[] params = { productNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 전체 목록
    public List<ProductDto> selectList() {
        String sql = "select * from product order by product_no asc";
        return jdbcTemplate.query(sql, productMapper);
    }

    // 상품 검색
    public List<ProductDto> selectList(String column, String keyword) {
        Set<String> allowList = Set.of("product_name", "product_content");
        if (!allowList.contains(column)) return List.of();

        String sql = "select * from product where instr(#1, ?) > 0 order by #1 asc, product_no asc";
        sql = sql.replace("#1", column);
        Object[] params = { keyword };
        return jdbcTemplate.query(sql, productMapper, params);
    }

    // 상세 조회
    public ProductDto selectOne(int productNo) {
        String sql = "select * from product where product_no=?";
        Object[] params = { productNo };
        List<ProductDto> list = jdbcTemplate.query(sql, productMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // 썸네일 연결
    public void connectThumbnail(int productNo, int thumbnailNo) {
        String sql = "update product set product_thumbnail_no=? where product_no=?";
        Object[] params = { thumbnailNo, productNo };
        jdbcTemplate.update(sql, params);
    }

    // 썸네일 찾기
    public int findThumbnail(int productNo) {
        String sql = "select product_thumbnail_no from product where product_no=?";
        Object[] params = { productNo };
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }
}
