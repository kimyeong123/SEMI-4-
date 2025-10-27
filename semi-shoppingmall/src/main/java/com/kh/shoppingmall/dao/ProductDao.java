package com.kh.shoppingmall.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.mapper.ProductMapper;

@Repository
public class ProductDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ProductMapper productMapper;

	public int sequence() {
		String sql = "select product_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	// 상품 등록
	public void insert(ProductDto dto) {
		String sql = "insert into product(product_no, product_name, product_price, product_content, product_thumbnail_no) "
				+ "values(?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, dto.getProductNo(), dto.getProductName(), dto.getProductPrice(),
				dto.getProductContent(), dto.getProductThumbnailNo());

	}

	// 상품 수정
	public boolean update(ProductDto dto) {
		String sql = "update product set product_name=?, product_price=?, product_content=?, product_thumbnail_no=? "
				+ "where product_no=?";
		return jdbcTemplate.update(sql, dto.getProductName(), dto.getProductPrice(), dto.getProductContent(),
				dto.getProductThumbnailNo(), dto.getProductNo()) > 0;
	}

	// 상품 삭제
	public boolean delete(int productNo) {
		String sql = "delete from product where product_no=?";
		return jdbcTemplate.update(sql, productNo) > 0;
	}

	// ---------------- 상품 목록 조회 (정렬 포함) ----------------

	// 1. 핵심 메서드: 검색, 정렬을 모두 처리 (3개 인자)
	public List<ProductDto> selectList(String column, String keyword, String order) {
	    // 1. 정렬 가능한 컬럼 화이트리스트 (DB 컬럼명과 정확히 일치해야 함)
	    // Controller에서 넘어오는 값과 이 Set의 값이 일치하는지 다시 확인해주세요.
	    Set<String> sortableColumns = Set.of("product_name", "product_price", "product_avg_rating", "product_no");
	    
	    // 2. 정렬 기준 컬럼 설정: 유효한 값이 아니면 product_no를 기본값으로 사용
	    String sortColumn = (column != null && sortableColumns.contains(column)) ? column : "product_no";
	    
	    // 정렬 방향 설정
	    String sortOrder = ("asc".equalsIgnoreCase(order)) ? "asc" : "desc";

	    StringBuilder sql = new StringBuilder();
	    List<Object> params = new ArrayList<>();
	    
	    sql.append("select p.*, ");
	    sql.append("(select avg(review_rating) from review where product_no = p.product_no) as product_avg_rating ");
	    sql.append("from product p ");
	    
	    // 3. 검색어 조건 처리
	    if (keyword != null && !keyword.isEmpty()) {
	        // 검색은 항상 'product_name'으로 하거나,
	        // 'column'이 검색 가능한 컬럼인 경우에만 검색하도록 단순화해야 합니다.
	        
	        // ⭐ 수정된 검색 로직: 'column'을 '검색 기준'으로 사용하지 않고, 
	        //    'keyword'만 있으면 'product_name'으로 검색하도록 단순화합니다.
	        //    (혹은 컨트롤러에서 넘어오는 searchColumn을 따로 처리해야 합니다.)
	        
	        String searchColumn = "product_name"; // 기본 검색 컬럼
	        
	        // 만약 'column'이 검색 컬럼이었으면, 다음과 같이 로직을 설정할 수 있습니다.
	        if (Set.of("product_content").contains(column)) { 
	            searchColumn = "product_content"; // 'column'이 검색 컬럼으로 사용된 경우
	        } 
	        
	        sql.append("where instr(lower(" + searchColumn + "), lower(?)) > 0 ");
	        params.add(keyword);
	    }
	    
	    // 4. 동적 정렬 조건 처리 (여기서는 sortColumn이 화이트리스트를 통과했으므로 문제 없음)
	    sql.append("order by ").append(sortColumn).append(" ").append(sortOrder);

	    return jdbcTemplate.query(sql.toString(), params.toArray(), productMapper);
	}

	// 2. 전체 목록 (인자 없음): 핵심 메서드 호출로 대체
	public List<ProductDto> selectList() {
	    // 기본 정렬(상품 번호, 오름차순)을 적용하여 핵심 메서드 호출
	    return selectList("product_no", null, "asc"); 
	}

	// 3. 검색 (2개 인자): 핵심 메서드 호출로 대체
	public List<ProductDto> selectList(String column, String keyword) {
	    // 검색 결과에 기본 정렬(상품 번호, 오름차순)을 적용하여 핵심 메서드 호출
	    return selectList(column, keyword, "asc");
	}

	
	// 단일 조회
	public ProductDto selectOne(int productNo) {
		String sql = "select * from product where product_no=?";
		List<ProductDto> list = jdbcTemplate.query(sql, productMapper, productNo);
		return list.isEmpty() ? null : list.get(0);
	}

	// 썸네일 연결
	public void connectThumbnail(int productNo, int attachmentNo) {
		String sql = "insert into product_image(product_no, attachment_no) values(?, ?)";
		jdbcTemplate.update(sql, productNo, attachmentNo);
	}

	// 상품 번호로 썸네일 조회
	public Integer findThumbnail(int productNo) {
		String sql = "select product_thumbnail_no from product where product_no=?";
		List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, productNo);
		return list.isEmpty() ? null : list.get(0);
	}

	// ---------------- 상품 번호로 상세 이미지 목록 조회 ----------------
	public List<Integer> findDetailAttachments(int productNo) {
		String sql = "select attachment_no from attachment where product_no = ?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	// 평균 평점 계산
	public Double calculateAverageRating(int productNo) {
		String sql = "select avg(review_rating) from review where product_no = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { productNo }, Double.class);
	}

	public void updateAverageRating(int productNo, Double avgRating) {
		String sql = "update product set product_avg_rating=? where product_no=?";
		jdbcTemplate.update(sql, avgRating, productNo);
	}

	// 상품의 리뷰 번호 리스트 조회
	public List<Integer> findReviewsByProduct(int productNo) {
		String sql = "select review_no from review where product_no=?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	// 리뷰 삭제
	public void deleteReview(int reviewNo) {
		String sql = "delete from review where review_no=?";
		jdbcTemplate.update(sql, reviewNo);
	}

	public List<Integer> findWishlistIdsByProduct(int productNo) {
		String sql = "select wishlist_no from wishlist where product_no = ?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	public void deleteWishlist(int wishlistNo) {
		String sql = "delete from wishlist where wishlist_no = ?";
		jdbcTemplate.update(sql, wishlistNo);
	}

	public List<ProductDto> selectByCategories(List<Integer> categoryNos, String column, String order) {
	    
	    // 1. 카테고리 목록 유효성 검사
	    if (categoryNos == null || categoryNos.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // 2. SQL IN 절을 위한 placeholder (?, ?, ...) 생성
	    String placeholders = categoryNos.stream()
	                                     .map(n -> "?")
	                                     .collect(Collectors.joining(","));
	                                     
	    // 3. SQL 쿼리 빌드
	    StringBuilder sql = new StringBuilder();
	    sql.append("select distinct p.* from product p ");
	    sql.append("join product_category_map pcm on p.product_no = pcm.product_no ");
	    sql.append("where pcm.category_no in (" + placeholders + ") "); 
	    
	    // 4. 정렬 조건 처리 (order by) 
	    if (column != null && !column.isEmpty()) {
	        // p.을 붙여 테이블 별칭을 명시하고, column으로 정렬합니다. 
	        // Controller에서 넘어오는 column 값이 DB 컬럼명과 일치해야 합니다.
	        sql.append("order by p.").append(column); 
	        
	        // 정렬 방향 결정 (소문자 SQL 구문 적용)
	        if ("asc".equalsIgnoreCase(order)) {
	            sql.append(" asc");
	        } else {
	            sql.append(" desc"); 
	        }
	    } else {
	        // 정렬 기준이 없을 경우 기본 정렬 (상품 번호 오름차순)
	        sql.append("order by p.product_no asc");
	    }

	    // 5. 파라미터 설정 및 쿼리 실행
	    Object[] params = categoryNos.toArray();

	    // 6. 쿼리 실행 및 결과 반환
	    return jdbcTemplate.query(sql.toString(), productMapper, params);
	}

}