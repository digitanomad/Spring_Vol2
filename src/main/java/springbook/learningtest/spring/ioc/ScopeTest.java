package springbook.learningtest.spring.ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ScopeTest {

	@Test
	public void singletonScopre() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);
		
		// Set은 중복을 허용하지 않으므로 같은 오브젝트는 여러 번 추가해도 한 개만 남는다.
		Set<SingletonBean> beans = new HashSet<SingletonBean>();
		
		// DL에서 싱글톤 확인
		beans.add(ac.getBean(SingletonBean.class));
		beans.add(ac.getBean(SingletonBean.class));
		assertThat(beans.size(), is(1));
		
		// DI에서 싱글톤 확인
		beans.add(ac.getBean(SingletonClientBean.class).bean1);
		beans.add(ac.getBean(SingletonClientBean.class).bean2);
		assertThat(beans.size(), is(1));
	}
	
	@Test
	public void prototypeScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		
		// 프로토타입 빈은 DL 방식으로 컨터이너에 빈을 요청할 때마다 새로운 빈 오브젝트가 만들어지는 것을 확인한다.
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(1));
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(2));
		
		// 프로토타입 빈을 DI 할 때도 주입받는 프로퍼티마다 다른 오브젝트가 만들어지는 것을 확인한다.
		beans.add(ac.getBean(PrototypeClientBean.class).bean1);
		assertThat(beans.size(), is(3));
		beans.add(ac.getBean(PrototypeClientBean.class).bean2);
		assertThat(beans.size(), is(4));
	}
	
	static class SingletonBean {}
	
	// 한 번 이상 DI가 일어날 수 있도록 두 개의 DI용 프로퍼티를 선언해줬다.
	static class SingletonClientBean {
		@Autowired
		SingletonBean bean1;
		@Autowired
		SingletonBean bean2;
	}
	
	// 애노테이션을 이용해 프로토타입 빈으로 만들려면 @Scope의 기본 값을 prototype으로 지정한다.
	@Component("prototypeBean")
	@Scope("prototype")
	static class PrototypeBean {}
	
	static class PrototypeClientBean {
		@Autowired
		PrototypeBean bean1;
		@Autowired
		PrototypeBean bean2;
	}
	
	@Test
	public void objectFactory() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ObjectFactoryConfig.class);
		ObjectFactory<PrototypeBean> factoryBeanFactory = ac.getBean("prototypeBeanFactory", ObjectFactory.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			beans.add(factoryBeanFactory.getObject());
			assertThat(beans.size(), is(i));
		}
	}
	
	@Configuration
	static class ObjectFactoryConfig {
		@Bean
		public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
			ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
			factoryBean.setTargetBeanName("prototypeBean");
			return factoryBean;
		}
	}
	
	@Test
	public void serviceLocatorFactoryBean() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ServiceLocatorConfig.class);
		PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			beans.add(factory.getPrototypeBean());
			assertThat(beans.size(), is(i));
		}
	}
	
	interface PrototypeBeanFactory {
		PrototypeBean getPrototypeBean();
	}
	
	@Configuration
	static class ServiceLocatorConfig {
		@Bean
		public ServiceLocatorFactoryBean prototypeBeanFactory() {
			ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
			factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
			return factoryBean;
		}
	}
	
	@Test
	public void provideryTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ProviderClient.class);
		ProviderClient client = ac.getBean(ProviderClient.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			beans.add(client.prototypeBeanProvider.get());
			assertThat(beans.size(), is(i));
		}
	}
	
	static class ProviderClient {
		@Inject
		Provider<PrototypeBean> prototypeBeanProvider;
	}
	
	static class AnnotationConfigDispatcherServlet extends DispatcherServlet {
		private Class<?>[] classes;
		
		public AnnotationConfigDispatcherServlet(Class<?> ...classes) {
			super();
			this.classes = classes;
		}
		
		protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
			AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
				protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
					AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
					reader.register(classes);
				}
			};
			
			wac.setServletContext(getServletContext());
			wac.setServletConfig(getServletConfig());
			wac.refresh();
			return wac;
		}
	}
	
	MockHttpServletResponse response = new MockHttpServletResponse();
	@Test
	public void requestScope() throws ServletException, IOException {
		MockServletConfig ctx = new MockServletConfig(new MockServletContext(), "spring");
		DispatcherServlet ds = new AnnotationConfigDispatcherServlet(HelloController.class, HelloService.class, RequestBean.class, BeanCounter.class);
		ds.init(new MockServletConfig());
		
		BeanCounter counter = ds.getWebApplicationContext().getBean(BeanCounter.class);
		
		ds.service(new MockHttpServletRequest("GET", "/hello"), this.response);
		assertThat(counter.addCounter, is(2));
		assertThat(counter.size(), is(1));
		
		ds.service(new MockHttpServletRequest("GET", "/hello"), this.response);
		assertThat(counter.addCounter, is(4));
		assertThat(counter.size(), is(2));
		
		for(String name : ((AbstractRefreshableWebApplicationContext)ds.getWebApplicationContext()).getBeanFactory().getRegisteredScopeNames()) {
			System.out.println(name);
		}
	}
	
	@RequestMapping("/")
	static class HelloController {
		@Autowired
		HelloService helloService;
		@Autowired
		Provider<RequestBean> requestBeanProvider;
		@Autowired
		BeanCounter beanCounter;
		
		@RequestMapping("hello")
		public String hello() {
			beanCounter.addCounter++;
			beanCounter.add(requestBeanProvider.get());
			helloService.hello();
			
			return "";
		}
	}
	
	static class HelloService {
		@Autowired
		Provider<RequestBean> requestBeanProvider;
		@Autowired
		BeanCounter beanCounter;
		
		public void hello() {
			beanCounter.addCounter++;
			beanCounter.add(requestBeanProvider.get());
		}
	}
	
	@Scope(value = "request")
	static class RequestBean {}
	
	static class BeanCounter extends HashSet {
		int addCounter = 0;
	}
	
}
