package com.practice.base_setup.config;

import com.practice.base_setup.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(https-> https
                        .requestMatchers(
                                Constants.SWAGGER_REQUEST_URL_1,
                                Constants.SWAGGER_REQUEST_URL_2,
                                Constants.SWAGGER_REQUEST_URL_3,
                                Constants.SWAGGER_REQUEST_URL_4,
                                Constants.SWAGGER_REQUEST_URL_5,
                                Constants.SWAGGER_REQUEST_URL_6,
                                Constants.SWAGGER_REQUEST_URL_7,
                                Constants.SWAGGER_REQUEST_URL_8,
                                Constants.SWAGGER_REQUEST_URL_9,
                                Constants.SWAGGER_REQUEST_URL_10,
                                Constants.SWAGGER_REQUEST_URL_11,
                                Constants.SWAGGER_REQUEST_URL_12,
                                Constants.SWAGGER_REQUEST_URL_13,
                                Constants.ACTUATOR_REQUEST_URL
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,"/user").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
