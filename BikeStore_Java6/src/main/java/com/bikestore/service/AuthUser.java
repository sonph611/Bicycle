package com.bikestore.service;

import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bikestore.model.Account;
import com.bikestore.service.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class AuthUser implements HandlerInterceptor{
	@Autowired
	JwtUtil jwt;
	@Autowired
	AuthService auth;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("Testdhhshshshsh");
		String token=request.getHeader("token");
		try { 
			if(jwt.isTokenValid(token)) {
				Account account=auth.findByUsername(jwt.getUsername(token));
				if(account!=null) {
					request.setAttribute("user", account);
					return true;
				}
			}
		} catch (IllegalArgumentException e) {
		}
		response.setStatus(403);
//		respone
		return false;
	}
}
