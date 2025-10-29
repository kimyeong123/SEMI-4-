package com.kh.shoppingmall.vo;

import lombok.Data;

@Data
public class PageVO {
	//필드의 페이징에 필요한 데이터들을 배치
	private int page = 1;
	private int size = 10;
	private String column, keyword;
	
	private int dataCount;
	private int blockSize = 10;
	
	//계산이 가능하게 getter 메소드 추가 생성
	public boolean isSearch()
	{
		return column != null && keyword != null;
	}
	
	public boolean isList()
	{
		return column == null || keyword == null;
	}
	
	public String getSearchParams() //목록 or 검색 여부에 따라 추가되는 파라미터 변환
	{
		if(isSearch())
			return "size="+size+"&column="+column+"&keyword="+keyword;
		else
		{
			return "size="+size;
		}
	}
	
	public int getBlockStart() //블록의 시작 번호
	{
		return (page-1) / blockSize * blockSize + 1;
	}
//	public int getBlockFinish() //블록의 종료 번호
//	{
//		return (page-1) / blockSize * blockSize + blockSize;
//	}
	
	public int getBlockFinish()
	{
		int finish = (page-1) / blockSize * blockSize + blockSize;
		if(finish > getTotalPage()) 
		{
			finish = getTotalPage();
		}
		return finish;
	}
	
	public int getTotalPage()
	{
		return (dataCount - 1) / size + 1;
	}
	
	public int getBegin()
	{
		return page * size - (size - 1 );
	}
	
	public int getEnd()
	{
		return page * size; 
	}
	
	//꼭 필요하진 않지만 가독성을 올릴 수 있는 메서드
	public boolean isFirstBlock()
	{
		return getBlockStart() == 1;
	}
	
	public int getPrevPage()
	{
		return getBlockStart() - 1;
	}
	
	public int getNextPage()
	{
		return getBlockFinish() + 1;
	}
	
	public boolean isLastBlock()
	{
		return getBlockFinish() == getTotalPage();
	}
	
}
