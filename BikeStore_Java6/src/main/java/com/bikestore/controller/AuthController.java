package com.bikestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
	@RequestMapping("/login")
	public String login(Model model) {
		//model.addAttribute("security", "Bạn chưa có quyền truy cập!");
		return "account/login";
	}
	
	
}
