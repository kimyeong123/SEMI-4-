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
                   + "(option_no, product_no, option_name, option_value, option_stock, option_parent_no) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        Object[] params = {
            dto.getOptionNo(),
            dto.getProductNo(),
            dto.getOptionName(),
            dto.getOptionValue(),
            dto.getOptionStock(),
            dto.getOptionParentNo()
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

    // ✅ 부모 옵션 목록 조회 (option_parent_no IS NULL)
    public List<ProductOptionDto> selectParentOptions(int productNo) {
        String sql = "SELECT * FROM product_option "
                   + "WHERE product_no = ? AND option_parent_no IS NULL "
                   + "ORDER BY option_no ASC";
        return jdbcTemplate.query(sql, productOptionMapper, productNo);
    }

    // ✅ 특정 부모 옵션의 하위 옵션 목록 조회
    public List<ProductOptionDto> selectChildOptions(int parentOptionNo) {
        String sql = "SELECT * FROM product_option "
                   + "WHERE option_parent_no = ? "
                   + "ORDER BY option_no ASC";
        return jdbcTemplate.query(sql, productOptionMapper, parentOptionNo);
    }

    // ✅ 색상 + 사이즈 조합으로 해당 옵션 번호 찾기 (2단 옵션 구조)
    public Integer findOptionNoByColorAndSize(int productNo, String color, String size) {
        // 1️⃣ 부모-자식 구조 (색상 → 사이즈)
        String sql = """
            SELECT c.option_no
            FROM product_option p
            JOIN product_option c ON p.option_no = c.option_parent_no
            WHERE p.product_no = ?
              AND p.option_name = '색상'
              AND TRIM(p.option_value) = TRIM(?)
              AND c.option_name = '사이즈'
              AND TRIM(c.option_value) = TRIM(?)
        """;
        List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, productNo, color, size);
        if (!list.isEmpty()) return list.get(0);

        // 2️⃣ parent_no가 비어있는 경우 (단일 구조일 때 fallback)
        String sql2 = """
            SELECT option_no FROM product_option
            WHERE product_no = ?
              AND (
                  (option_name = '색상' AND TRIM(option_value) = TRIM(?))
                  OR
                  (option_name = '사이즈' AND TRIM(option_value) = TRIM(?))
              )
        """;
        List<Integer> list2 = jdbcTemplate.queryForList(sql2, Integer.class, productNo, color, size);
        return list2.isEmpty() ? null : list2.get(0);
    }

    // ✅ 단일 옵션 이름 + 값으로 옵션 번호 찾기 (부모-자식 구조 아닐 때)
    public Integer findOptionNoByNameValue(int productNo, String name, String value) {
        String sql = """
            SELECT option_no FROM product_option
            WHERE product_no = ?
              AND TRIM(option_name) = TRIM(?)
              AND TRIM(option_value) = TRIM(?)
        """;
        List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, productNo, name, value);
        return list.isEmpty() ? null : list.get(0);
    }

    // ✅ 색상만 있을 때 자동 처리용 (2단 구조 없을 때 대응)
    public Integer findOptionNoAuto(int productNo, String color, String size) {
        Integer optionNo = null;

        // 1️⃣ 색상 + 사이즈 조합 먼저 시도
        if (color != null && size != null) {
            optionNo = findOptionNoByColorAndSize(productNo, color, size);
        }

        // 2️⃣ 색상만 있을 경우 단일 옵션 검색
        if (optionNo == null && color != null && (size == null || size.isEmpty())) {
            optionNo = findOptionNoByNameValue(productNo, "색상", color);
        }

        // 3️⃣ 사이즈만 있을 경우 단일 옵션 검색
        if (optionNo == null && size != null && (color == null || color.isEmpty())) {
            optionNo = findOptionNoByNameValue(productNo, "사이즈", size);
        }

        return optionNo;
    }

    // ================= 옵션 수정 =================
    public boolean update(ProductOptionDto dto) {
        String sql = "UPDATE product_option "
                   + "SET option_name = ?, option_value = ?, option_stock = ?, option_parent_no = ? "
                   + "WHERE option_no = ?";
        Object[] params = {
            dto.getOptionName(),
            dto.getOptionValue(),
            dto.getOptionStock(),
            dto.getOptionParentNo(),
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

    // 특정 상품의 모든 옵션 삭제
    public int deleteByProduct(int productNo) {
        String sql = "DELETE FROM product_option WHERE product_no = ?";
        return jdbcTemplate.update(sql, productNo);
    }
}
