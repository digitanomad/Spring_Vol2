package springbook.learningtest.spring31.web;

import java.util.List;

import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfigurerTest {

	static class MyWebMvcConfig implements WebMvcConfigurer {

		@Override
		public void addFormatters(FormatterRegistry registry) {
		}

		@Override
		public void configureMessageConverters(
				List<HttpMessageConverter<?>> converters) {
		}

		@Override
		public Validator getValidator() {
			return null;
		}

		@Override
		public void addArgumentResolvers(
				List<HandlerMethodArgumentResolver> argumentResolvers) {
		}

		@Override
		public void addReturnValueHandlers(
				List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		}

		@Override
		public void configureHandlerExceptionResolvers(
				List<HandlerExceptionResolver> exceptionResolvers) {
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
		}

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/main");
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/ui/**").addResourceLocations("classpath:/META-INF/webresources/");
		}

		@Override
		public void configureDefaultServletHandling(
				DefaultServletHandlerConfigurer configurer) {
			configurer.enable();
		}
		
	}
}
