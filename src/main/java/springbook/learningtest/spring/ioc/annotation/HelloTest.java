package springbook.learningtest.spring.ioc.annotation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import springbook.learningtest.spring.ioc.bean.Hello;

@Configuration(value="springbook/learningtest/spring/ioc/resource.xml")
public class HelloTest {

	@Test
	public void simpleAutowired() {
		AbstractApplicationContext ac = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.annotation");
		Hello hello = ac.getBean(Hello.class);
		hello.print();
	}
	

}
