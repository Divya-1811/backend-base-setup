package com.practice.base_setup.serviceimpl;

import com.practice.base_setup.model.User;
import com.practice.base_setup.response.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${spring.jwt.password}")
    private String secret;

    public AuthResponse generateToken(User user) {
        AuthResponse authResponse=new AuthResponse();
        String accessToken=generateAuthToken(user,"AccessToken");
        String refreshToken=generateAuthToken(user,"RefreshToken");
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());
        return authResponse;
    }

    private String generateAuthToken(User user, String tokenType) {
        Map<String,Object> claims;
        Date expiry = new Date();
        if (tokenType.equals("AccessToken")){
            expiry=new Date(System.currentTimeMillis()+1800000);
        } else if (tokenType.equals("RefreshToken")) {
            expiry=new Date(System.currentTimeMillis()+46800000);
        }
        claims=getClaims(user);
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

    }

    private Map<String, Object> getClaims(User user) {
        Map<String, Object> claims=new HashMap<>();
        claims.put("id",user.getId());
        claims.put("name",user.getName());
        claims.put("email",user.getEmail());
        claims.put("role",user.getRole());
        return claims;
    }

    public Claims parseToken(String jwtToken) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
