// src/main/java/com/enigma/live_code_springboot/service/impl/AuthServiceImpl.java
package com.belajar.springboot.service.impl;

import com.belajar.springboot.constant.RoleType;
import com.belajar.springboot.dto.request.LoginRequest;
import com.belajar.springboot.dto.request.UserRegisterRequest;
import com.belajar.springboot.dto.response.JwtResponse;
import com.belajar.springboot.dto.response.UserResponse;
import com.belajar.springboot.entity.Role;
import com.belajar.springboot.entity.User;
import com.belajar.springboot.exception.BadRequestException;
import com.belajar.springboot.exception.UnauthorizedException;
import com.belajar.springboot.mapper.UserMapper;
import com.belajar.springboot.repository.UserRepository;
import com.belajar.springboot.security.JwtUtils;
import com.belajar.springboot.security.UserDetailsImpl;
import com.belajar.springboot.service.AuthService;
import com.belajar.springboot.service.RoleService;
import com.belajar.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse registerCustomer(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered.");
        }

        Role customerRole = roleService.getOrCreate(RoleType.CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullname())
                .roles(roles)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(newUser);
        return UserMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse registerStaff(UserRegisterRequest request) {
        User currentUser = userService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.ADMIN);
        if (!isAdmin) {
            throw new UnauthorizedException("Only ADMIN users can register new STAFF users.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered.");
        }

        Role staffRole = roleService.getOrCreate(RoleType.STAFF);
        Set<Role> roles = new HashSet<>();
        roles.add(staffRole);

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullname())
                .roles(roles)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(newUser);
        return UserMapper.toUserResponse(savedUser);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), // Ditangani oleh loadUserByUsername
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateTokenFromUserDetails(userDetails);

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return JwtResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .roles(roles)
                    .build();
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Login failed: Invalid username/email or password.");
        }
    }
}