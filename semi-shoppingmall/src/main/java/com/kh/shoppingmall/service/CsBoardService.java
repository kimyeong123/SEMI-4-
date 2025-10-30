package com.kh.shoppingmall.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.shoppingmall.vo.CsBoardListVO;

@Service
public class CsBoardService {
	
	public void setTimeFlags(List<CsBoardListVO> inputListVo) {
		LocalDateTime now = LocalDateTime.now();
		//현재 시간 비교해 플래그 값 설정
		for(CsBoardListVO vo: inputListVo) {
			LocalDateTime wTime = vo.getCsBoardWtime().toLocalDateTime();
			vo.setWtimeRecent(ChronoUnit.HOURS.between(wTime, now) < 24);
			
			if(vo.getCsBoardEtime() != null) {
				LocalDateTime eTime = vo.getCsBoardEtime().toLocalDateTime();
				vo.setEtimeRecent(ChronoUnit.HOURS.between(eTime, now) < 24);
			}
			else {
				vo.setEtimeRecent(false);
			}
		}
	}
	
	public List<CsBoardListVO> initializeListIfNull(List<CsBoardListVO> inputListVO) {
		if (inputListVO == null) {
			// DAO가 null을 반환하면 빈 리스트로 초기화 (JSP 오류 방지)
			return inputListVO = new ArrayList<>(); 
		}
		return inputListVO;
	}

}
