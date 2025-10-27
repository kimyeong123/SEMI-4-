package com.kh.shoppingmall.vo;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CsBoardListVO {
	private int CsBoardNo;
	private String CsBoardTitle;
	//외래키
	private String CsBoardWriter;
	private Timestamp CsBoardWtime;
	private Timestamp CsBoardEtime;
	//컨텐츠 삭제
	private int CsBoardRead;
	private int CsBoardLike;
	private int CsBoardReply;
	@Builder.Default
	private String CsBoardNotice = "N";
	private int CsBoardGroup;
	private Integer CsBoardOrigin;
	private int CsBoardDepth;
	
	//EL에서 ${boardDto.boardWriteTime}으로부를 수 있는 메소드
	public String getBoardWriteTime()
	{
		LocalDateTime wtime = CsBoardWtime.toLocalDateTime();
		LocalDate today = LocalDate.now();
		LocalDate wday = wtime.toLocalDate();
		if(wday.isBefore(today))
		{
			return wtime.toLocalDate().toString();
		}
		else
		{
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
			return wtime.toLocalTime().format(fmt);			
		}
	}
	
}
