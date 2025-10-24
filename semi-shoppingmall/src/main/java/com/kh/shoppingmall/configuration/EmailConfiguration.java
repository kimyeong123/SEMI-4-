package com.kh.shoppingmall.configuration;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//설정파일을 쓰면 내가 자주 쓰는 도구 등록이 가능
@Configuration
public class EmailConfiguration {
	//스프링서는 서버가 시작되면 @bean을 자동등록함
	@Bean
	public JavaMailSenderImpl sender() {
		//1 메일 발송 도구 생성
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		
		//2 서비스 제공자 정보 설정
		sender.setHost("smtp.gmail.com"); //이용할 업체의 호스트 정보
		sender.setPort(587); //이용할 업체의 포트 번호
		sender.setUsername("semiproject1001"); //이용할 업체의 사용자 계정 이릅
		sender.setPassword("mmyaffqydvyepyrg"); //지메일은 앱비번
		
		//추가옵션(지메일)
		Properties props = new Properties(); //추가정보 담을데 string, string 형태 맵
		props.setProperty("mail.smtp.auth", "true"); //이메일 발송에 인증사용(무조건 true)
		props.setProperty("mail.smtp.debug", "true"); //이메일 발송과정을 자세히 출력(오류해결용)
		props.setProperty("mail.smtp.starttls.enable", "true"); //STARTTLS 사용 (보안용 통신)
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); //TLS 방식 선택
		props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); //신뢰할 수 있는 인증서 발급자 지정
		sender.setJavaMailProperties(props);
		
		return sender;
	}
}