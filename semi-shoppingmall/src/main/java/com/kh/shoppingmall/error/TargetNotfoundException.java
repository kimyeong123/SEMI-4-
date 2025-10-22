package com.kh.shoppingmall.error;

public class TargetNotfoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	//사용처: 대상이 없어 더이상 진행 불가한 경우
	public TargetNotfoundException() {
		super();
	}

	public TargetNotfoundException(String message) {
		super(message);
	}

}
