package com.practice.base_setup.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.base_setup.exception.CustomException;
import com.practice.base_setup.model.User;
import com.practice.base_setup.repository.UserRepository;
import com.practice.base_setup.response.UserDto;
import com.practice.base_setup.serviceimpl.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if (skipFilter(request)){
                filterChain.doFilter(request,response);
                return;
            }
            String jwtToken=extractJwtToken(request.getHeader("Authorization"));
            processJwtToken(jwtToken,request);
            filterChain.doFilter(request,response);
            if (jwtToken==null){
                throw new ServletException("JWT token does not with begin Bearer");
            }
        } catch (ExpiredJwtException e) {
            handleException(response,HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
        }catch (ServletException e){
            handleException(response,HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        }catch (Exception e){
            handleException(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    private void handleException(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin","*");
        Map<String,Object> errorResponse=new HashMap<>();
        errorResponse.put("statusCode", status);
        errorResponse.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void processJwtToken(String jwtToken, HttpServletRequest request) throws ServletException {
        Claims claims=authService.parseToken(jwtToken);
        Date expiry=claims.getExpiration();
        if (expiry.before(new Date())){
            throw new ServletException("Access token is expired");
        }
        if (SecurityContextHolder.getContext().getAuthentication()==null){
            Long userId=claims.get("id",Long.class);
            User user=userRepository.findById(userId)
                    .orElseThrow(()->new CustomException("User not found"));
            UserDetails userDetails=createUserDetails(user);
            authenticationWithJwtToken(userDetails,request);
            UserDto userDto=setUserDtoDetails(claims);
            UserContextHolder.setUserDto(userDto);
        }

    }

    private UserDto setUserDtoDetails(Claims claims) {
        UserDto userDto=new UserDto();
        userDto.setId(claims.get("id",Long.class));
        userDto.setName(claims.get("name",String.class));
        userDto.setEmail(claims.get("email",String.class));
        userDto.setRole(claims.get("role",String.class));
        return userDto;
    }

    private void authenticationWithJwtToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                userDetails,null,userDetails.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UserDetails createUserDetails(User user) {
        String role="ROLE_"+user.getRole();
        List<GrantedAuthority> authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),"",authorities);
    }

    private String extractJwtToken(String authorization) {
        return(authorization!=null && authorization.startsWith("Bearer ")) ?
                authorization.substring(7) : null;
    }

    private boolean skipFilter(HttpServletRequest request) {
        String uri=request.getRequestURI();
        List<String> skipList=List.of(
                "/v1/auth",
                "/actuator/health",
                "/v3/api-docs",
                "/swagger-ui/",
                "/favicon.ico"
        );
        return skipList.stream().anyMatch(uri::startsWith) ||
                StringUtils.isBlank(request.getHeader("Authorization"));
    }
}
