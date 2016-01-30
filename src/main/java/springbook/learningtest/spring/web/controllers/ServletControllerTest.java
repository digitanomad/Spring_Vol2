package springbook.learningtest.spring.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class ServletControllerTest extends AbstractDispatcherServletTest {
	
	@Test
	public void helloServletController() throws UnsupportedEncodingException, ServletException, IOException {
		// 핸들러 어댑터와 컨트롤러를 빈으로 등록해준다.
		setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class);
		initRequest("/hello").addParameter("name", "Spring");
		
		assertThat(runService().getContentAsString(), is("Hello Spring"));
	}
	
	@Component("/hello")
	static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// GET 메소드를 통해 전달받은 name 파라미터 값을 이용해서 메시지를 만든 후에
			// HttpServletResponse에 넣어준다.
			String name = req.getParameter("name");
			resp.getWriter().print("Hello " + name);
		}
	}

}
