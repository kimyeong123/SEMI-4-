package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.mapper.AttachmentMapper;

@Repository
public class AttachmentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AttachmentMapper attachmentMapper;

    /**
     * 1. 첨부파일 번호(시퀀스) 생성
     */
    public int sequence() {
        String sql = "select attachment_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    /**
     * 2. 첨부파일 정보 저장 (DB 메타 정보)
     */
    public void insert(AttachmentDto attachmentDto) {
        String sql = "insert into attachment("
                   + "attachment_no, attachment_name, "
                   + "attachment_size, attachment_type, "
                   + "product_no, review_no"
                   + ") values(?, ?, ?, ?, ?, ?)";

        // 상품번호 또는 리뷰번호가 0이면 null로 변환 (DB 저장용)
        Integer productNo = attachmentDto.getProductNo() == 0 ? null : attachmentDto.getProductNo();
        Integer reviewNo = attachmentDto.getReviewNo() == 0 ? null : attachmentDto.getReviewNo();

        // 상품번호와 리뷰번호가 동시에 존재하면 예외 발생 (Service에서 처리하는 것이 일반적이지만, DAO에 명시)
        if (productNo != null && reviewNo != null) {
            throw new IllegalArgumentException("첨부 파일은 상품 또는 리뷰 중 하나에만 연결될 수 있습니다.");
        }
        Object[] params = {
            attachmentDto.getAttachmentNo(), attachmentDto.getAttachmentName(), 
            attachmentDto.getAttachmentSize(), attachmentDto.getAttachmentType(), 
            productNo, reviewNo
        };
        jdbcTemplate.update(sql, params);
    }

    /**
     * 3. 첨부파일 정보 조회 (단일 조회)
     */
    public AttachmentDto selectOne(int attachmentNo) {
        String sql  = "select * from attachment where attachment_no = ?";
        Object[] param = {attachmentNo};
        List<AttachmentDto> list = jdbcTemplate.query(sql, attachmentMapper, param);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 4. 첨부파일 삭제 (단일 삭제)
     */
    public boolean delete(int attachmentNo) {
        String sql = "delete from attachment where attachment_no = ?";
        Object[] param = {attachmentNo};
        return jdbcTemplate.update(sql, param) > 0;
    }

    /**
     * 5. 첨부파일에 상품번호를 연결 (업데이트)
     */
    public boolean updateProductNo(int attachmentNo, int productNo) {
        String sql = "UPDATE attachment SET product_no = ? WHERE attachment_no = ?";
        Object[] params = { productNo, attachmentNo };
        return jdbcTemplate.update(sql, params) > 0;
    }
    
    // --- ReviewService의 deleteReview 지원 메서드 ---
    
    /**
     * 6. 리뷰 번호로 첨부 파일 목록 조회 (물리 파일 삭제 정보 획득용)
     */
    public List<AttachmentDto> selectListByReviewNo(int reviewNo) {
        String sql = "select * from attachment where review_no = ?";
        Object[] params = {reviewNo};
        return jdbcTemplate.query(sql, attachmentMapper, params);
    }

    /**
     * 7. 리뷰 번호로 첨부 파일 DB 정보 삭제 (리뷰 삭제 시 정리)
     */
    public int deleteByReviewNo(int reviewNo) {
        String sql = "delete from attachment where review_no = ?";
        Object[] params = {reviewNo};
        return jdbcTemplate.update(sql, params);
    }
}
