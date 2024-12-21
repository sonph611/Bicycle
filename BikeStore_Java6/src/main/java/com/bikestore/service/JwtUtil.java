package com.bikestore.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bikestore.model.Account;
@Component
public class JwtUtil {

    private String secretKey = "tanbvpc05190dgwcbxmcbxmbcjgcdjdjdhdu2382jscsjcvsvcv"; 

    public String generateToken(Account user) {
        Map<String, Object> claims = new HashMap();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
    
    public  String createTokendecodeTokenJSON(String json) {
    	Map<String, Object> claims = new HashMap();
        claims.put("data",json);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    public  String decodeTokenJSON(String token) {
    	return (String) getClaims(token).get("data");
    }

    
    

    public String getUsername(String token) {
        return (String) getClaims(token).get("username");
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

            return !isTokenExpired(claims);
        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
