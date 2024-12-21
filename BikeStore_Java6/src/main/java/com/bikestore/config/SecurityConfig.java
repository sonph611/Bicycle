package com.bikestore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bikestore.model.Account;
import com.bikestore.repository.AccountRepository;
import com.bikestore.service.CustomOAuth2UserService;
import com.bikestore.service.UserService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	AccountRepository accountRepository;
	
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    	 http
//         .authorizeHttpRequests(authorize -> authorize
//             .requestMatchers("/login/**","/admin/**", "/oauth2/**"
//            		 , "/user/**"
//            		 //, "/views/user/js/main.js"
//            		 ).permitAll() // các tài nguyên công khai
//             .requestMatchers("/home", "/account/login/**").permitAll() 
//             //.requestMatchers("/admin/**").hasRole("admin")
//             .anyRequest().authenticated()
//         )
//         .logout(logout -> logout
//             .logoutSuccessUrl("/login")
//             .permitAll()
//         )
//         .oauth2Login()
//         .loginPage("/login")
//         .defaultSuccessUrl("/home")
//         .failureUrl("/login?error")
//         .userInfoEndpoint()
//         .userService(oAuth2UserService()); 
//     return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//    	UserService userService = new UserService();
//    	Account account = accountRepository.findAccountById(11);
////    	System.out.println("Tao nè "+account.getRole());
////    	System.out.println("Tao nè "+account.getUsername());
////    	System.out.println("Tao nè "+account.getPassword());
//        return new UserService(); 
//    }
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
//    	
//        return new CustomOAuth2UserService();
//    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
