package springbook.learningtest.spring.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class SimpleControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void helloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("/WEB-INF/view/hello.jsp");
	}
	
	@Test(expected=Exception.class)
	public void noParameterHelloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello");
		runService();
	}
	
	@Test
	public void helloControllerUnitTest() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "Spring");
		Map<String, Object> model = new HashMap<String, Object>();
		
		new HelloController().control(params, model);
		
		assertThat((String)model.get("message"), is("Hello Spring"));
	}
	
	@RequestMapping("/hello")
	static class HelloController extends SimpleController {
		public HelloController() {
			this.setRequiredParams(new String[] { "name" });
			this.setViewName("/WEB-INF/view/hello.jsp");
		}
		
		@Override
		public void control(Map<String, String> params,
				Map<String, Object> model) throws Exception {
			model.put("message", "Hello " + params.get("name"));
		}
		
	}
	
	static abstract class SimpleController implements Controller {
		// 필수 파라미터를 정의한다. 이 파라미터만 control() 메소드로 전달한다.
		private String[] requiredParams;
		private String viewName;
		
		public void setRequiredParams(String[] requiredParams) {
			this.requiredParams = requiredParams;
		}
		
		public void setViewName(String viewName) {
			this.viewName = viewName;
		}
		
		@Override
		final public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			// 뷰 이름 프로퍼티가 지정되지 않았으면 예외를 발생시킨다.
			if (viewName == null) {
				throw new IllegalStateException();
			}
			
			Map<String, String> params = new HashMap<String, String>();
			for (String param : requiredParams) {
				// 필요한 파라미터를 가져와 맵에 담는다.
				// 존재하지 않으면 예외를 던진다.
				String value = request.getParameter(param);
				if (value == null) {
					throw new IllegalStateException();
				}
				params.put(param, value);
			}
			
			// 모델용 맵 오브젝트를 미리 만들어서 전달해준다.
			Map<String, Object> model = new HashMap<String, Object>();
			
			// 개별 컨트롤러가 구현할 메소드를 호출해준다.
			this.control(params, model);
			
			// Controller 인터페이스의 정의에 따라 ModelAndView 타입의 결과를 돌려준다.
			return new ModelAndView(this.viewName, model);
		}
		
		// 서브클래스가 구현할 실제 컨트롤러 로직을 담을 메소드다.
		public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
	}
}
