package com.bikestore.service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bikestore.model.Account;
import com.bikestore.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private HttpSession session;
    
    @Autowired
    private AccountRepository accountRepository;
    
    private com.bikestore.service.PasswordEncoder passwordEncoder=new com.bikestore.service.PasswordEncoder();

    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);

        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        
        Account account = optionalAccount.get();
        String accountUsername = account.getUsername(); 
        String password = account.getPassword();
        String role = account.getRole(); 

        
        System.out.println("Heheeeeeeee Username: " + accountUsername);
        System.out.println("Heheeeeeeee Role: " + role);

        
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));

        
        return new User(accountUsername, password, authorities);
    }




    public void saveUser(Account account) {
        if (account.getPassword() == null || account.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String encodedPassword = passwordEncoder.encodePassword(account.getPassword());
        account.setPassword(encodedPassword);
        session.setAttribute("username", account.getUsername());
        session.setAttribute("isLoggedIn", true);
        session.setAttribute("user", account.getId());
        accountRepository.saveAndFlush(account);
    }

    public Account findOrCreateUser(String identifier, String loginType) {
        String username;
        if (loginType.equalsIgnoreCase("google")) {
            username = identifier.split("\\.")[0]; // Lấy phần trước dấu chấm của email
        } else {
            // Đối với Facebook, nếu có '@', xử lý như email, không thì giữ nguyên
            username = identifier.contains("@") ? identifier.split("@")[0] : identifier;
        }

        return accountRepository.findByUsername(username).orElseGet(() -> {
            Account newAccount = new Account();
            newAccount.setUsername(username);
            newAccount.setPassword(" "); // Chắc chắn bạn đã kiểm tra tình huống này
            newAccount.setEmail(identifier);
            newAccount.setRole("User");

            // Lưu thông tin người dùng vào session
            session.setAttribute("username", newAccount.getUsername());
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("user", newAccount.getId());

            return accountRepository.saveAndFlush(newAccount);
        });
    }
}
