package springbook.learningtest.spring.ioc.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	@Value("${database.username")
	private String name;

	@Bean
	public Hello hello(Printer printer) {
		Hello hello = new Hello();
		hello.setName(name);
		hello.setPrinter(printer);
		return hello;
	}
	
	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}
