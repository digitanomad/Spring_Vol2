package springbook.learningtest.spring.web.controllers;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class HandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void simpleHandlerAdapter() throws ServletException, IOException {
		setClasses(SimpleHandlerAdapter.class, HelloController.class);
		initRequest("/hello").addParameter("name", "Spring").runService();
		assertViewName("/WEB-INF/view/hello.jsp");
		assertModel("message", "Hello Spring");
	}
	
	static class SimpleHandlerAdapter implements HandlerAdapter {
		// 이 핸들러 어댑터가 지원하는 타입을 확인해준다.
		// 하나 이상의 타입을 지원하게 할 수도 있다.
		@Override
		public boolean supports(Object handler) {
			return (handler instanceof SimpleController);
		}

		@Override
		public ModelAndView handle(HttpServletRequest request,
				HttpServletResponse response, Object handler) throws Exception {
			Method m = ReflectionUtils.findMethod(handler.getClass(), "control", Map.class, Map.class);
			// 컨트롤러 메소드의 애노테이션에서 필요한 정보를 가져온다.
			// 스프링 유틸리티 클래스를 이용해 간단히 애노테이션을 가져왔다.
			ViewName viewName = AnnotationUtils.getAnnotation(m, ViewName.class);
			RequiredParams requiredParams = AnnotationUtils.getAnnotation(m, RequiredParams.class);
			
			Map<String, String> params = new HashMap<String, String>();
			for (String param : requiredParams.value()) {
				String value = request.getParameter(param);
				if (value == null) {
					throw new IllegalStateException();
				}
				params.put(param, value);
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			// DispatcherServlet은 컨트롤러의 타입을 모르기 때문에 컨트롤러를 Object 타입으로 넘겨준다.
			// 이를 적절한 컨트롤러 타입으로 캐스팅해서 메소드를 호출해준다.
			((SimpleController)handler).control(params, model);
			
			return new ModelAndView(viewName.value(), model);
		}

		// getLastModified()는 컨트롤러의 getLastModified() 메소드를 다시 호출해서 컨트롤러가 결정하도록 만든다.
		// 캐싱을 적용하지 않으려면 0보다 작은 값을 리턴한다.
		// getLastModified()의 지원은 없는 것으로 만들었다.
		@Override
		public long getLastModified(HttpServletRequest request, Object handler) {
			return -1;
		}
		
	}

	@Component("/hello")
	static class HelloController implements SimpleController {
		@ViewName("/WEB-INF/view/hello.jsp")
		@RequiredParams({"name"})
		@Override
		public void control(Map<String, String> params,
				Map<String, Object> model) {
			model.put("message", "Hello " + params.get("name"));
		}
		
	}
	
	
	public interface SimpleController {
		void control(Map<String, String> params, Map<String, Object> model);
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface ViewName {
		String value();
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface RequiredParams {
		String[] value();
	}
}
