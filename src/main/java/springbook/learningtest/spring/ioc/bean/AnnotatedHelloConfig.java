package springbook.learningtest.spring.ioc.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotatedHelloConfig {

	@Bean
	// @Bean이 붙은 메소드 하나가 하나의 빈을 정의한다. 메소드 이름이 등록되는 빈의 이름이 된다.
	public AnnotatedHello annotatedHello() {
		// 자바 코드를 이용해 빈 오브젝트를 만들고, 초기화한 후에 리턴해준다.
		// 컨테이너는 이 리턴 오브젝트를 빈으로 활용한다.
		return new AnnotatedHello();
	}
}
