package com.belajar.springboot.mapper;

import com.belajar.springboot.dto.response.UserResponse;
import com.belajar.springboot.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
