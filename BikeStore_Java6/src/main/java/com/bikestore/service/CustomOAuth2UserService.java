package com.bikestore.service;

import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.bikestore.model.Account;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
	private UserService userService;

	@Autowired
	HttpServletRequest request;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	    OAuth2User oAuth2User = delegate.loadUser(userRequest);

	    String provider = userRequest.getClientRegistration().getRegistrationId(); // Lấy tên của provider (google, facebook)
	    String identifier = provider.equals("google") ? oAuth2User.getAttribute("email") : oAuth2User.getAttribute("name");

	    if (identifier == null || identifier.isEmpty()) {
	        throw new OAuth2AuthenticationException("Identifier is required for " + provider + " login");
	    }

	    Account account = userService.findOrCreateUser(identifier, provider);
	    userService.saveUser(account);

	    return oAuth2User;
	}


}
