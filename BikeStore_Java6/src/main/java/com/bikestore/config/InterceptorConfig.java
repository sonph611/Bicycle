package com.bikestore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bikestore.service.AuthUser;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer{
//	@Autowired
//	AuthInterceptor interceptor;
	@Autowired
	AuthUser authUser;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
//		registry.addInterceptor(interceptor).addPathPatterns("/admin/**");
		registry.addInterceptor(authUser).addPathPatterns("/user/auth/**");
	}
	
}
