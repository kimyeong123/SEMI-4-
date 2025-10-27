package com.kh.shoppingmall.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.CsBoardDto;
import com.kh.shoppingmall.mapper.CsBoardListMapper;
import com.kh.shoppingmall.mapper.CsBoardMapper;
import com.kh.shoppingmall.vo.CsBoardListVO;
import com.kh.shoppingmall.vo.PageVO;

@Repository
public class CsBoardDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CsBoardMapper csBoardMapper;
	@Autowired
	private CsBoardListMapper csBoardListMapper;
	
	public void insert(CsBoardDto csBoardDto) {
		String sql = "insert into "
				+ "cs_board("
				+ "cs_board_no, cs_board_title, cs_board_writer, "
				+ "cs_board_wtime, cs_board_content, cs_board_notice, "
				+ "cs_board_group, cs_board_origin, cs_board_depth "
				+ ") "
				+ "values(?, ?, ?, systimestamp, ?, ?, ?, ?, ?)"; //기존 방식
		Object[] params = {
				csBoardDto.getCsBoardNo() ,csBoardDto.getCsBoardTitle(), csBoardDto.getCsBoardWriter(),
				 csBoardDto.getCsBoardContent(), csBoardDto.getCsBoardNotice(), csBoardDto.getCsBoardGroup(), 
				 csBoardDto.getCsBoardOrigin(), csBoardDto.getCsBoardDepth()
		};
		
		jdbcTemplate.update(sql, params);
	}
	


	
	public List<CsBoardDto> selectListByPage(int begin, int pageSize) {
		String sql = "select * from (select rownum rnum, a * from cs_board a order by cs_board_no desc) where rnum between ? and ?";
		Object[] params = {begin, pageSize};
		
		return jdbcTemplate.query(sql, csBoardMapper, params);
	}
	
	public CsBoardDto selectOne(int csBoardNo) {
		String sql = "select * from cs_board where cs_board_no = ?";
		Object[] param = {csBoardNo};
		List<CsBoardDto>list = jdbcTemplate.query(sql, csBoardMapper, param);		

		return list.isEmpty() ? null : list.get(0);
	}

	public boolean update (CsBoardDto csBoardDto) {
		String sql  = "update cs_board set cs_board_title = ?, cs_board_etime = systimestamp, "
				+ "cs_board_content = ?, cs_board_notice = ?, where cs_board_no = ?";
		Object[] params = {csBoardDto.getCsBoardTitle(), csBoardDto.getCsBoardContent(), 
				csBoardDto.getCsBoardNotice(), csBoardDto.getCsBoardNo()};
		
		return jdbcTemplate.update(sql, params) > 0;
	}

	
	public boolean updateReadCount (int csBoardNo) {
		String sql = "update cs_board set cs_board_read = cs_board_read+1 where cs_board_no = ?";
		Object[] param = {csBoardNo};
		
		return jdbcTemplate.update(sql, param) > 0;
	}
	
	public int getSeq()
	{
		String sql  = "select cs_board_seq.nextval from dual";
		//int 같은 기본데이터는 mapper없이 조회 가능
		return jdbcTemplate.queryForObject(sql, int.class);
	}
	
	public boolean delete(int csBoardNo) {
		String sql = "delete cs_board where cs_board_no = ?";
		Object[] param = {csBoardNo};

		return jdbcTemplate.update(sql, param) > 0;
	}
	
	public int articleCount() {
		String sql = "select count(*) from cs_board";
		 return jdbcTemplate.queryForObject(sql, int.class);
	}

	//목록 + 페이징
	public List<CsBoardListVO> selectListWithPaging(PageVO pageVO) {
		//목록
		if(pageVO.isList()) {
			String sql = "select * from ("
					+ "select rownum rn, TMP. * from ("
					+"select * from cs_board_list "
					+ "connect by prior cs_board_no = cs_board_origin "
					+ "start with cs_board_origin is null "
					+ "order siblings by cs_board_group desc, cs_board_no asc"
					+ ")TMP"
				+ ") where rn between ? and ?";
			Object[] params = {pageVO.getBegin(), pageVO.getEnd()};
			return jdbcTemplate.query(sql, csBoardListMapper, params);
		}
		//검색
		else {
			String sql = "select * from ("
					+ "select rownum rn, TMP. * from ("
						+ "select * from cs_board_list "
						+ "where instr(#1, ?) > 0 "
						+ "connect by prior cs_board_no = cs_board_origin "
						+ "start with cs_board_origin is null "
						+ "order siblings by cs_board_group desc, cs_board_no asc"
					+ ") TMP "
			+ ") where rn between ? and ?";
			sql = sql.replace("#1", pageVO.getColumn());
			Object[] params = {pageVO.getKeyword(), pageVO.getBegin(), pageVO.getEnd()};
			return jdbcTemplate.query(sql, csBoardListMapper, params);
		}

	}
	
	//페이지 네비게이터 구현에 필요한 카운트 구하는 메소드(목록 검색 따로)
	public int count(PageVO pageVO) {
		//목록인 경우
		if(pageVO.isList()) {
			String sql = "select count(*) from cs_board";	
			return (Integer) jdbcTemplate.queryForObject(sql, int.class);
		}
		//검색인 경우
		else {
			String sql = "select count(*) from cs_board where instr(#1, ?) > 0";
			sql = sql.replace("#1", pageVO.getColumn());
			Object[] param = {pageVO.getKeyword()};
			
			return (Integer) jdbcTemplate.queryForObject(sql, int.class, param);
		}
		
	}
	
	public int  count() {
		String sql = "select count(*) from cs_board";	
		return (Integer) jdbcTemplate.queryForObject(sql, int.class);
	}
	
	public int count(String column, String keyword) {
		String sql = "select count(*) from cs_board where(#1, ?) > 0";
		sql = sql.replace("#1", column);
		Object[] param = {keyword};
		
		return (Integer) jdbcTemplate.queryForObject(sql, int.class, param);
	}
	
	//공지사항 조회 메소드
	public List<CsBoardListVO> selectListNotice(PageVO pageVO) {
		if(pageVO.isList()) {
			String sql = "select * from cs_board_list "
					+" where cs_board_notice = 'Y' order by cs_board_no desc";
			return jdbcTemplate.query(sql, csBoardListMapper);
		}
		else {
			String sql = "select * from cs_board_list "
				+ "where cs_board_notice = 'Y' and instr(#1, ?) > 0 "
				+ "order by cs_board_no desc";
			sql = sql.replace("#1", pageVO.getColumn());
			Object[] param = {pageVO.getKeyword()};
			return jdbcTemplate.query(sql, csBoardListMapper, param);
		}
	}
	
	public List<CsBoardListVO> selectListByMember(String csBoardWriter) {
		if(csBoardWriter == null) return List.of();
		String sql = "select * from cs_board_list where cs_board_writer = ? order by cs_board_no desc";
		Object[] param = {csBoardWriter};
		
		return jdbcTemplate.query(sql, csBoardListMapper, param);
	}
	
}
















