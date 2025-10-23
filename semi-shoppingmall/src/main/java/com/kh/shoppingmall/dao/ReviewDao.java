package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private ProductDao productDao;
    @Autowired
    private ReviewDetailVOMapper reviewDetailVOMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    // ================= CRUD =================

    // 리뷰 단일 조회
    public ReviewDto selectOne(int reviewNo) {
        String sql = "SELECT * FROM review WHERE review_no = ?";
        Object[] params = { reviewNo };
        List<ReviewDto> list = jdbcTemplate.query(sql, reviewMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // 리뷰 작성자 조회
    public String selectAuthorId(int reviewNo) {
        String sql = "SELECT member_id FROM review WHERE review_no = ?";
        Object[] params = { reviewNo };
        try {
            return jdbcTemplate.queryForObject(sql, String.class, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 리뷰 평균 평점 조회 (null-safe)
    public Double selectAverageRating(int productNo) {
        String sql = "SELECT COALESCE(AVG(review_rating), 0) FROM review WHERE product_no = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, productNo);
    }

    // 리뷰 상세 목록 조회 - 회원 기준
    public List<ReviewDetailVO> selectDetailListByMember(String memberId) {
        String sql = "SELECT * FROM review_detail WHERE member_id = ? ORDER BY review_no DESC";
        Object[] params = { memberId };
        return jdbcTemplate.query(sql, reviewDetailVOMapper, params);
    }

    // 리뷰 상세 목록 조회 - 상품 기준
    public List<ReviewDetailVO> selectDetailListByProduct(int productNo) {
        String sql = "SELECT * FROM review_detail WHERE product_no = ? ORDER BY review_no DESC";
        Object[] params = { productNo };
        return jdbcTemplate.query(sql, reviewDetailVOMapper, params);
    }

    // 리뷰 등록
    public int insert(ReviewDto reviewDto) {
        String seqSql = "SELECT review_seq.nextval FROM dual";
        int reviewNo = jdbcTemplate.queryForObject(seqSql, int.class);
        reviewDto.setReviewNo(reviewNo);

        String insertSql = "INSERT INTO review(review_no, product_no, member_id, review_rating, review_content) "
                + "VALUES (?, ?, ?, ?, ?)";
        Object[] params = {
                reviewNo,
                reviewDto.getProductNo(),
                reviewDto.getMemberId(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewContent()
        };
        jdbcTemplate.update(insertSql, params);

        return reviewNo;
    }

    // 리뷰 수정
    public boolean update(ReviewDto reviewDto) {
        String sql = "UPDATE review SET review_content = ?, review_rating = ? WHERE review_no = ?";
        Object[] params = {
                reviewDto.getReviewContent(),
                reviewDto.getReviewRating(),
                reviewDto.getReviewNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 리뷰 삭제
    public boolean delete(int reviewNo) {
        String sql = "DELETE FROM review WHERE review_no = ?";
        Object[] params = { reviewNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // ================= 평점 갱신 =================

    // 특정 상품 평균 평점 계산 후 갱신
    public void refreshAvgRating(int productNo) {
        try {
            Double avg = this.selectAverageRating(productNo);
            productDao.updateAverageRating(productNo, avg);
        } catch (Exception e) {
            System.err.println("평점 갱신 실패: productNo=" + productNo);
            e.printStackTrace();
        }
    }
}
