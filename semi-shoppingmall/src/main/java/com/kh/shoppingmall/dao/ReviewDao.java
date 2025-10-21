package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.mapper.ReviewMapper;

@Repository
public class ReviewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewMapper reviewMapper;
    
    // 시퀀스 생성 
    public int sequence() {
        String sql = "select review_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    // 리뷰 목록 조회 회원 아이디 기준으로 조회
    public List<ReviewDto> selectListByMember(String memberId) {
        String sql = "select * from review where product_no = ? order by review_no desc";
        Object[] params = {memberId};
        return jdbcTemplate.query(sql, reviewMapper, params);
    }

    // 리뷰 단일 조회
    public ReviewDto selectOne(int reviewNo) {
        String sql = "select * from review where review_no = ?";
        Object[] params = {reviewNo};
        List<ReviewDto> list = jdbcTemplate.query(sql, reviewMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // 리뷰 등록
    public void insert(ReviewDto reviewDto) {
        String sql = "insert review(review_no, product_no, member_id, review_content, review_rating, review_created_at) "
                   + "values(?, ?, ?, ?, ?, ?)";
        Object[] params = {
            reviewDto.getReviewNo(),
            reviewDto.getProductNo(),
            reviewDto.getMemberId(),
            reviewDto.getReviewContent(),
            reviewDto.getReviewRating(),
            reviewDto.getReviewCreatedAt()
        };
        jdbcTemplate.update(sql, params);
    }

    // 리뷰 수정
    public boolean update(ReviewDto reviewDto) {
        String sql = "update review "
                   + "set review_content = ?, review_rating = ? "
                   + "where review_no = ?";
        Object[] params = {
            reviewDto.getReviewContent(),
            reviewDto.getReviewRating(),
            reviewDto.getReviewNo()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    // 리뷰 삭제
    public boolean delete(int reviewNo) {
        String sql = "delete from review where review_no = ?";
        Object[] params = {reviewNo};
        return jdbcTemplate.update(sql, params) > 0;
    }
}
