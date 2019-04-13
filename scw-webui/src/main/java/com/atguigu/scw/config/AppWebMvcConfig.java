package com.atguigu.scw.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class AppWebMvcConfig implements WebMvcConfigurer {

	
	//<mvc:view-controller path="/login" view-name="login"/>
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// 1、当我们开启页面映射了，所有都得映射
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/toRegist").setViewName("regist");
		
	}
	
	//拦截器配置
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		//WebMvcConfigurer.super.addInterceptors(registry);
		//registry.addInterceptor(interceptor);
	}

}
