package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.shoppingmall.dao.WishlistDao;
import com.kh.shoppingmall.vo.WishlistDetailVO;

@Service
public class WishlistService {
    @Autowired private WishlistDao wishlistDao;

    // 1. 찜 목록 추가 (핵심 로직)
    public boolean addItem(String memberId, int productNo) {
        // 이미 찜했는지 확인
        boolean alreadyExists = wishlistDao.check(memberId, productNo);

        if (alreadyExists) {
            // 이미 찜했으면 아무것도 안 하거나, 사용자에게 알림
            return false; // 예: 이미 추가됨을 알리는 false 반환
        } else {
            // 찜하지 않았으면 추가
            wishlistDao.insert(memberId, productNo);
            return true; // 예: 추가 성공을 알리는 true 반환
        }
    }

    // 2. 내 찜 목록 조회 (상품 정보 포함)
    public List<WishlistDetailVO> getWishlistItems(String memberId) {
        return wishlistDao.selectDetailListByMemberId(memberId); // 뷰(View) 조회
    }

    // 3. 찜 목록에서 삭제
    public boolean removeItem(String memberId, int productNo) {
        return wishlistDao.delete(memberId, productNo);
    }

    // 4. 특정 상품 찜 여부 확인 (상품 상세 페이지 등에서 사용)
    public boolean checkItem(String memberId, int productNo) {
        return wishlistDao.check(memberId, productNo);
    }



}
