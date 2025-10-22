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

    // 1. 첨부파일 번호(시퀀스) 생성
    // - 첨부파일을 저장하기 전에 고유번호(attachment_no)를 먼저 확보해야 함
    public int sequence() {
        String sql = "select attachment_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    // 2. 첨부파일 정보 저장
    // - 실제 파일은 서비스에서 저장되고, 이 메서드는 DB에 메타정보만 저장
    public void insert(AttachmentDto attachmentDto) {
        String sql = "insert into attachment("
                   + "attachment_no, attachment_name, "
                   + "attachment_size, attachment_type, "
                   + "product_no, review_no"
                   + ") values(?, ?, ?, ?, ?, ?)";

        // 상품번호 또는 리뷰번호가 0이면 null로 변환 (DB 저장용)
        Integer productNo = attachmentDto.getProductNo() == 0 ? null : attachmentDto.getProductNo();
        Integer reviewNo = attachmentDto.getReviewNo() == 0 ? null : attachmentDto.getReviewNo();

        // 상품번호와 리뷰번호가 동시에 존재하면 예외 발생 (첨부파일은 둘 중 하나에만 연결되어야 함)
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

    // 3. 첨부파일 정보 조회 (첨부파일 번호로)
    public AttachmentDto selectOne(int attachmentNo) {
        String sql  = "select * from attachment where attachment_no = ?";
        Object[] param = {attachmentNo};
        List<AttachmentDto> list = jdbcTemplate.query(sql, attachmentMapper, param);
        return list.isEmpty() ? null : list.get(0); // 결과가 없으면 null 반환
    }

    // 4. 첨부파일 삭제 (DB 기록만 삭제, 실물 파일은 서비스에서 삭제됨)
    public boolean delete(int attachmentNo) {
        String sql = "delete from attachment where attachment_no = ?";
        Object[] param = {attachmentNo};
        return jdbcTemplate.update(sql, param) > 0;
    }

    // 5. 첨부파일에 상품번호를 연결 (update)
    // - 상품 상세 이미지 등록 후 해당 파일을 어떤 상품에 연결할지 지정
    public boolean updateProductNo(int attachmentNo, int productNo) {
        String sql = "UPDATE attachment SET product_no = ? WHERE attachment_no = ?";
        Object[] params = { productNo, attachmentNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

}
