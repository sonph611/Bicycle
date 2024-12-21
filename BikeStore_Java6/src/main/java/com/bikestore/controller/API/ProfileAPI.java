package com.bikestore.controller.API;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bikestore.model.Account;
import com.bikestore.repository.AddressRepository;
import com.bikestore.service.DataRespone;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ProfileAPI {
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	HttpServletRequest request;
	@RequestMapping("/user/auth/profile")
	public DataRespone<Object> getProfile(){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		Account a=(Account) request.getAttribute("user");
		a.setAddresses(null);
		return dataMap.addData("adress", addressRepository.findBListyAccount(a.getId())).addData("account", a);
	}
}
