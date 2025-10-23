package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.mapper.ReviewDetailVOMapper;
import com.kh.shoppingmall.mapper.ReviewMapper;
import com.kh.shoppingmall.vo.ReviewDetailVO;

@Repository
public class ReviewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewDetailVOMapper reviewDetailVOMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    // ================= CRUD =================

    public ReviewDto selectOne(int reviewNo) {
        String sql = "SELECT * FROM review WHERE review_no = ?";
        List<ReviewDto> list = jdbcTemplate.query(sql, reviewMapper, reviewNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public String selectAuthorId(int reviewNo) {
        String sql = "SELECT member_id FROM review WHERE review_no = ?";
        return jdbcTemplate.queryForObject(sql, String.class, reviewNo);
    }

    public Double selectAverageRating(int productNo) {
        String sql = "SELECT COALESCE(AVG(review_rating), 0) FROM review WHERE product_no = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, productNo);
    }

    public List<ReviewDetailVO> selectDetailListByMember(String memberId) {
        String sql = "SELECT * FROM review_detail WHERE member_id = ? ORDER BY review_no DESC";
        return jdbcTemplate.query(sql, reviewDetailVOMapper, memberId);
    }

    public List<ReviewDetailVO> selectDetailListByProduct(int productNo) {
        String sql = "SELECT * FROM review_detail WHERE product_no = ? ORDER BY review_no DESC";
        return jdbcTemplate.query(sql, reviewDetailVOMapper, productNo);
    }

    public int insert(ReviewDto reviewDto) {
        String seqSql = "SELECT review_seq.nextval FROM dual";
        int reviewNo = jdbcTemplate.queryForObject(seqSql, int.class);
        reviewDto.setReviewNo(reviewNo);

        String insertSql = "INSERT INTO review(review_no, product_no, member_id, review_rating, review_content) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertSql,
                reviewNo,
                reviewDto.getProductNo(),
                reviewDto.getMemberId(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewContent()
        );

        return reviewNo;
    }

    public boolean update(ReviewDto reviewDto) {
        String sql = "UPDATE review SET review_content = ?, review_rating = ? WHERE review_no = ?";
        return jdbcTemplate.update(sql,
                reviewDto.getReviewContent(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewNo()) > 0;
    }

    public boolean delete(int reviewNo) {
        String sql = "DELETE FROM review WHERE review_no = ?";
        return jdbcTemplate.update(sql, reviewNo) > 0;
    }
}
