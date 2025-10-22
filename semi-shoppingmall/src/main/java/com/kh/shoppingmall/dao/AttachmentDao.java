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
	
	public int sequence() {
		String sql = "select attachment_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}
	
	public void insert(AttachmentDto attachmentDto) {
		String sql = "insert into attachment("
				+ "attachment_no, attachment_name, "
				+ "attachment_size, attachment_type, "
				+ "product_no, review_no"
				+ ") values(?, ?, ?, ?, ?, ?)";
		
		Integer productNo = attachmentDto.getProductNo() == 0 ? null : attachmentDto.getProductNo();
	    Integer reviewNo = attachmentDto.getReviewNo() == 0 ? null : attachmentDto.getReviewNo();
	    
	    
	 // 두 필드가 모두 null이 아닌 경우 (즉, 둘 다 유효한 값을 가진 경우)
	    if (productNo != null  && reviewNo != null) {
	        // 🚨 예외 발생 또는 비즈니스 로직에 따라 하나를 NULL 처리
	        throw new IllegalArgumentException("첨부 파일은 상품 또는 리뷰 중 하나에만 연결될 수 있습니다.");
	    }
	    
		Object[] params = {
				attachmentDto.getAttachmentNo(), attachmentDto.getAttachmentName(), 
				attachmentDto.getAttachmentSize(), attachmentDto.getAttachmentType(), 
				productNo, reviewNo
		};
		jdbcTemplate.update(sql, params);
	}
	
	public AttachmentDto selectOne(int attachmentNo) {
		String sql  = "select * from attachment where attachment_no = ?";
		Object[] param = {attachmentNo};
		List<AttachmentDto> list = jdbcTemplate.query(sql, attachmentMapper,  param);
		return list.isEmpty() ? null : list.get(0);
	}

	public boolean delete (int attachmentNo) {
		String sql = "delete from attachment where attachment_no = ?";
		Object[] param = {attachmentNo};
		return jdbcTemplate.update(sql, param) > 0;
	}
	
	public List<AttachmentDto> selectListByReviewNo(int reviewNo) {
		String sql = "select * from attachment where review_no = ?";
		Object[] params = {reviewNo};
		return jdbcTemplate.query(sql, attachmentMapper, params);
	}
	
	public int deleteByReviewNo(int reviewNo) {
	    String sql = "delete from attachment where review_no = ?";
	    Object[] params = {reviewNo};
	    return jdbcTemplate.update(sql, params);
	}
	
}
