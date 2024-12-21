package com.bikestore.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@SpringBootApplication
public class SpringBootImageApplication {
	@Bean
	public Cloudinary cloudinary() {
		Cloudinary c = new Cloudinary(ObjectUtils.asMap(
				 "cloud_name", "dtf0imsqt",
	              "api_key", "611451577453323",
	              "api_secret", "GAGFWRLg4kZ41x1TC4TBtHpExoU",
	              "secure",true
				));	
		return c;
	}
}
