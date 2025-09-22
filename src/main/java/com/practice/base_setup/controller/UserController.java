package com.practice.base_setup.controller;

import com.practice.base_setup.dto.LoginDto;
import com.practice.base_setup.dto.RefreshTokenDto;
import com.practice.base_setup.dto.UserDto;
import com.practice.base_setup.model.User;
import com.practice.base_setup.response.ApiResponse;
import com.practice.base_setup.response.AuthResponse;
import com.practice.base_setup.service.UserService;
import com.practice.base_setup.serviceimpl.AuthService;
import com.practice.base_setup.util.CommonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> saveUser(@RequestBody UserDto userDto){
        userService.saveUser(userDto);
        return CommonUtil.getOkResponse("User save successfully","");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> userLogin(@RequestBody LoginDto loginDto){
        User user=userService.userLogin(loginDto);
        AuthResponse authResponse=authService.generateToken(user);
        return CommonUtil.getOkResponse("User login successfully",authResponse);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto)  {
        Map<String, Object> claims = this.authService.parseToken(refreshTokenDto.getRefreshToken());
        User user = this.authService.getByUserId((long) ((Integer) claims.get("id")));
        AuthResponse authResponse = this.authService.generateToken(user);
        return CommonUtil.getOkResponse("Refresh successful", authResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getByUserList(){
        List<User> user=userService.getByUserList();
        return CommonUtil.getOkResponse("Success",user);
    }


}
