package springbook.learningtest.spring.remote;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class RestTemplateTest {

	@Test
	public void openApi() {
		RestTemplate template = new RestTemplate();
		
		String key = "";
		
		String result = template.getForObject("http://apis.daum.net/search/blog?apikey=" + key +"&q={key}&output={output}", String.class, "SpringFramework", "json");
		System.out.println(result);
		assertThat(result.contains("\"reuslt\":\"10\""), is(true));
	}
}
