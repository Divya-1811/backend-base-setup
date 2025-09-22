package com.practice.base_setup.serviceimpl;

import com.practice.base_setup.config.UserContextHolder;
import com.practice.base_setup.dto.LoginDto;
import com.practice.base_setup.dto.UserDto;
import com.practice.base_setup.exception.CustomException;
import com.practice.base_setup.model.User;
import com.practice.base_setup.repository.UserRepository;
import com.practice.base_setup.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        Boolean existEmail=userRepository.existsByEmailIgnoreCase(userDto.getEmail());
        if (Boolean.TRUE.equals(existEmail)){
            throw new CustomException("Email already exists");
        }
        User user=new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        userRepository.save(user);
    }

    @Override
    public User userLogin(LoginDto loginDto) {
        User user=userRepository.findByEmail(loginDto.getEmail()).
                orElseThrow(()->new CustomException("Invalid email"));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new CustomException("Invalid password");
        }
        return user;
    }

    @Override
    public List<User> getByUserList() {
        return userRepository.findAll();
    }

}
