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
     * Note: productNo, reviewNo가 0인 경우 null로 변환하여 삽입합니다.
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

        // 첨부 파일은 상품 또는 리뷰 중 하나에만 연결 가능 (DAO 레벨에서 1차 방어)
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
        String sql = "update attachment set product_no = ?, review_no = null where attachment_no = ?";
        Object[] params = { productNo, attachmentNo };
        return jdbcTemplate.update(sql, params) > 0;
    }
    
    // --- ReviewService의 파일 처리 지원 메서드 ---
    
    /**
     * 6. 첨부파일에 리뷰 번호를 연결 (업데이트) - insertReview에서 사용
     */
    public boolean updateReviewNo(int attachmentNo, int reviewNo) {
        // 기존의 product_no를 null로 비우고 review_no를 설정합니다. (단일 연결 보장)
        String sql = "update attachment set review_no = ?, product_no = null where attachment_no = ?";
        Object[] params = { reviewNo, attachmentNo };
        return jdbcTemplate.update(sql, params) > 0;
    }
    
    /**
     * 7. 리뷰 번호로 연결된 모든 첨부 파일 번호(attachmentNo) 목록을 조회 - deleteReview에서 사용
     */
    public List<Integer> selectAttachmentNosByReviewNo(int reviewNo) {
		String sql = "select attachment_no from attachment where review_no = ?";
		Object[] params = {reviewNo};
        // queryForList를 사용하여 Integer 타입 리스트를 반환합니다.
		return jdbcTemplate.queryForList(sql, Integer.class, params);
	}
}