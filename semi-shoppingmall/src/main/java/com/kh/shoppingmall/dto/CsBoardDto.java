package com.kh.shoppingmall.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CsBoardDto {
	private int csBoardNo;
	private String csBoardTitle;
	//외래키
	private String csBoardWriter;
	private Timestamp csBoardWtime;
	private Timestamp csBoardEtime;
	private String csBoardContent;
	//필요 없어서 삭제
//	private int csBoardRead;
//	private int csBoardLike;
//	private int csBoardReply;
	@Builder.Default
	private String csBoardNotice = "N";
	private int csBoardGroup;
	private Integer csBoardOrigin;
	private int csBoardDepth;
	
	// [추가] 비공개 여부 필드
	@Builder.Default
    private String csBoardSecret = "N";
	
	//EL에서 ${boardDto.boardWriteTime}으로부를 수 있는 메소드
	public String getBoardWriteTime()
	{
		LocalDateTime wtime = csBoardWtime.toLocalDateTime();
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
