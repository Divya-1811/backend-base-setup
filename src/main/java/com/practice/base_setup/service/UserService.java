package com.practice.base_setup.service;

import com.practice.base_setup.dto.LoginDto;
import com.practice.base_setup.dto.UserDto;
import com.practice.base_setup.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void saveUser(UserDto userDto);

    User userLogin(LoginDto loginDto);

    User getByUserId(Long id);

    User getByRole();
}
