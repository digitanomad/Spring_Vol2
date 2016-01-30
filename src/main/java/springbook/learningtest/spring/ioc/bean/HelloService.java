package springbook.learningtest.spring.ioc.bean;

import org.springframework.context.annotation.Bean;

public class HelloService {
	private Printer printer;
	
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	
	@Bean
	public Hello hello() {
		Hello hello = new Hello();
		hello.setPrinter(this.printer);
		return hello;
	}
	
	@Bean
	public Hello hello2() {
		Hello hello = new Hello();
		hello.setPrinter(this.printer);
		return hello;
	}
	
	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}
