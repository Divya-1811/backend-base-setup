package com.practice.base_setup.controller;

import com.practice.base_setup.dto.LoginDto;
import com.practice.base_setup.dto.UserDto;
import com.practice.base_setup.model.User;
import com.practice.base_setup.response.ApiResponse;
import com.practice.base_setup.response.AuthResponse;
import com.practice.base_setup.service.UserService;
import com.practice.base_setup.serviceimpl.AuthService;
import com.practice.base_setup.util.CommonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/login/get/{userId}")
    public ResponseEntity<ApiResponse> getByUserId(@PathVariable("userId") Long userId){
        User user=userService.getByUserId(userId);
        return CommonUtil.getOkResponse("Success",user);
    }

    @GetMapping("/login/get")
    public ResponseEntity<ApiResponse> getByRole(){
        User user=userService.getByRole();
        return CommonUtil.getOkResponse("Success",user);
    }

}
