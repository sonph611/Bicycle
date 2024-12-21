package com.bikestore.service;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoder {

	public static String encodePassword(String password) {
	    String salt = BCrypt.gensalt(6);
	    String hashedPassword = BCrypt.hashpw(password, salt);
	    return hashedPassword;
	}

	public boolean checkPassword(String password, String hashedPassword) {
	    return BCrypt.checkpw(password, hashedPassword);
	}

}

