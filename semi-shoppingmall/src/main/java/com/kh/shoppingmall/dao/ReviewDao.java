package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.vo.ReviewDetailVO;
import com.kh.shoppingmall.mapper.ReviewDetailVOMapper;

@Repository
public class ReviewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewDetailVOMapper reviewDetailVOMapper;

    // ================= CRUD =================

    // 단일 리뷰 조회 (DTO)
    public ReviewDto selectOne(int reviewNo) {
        String sql = "SELECT review_no, product_no, review_content, review_rating, review_created_at, member_id " +
                     "FROM review WHERE review_no = ?";
        List<ReviewDto> list = jdbcTemplate.query(sql, (rs, rowNum) -> ReviewDto.builder()
                .reviewNo(rs.getInt("review_no"))
                .productNo(rs.getInt("product_no"))
                .reviewContent(rs.getString("review_content"))
                .reviewRating(rs.getInt("review_rating"))
                .reviewCreatedAt(rs.getTimestamp("review_created_at"))
                .memberId(rs.getString("member_id"))
                .build(), reviewNo);
        return list.isEmpty() ? null : list.get(0);
    }

    // 멤버 ID로 리뷰 목록 조회 (VO)
    public List<ReviewDetailVO> selectDetailListByMember(String memberId) {
        String sql = "SELECT * FROM review_detail WHERE member_id = ? ORDER BY review_no DESC";
        return jdbcTemplate.query(sql, reviewDetailVOMapper, memberId);
    }

    // 상품 번호로 리뷰 목록 조회 (VO)
    public List<ReviewDetailVO> selectDetailListByProduct(int productNo) {
        String sql = "SELECT * FROM review_detail WHERE product_no = ? ORDER BY review_no DESC";
        return jdbcTemplate.query(sql, reviewDetailVOMapper, productNo);
    }

    // 리뷰 평균 평점 조회
    public Double selectAverageRating(int productNo) {
        String sql = "SELECT COALESCE(AVG(review_rating), 0) FROM review WHERE product_no = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, productNo);
    }

    // 리뷰 등록
    public int insert(ReviewDto reviewDto) {
        int reviewNo = jdbcTemplate.queryForObject("SELECT review_seq.nextval FROM dual", int.class);
        reviewDto.setReviewNo(reviewNo);

        String insertSql = "INSERT INTO review(review_no, product_no, member_id, review_rating, review_content) " +
                           "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertSql,
                reviewNo,
                reviewDto.getProductNo(),
                reviewDto.getMemberId(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewContent()
        );
        return reviewNo;
    }

    // 리뷰 수정
    public boolean update(ReviewDto reviewDto) {
        String sql = "UPDATE review SET review_content = ?, review_rating = ? WHERE review_no = ?";
        return jdbcTemplate.update(sql,
                reviewDto.getReviewContent(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewNo()) > 0;
    }

    // 리뷰 삭제
    public boolean delete(int reviewNo) {
        String sql = "DELETE FROM review WHERE review_no = ?";
        return jdbcTemplate.update(sql, reviewNo) > 0;
    }

    // 작성자 ID 조회
    public String selectAuthorId(int reviewNo) {
        String sql = "SELECT member_id FROM review WHERE review_no = ?";
        return jdbcTemplate.queryForObject(sql, String.class, reviewNo);
    }
}
