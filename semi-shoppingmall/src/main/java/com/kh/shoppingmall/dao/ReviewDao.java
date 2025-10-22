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
    private ReviewMapper reviewMapper;
    
    @Autowired
    private ReviewDetailVOMapper reviewDetailVOMapper;
    
    // 시퀀스 생성 
    public int sequence() {
        String sql = "select review_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    // 리뷰 목록 조회 회원 아이디 기준으로 조회
//    public List<ReviewDetailVO> selectListByMember(String memberId) {
//        String sql = "select * from review where member_id = ? order by review_no desc";
//        Object[] params = {memberId};
//        return jdbcTemplate.query(sql, reviewDetailMapper, params);
//    }
    
    //리뷰 목록 조회 상품 기준으로 조회
    public List<ReviewDetailVO> selectListByProduct(int productNo) {
        String sql = "select * from review_detail where product_no = ? order by review_no desc";
        Object[] params = { productNo };
        return jdbcTemplate.query(sql, reviewDetailVOMapper, params);
    }


    // 리뷰 단일 조회
    public ReviewDto selectOne(int reviewNo) {
        String sql = "select * from review where review_no = ?";
        Object[] params = {reviewNo};
        List<ReviewDto> list = jdbcTemplate.query(sql, reviewMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // 리뷰 등록
    public int insert(ReviewDto reviewDto) {
        String seqSql = "select review_seq.nextval from dual";
        int reviewNo = jdbcTemplate.queryForObject(seqSql, int.class);
        reviewDto.setReviewNo(reviewNo); 
        String insertSql = "insert into review("
                + "review_no, product_no, member_id, review_rating, review_content"
                + ") values(?, ?, ?, ?, ?)";
        Object[] params = {
                reviewNo, reviewDto.getProductNo(), reviewDto.getMemberId(), 
                reviewDto.getReviewRating(), reviewDto.getReviewContent()
        };
        jdbcTemplate.update(insertSql, params);
        
        return reviewNo;
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

    public List<ReviewDetailVO> selectDetailListByMember(String memberId) {
        // ReviewDetailVO는 리뷰 정보와 함께, 첨부파일, 상품명 등 조인된 정보를 포함하는 VO라고 가정합니다.
        String sql = "SELECT * FROM review_detail_view WHERE member_id = ? ORDER BY review_no DESC";
        Object[] params = {memberId};
        
        return jdbcTemplate.query(sql, reviewDetailVOMapper, params); 
    }
}
