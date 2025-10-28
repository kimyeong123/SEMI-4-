package com.kh.shoppingmall.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.kh.shoppingmall.dto.BannerDto;

@Repository
public class BannerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<BannerDto> mapper = (rs, rowNum) -> BannerDto.builder()
            .bannerNo(rs.getInt("banner_no"))
            .bannerTitle(rs.getString("banner_title"))
            .bannerLink(rs.getString("banner_link"))
            .bannerOrder(rs.getInt("banner_order"))
            .bannerAttachmentNo(rs.getInt("banner_attachment_no"))
            .build();

    public void insert(BannerDto dto) {
        String sql = "INSERT INTO banner VALUES(banner_seq.NEXTVAL, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, dto.getBannerTitle(), dto.getBannerLink(), dto.getBannerOrder(), dto.getBannerAttachmentNo());
    }

    public List<BannerDto> selectList() {
        String sql = "SELECT * FROM banner ORDER BY banner_order ASC";
        return jdbcTemplate.query(sql, mapper);
    }

    public void delete(int bannerNo) {
        String sql = "DELETE FROM banner WHERE banner_no = ?";
        jdbcTemplate.update(sql, bannerNo);
    }
    public BannerDto selectOne(int bannerNo) {
        String sql = "SELECT * FROM banner WHERE banner_no = ?";
        List<BannerDto> list = jdbcTemplate.query(sql, mapper, bannerNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public void update(BannerDto dto) {
        String sql = "UPDATE banner SET banner_title=?, banner_link=?, banner_order=?, banner_attachment_no=? WHERE banner_no=?";
        jdbcTemplate.update(sql,
            dto.getBannerTitle(),
            dto.getBannerLink(),
            dto.getBannerOrder(),
            dto.getBannerAttachmentNo(),
            dto.getBannerNo());
    }
}
