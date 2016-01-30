package springbook.learningtest.spring.ioc.annotation;

import javax.annotation.Resource;

public class Hello {
	
	String name;
	Printer printer;
	
	public Hello() {
	}
	
	public Hello(String name, Printer printer) {
		this.name = name;
		this.printer = printer;
	}
	
	public String sayHello() {
		return "Hello " + name;
	}
	
	public void print() {
		this.printer.print(sayHello());
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// <property name="printer" ref="printer" />와 동일한 의존관계 메타정보로 변환된다.
	@Resource(name = "printer")
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	
}
