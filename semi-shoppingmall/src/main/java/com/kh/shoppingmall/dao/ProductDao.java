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
		String sql = "SELECT product_seq.NEXTVAL FROM DUAL";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	// 상품 등록
	public void insert(ProductDto dto) {
		String sql = "INSERT INTO product(product_no, product_name, product_price, product_content, product_thumbnail_no) "
				+ "VALUES(?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, dto.getProductNo(), dto.getProductName(), dto.getProductPrice(),
				dto.getProductContent(), dto.getProductThumbnailNo());

	}

	// 상품 수정
	public boolean update(ProductDto dto) {
		String sql = "UPDATE product SET product_name=?, product_price=?, product_content=?, product_thumbnail_no=? "
				+ "WHERE product_no=?";
		return jdbcTemplate.update(sql, dto.getProductName(), dto.getProductPrice(), dto.getProductContent(),
				dto.getProductThumbnailNo(), dto.getProductNo()) > 0;
	}

	// 상품 삭제
	public boolean delete(int productNo) {
		String sql = "DELETE FROM product WHERE product_no=?";
		return jdbcTemplate.update(sql, productNo) > 0;
	}

	// 전체 목록
	public List<ProductDto> selectList() {
		String sql = "SELECT * FROM product ORDER BY product_no ASC";
		return jdbcTemplate.query(sql, productMapper);
	}

	// 검색
	public List<ProductDto> selectList(String column, String keyword) {
		Set<String> allowColumns = Set.of("product_name", "product_content");
		if (!allowColumns.contains(column)) {
			column = "product_name"; // 기본 검색 컬럼
		}

		String sql = "SELECT * FROM product WHERE INSTR(LOWER(" + column + "), LOWER(?)) > 0 ORDER BY product_no ASC";
		return jdbcTemplate.query(sql, productMapper, keyword);
	}

	// 단일 조회
	public ProductDto selectOne(int productNo) {
		String sql = "SELECT * FROM product WHERE product_no=?";
		List<ProductDto> list = jdbcTemplate.query(sql, productMapper, productNo);
		return list.isEmpty() ? null : list.get(0);
	}

	// 썸네일 연결
	public void connectThumbnail(int productNo, int attachmentNo) {
		String sql = "INSERT INTO product_image(product_no, attachment_no) VALUES(?, ?)";
		jdbcTemplate.update(sql, productNo, attachmentNo);
	}

	// 상품 번호로 썸네일 조회
	public Integer findThumbnail(int productNo) {
		String sql = "SELECT product_thumbnail_no FROM product WHERE product_no=?";
		List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, productNo);
		return list.isEmpty() ? null : list.get(0);
	}

	// ---------------- 상품 번호로 상세 이미지 목록 조회 ----------------
	public List<Integer> findDetailAttachments(int productNo) {
		String sql = "SELECT attachment_no FROM attachment WHERE product_no = ?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	// 평균 평점 계산
	public Double calculateAverageRating(int productNo) {
		String sql = "select avg(review_rating) from review where product_no = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { productNo }, Double.class);
	}

	public void updateAverageRating(int productNo, Double avgRating) {
		String sql = "UPDATE product SET product_avg_rating=? WHERE product_no=?";
		jdbcTemplate.update(sql, avgRating, productNo);
	}

	// 상품의 리뷰 번호 리스트 조회
	public List<Integer> findReviewsByProduct(int productNo) {
		String sql = "SELECT review_no FROM review WHERE product_no=?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	// 리뷰 삭제
	public void deleteReview(int reviewNo) {
		String sql = "DELETE FROM review WHERE review_no=?";
		jdbcTemplate.update(sql, reviewNo);
	}

	public List<Integer> findWishlistIdsByProduct(int productNo) {
		String sql = "SELECT wishlist_no FROM wishlist WHERE product_no = ?";
		return jdbcTemplate.queryForList(sql, Integer.class, productNo);
	}

	public void deleteWishlist(int wishlistNo) {
		String sql = "DELETE FROM wishlist WHERE wishlist_no = ?";
		jdbcTemplate.update(sql, wishlistNo);
	}

	// 상품 카테고리별 조회
	// categoryNo와 하위 카테고리까지 조회
	public List<ProductDto> selectByCategories(List<Integer> categoryNos) {
	    if (categoryNos == null || categoryNos.isEmpty()) return new ArrayList<>();

	    String placeholders = categoryNos.stream()
	                                     .map(n -> "?")
	                                     .collect(Collectors.joining(","));
	    String sql = "select distinct p.* from product p " +
	                 "join product_category_map pcm on p.product_no = pcm.product_no " +
	                 "where pcm.category_no in (" + placeholders + ") " +
	                 "order by p.product_no asc";

	    return jdbcTemplate.query(sql, categoryNos.toArray(), productMapper);
	}

}
