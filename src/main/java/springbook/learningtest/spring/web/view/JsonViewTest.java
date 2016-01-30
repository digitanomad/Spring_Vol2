package springbook.learningtest.spring.web.view;

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
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class JsonViewTest extends AbstractDispatcherServletTest {
	@Test
	public void jsonView() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		System.out.println(getContentAsString());
	}
	
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		MappingJacksonJsonView jacksonJsonView = new MappingJacksonJsonView();
		
		@Override
		public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Map<String, Object> model= new HashMap<String, Object>();
			model.put("messages", "Hello " + request.getParameter("name"));
			
			return new ModelAndView(jacksonJsonView, model);
		}
		
	}
}
