package com.bikestore.controller.API;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bikestore.model.Account;
import com.bikestore.service.AuthService;
import com.bikestore.service.DataRespone;
import com.bikestore.service.JwtUtil;
import com.bikestore.service.PasswordEncoder;

//import com.bikestore.model.Account;
//import com.bikestore.service.AuthService;
//import com.asm.service.DataRespone;
//import com.asm.service.JwtUtil;
//import com.asm.utils.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ApiAuth {
    @Autowired
    private AuthService authService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse respone;
    
    @Autowired
    JwtUtil jwt;
 
    private com.bikestore.service.PasswordEncoder passwordEncoder=new PasswordEncoder();
    
    private Map<String,String>map=new HashMap<String, String>();

    @PostMapping("/user/login")
    public DataRespone<Object> login(@RequestParam(name="userName",defaultValue ="") String emailOrUsername,
    		@RequestParam(name="password",defaultValue ="") String password) {
        DataRespone<Object> dataMap = new DataRespone<Object>();
        Account account =  authService.findByEmailOrUserName(emailOrUsername);
        if (account != null) {
        	String a = map.get(account.getPassword()) != null ? map.get(account.getPassword()) : "";
        	if (a.equals(password)||passwordEncoder.checkPassword(password, account.getPassword())
        			) {
        		if((a.equals(""))) {
        			map.put(account.getPassword(),password);
        		}
                if (!account.getStatus().equals("Activing")) {
                    return dataMap.addData("code", 423).addData("message", "Tài khoản bạn đã bị khóa !!!");
                } if(account.getRole().equals("User")) {
                	account.setPassword(null);
                	account.setConfirmPassword(null);
                	account.setEmail(null);
                	String token=jwt.generateToken(account);
                	account.setRole(null);
                	return dataMap.addData("code", 200).addData("message", "Đăng nhập thành công!!!").addData("accessToken",token).addData("user",account);
                }else {
                	return dataMap.addData("code", 403).addData("message", "Vui lòng đăng nhập lại!!!");
                }
            }
        }
        return dataMap.addData("code", 400).addData("message", "Thông tin tài khoản không chính xác !!!");
    }
    
    @GetMapping("/user/auth")
    public boolean jjj() {
    	return true;
    }
    
}
