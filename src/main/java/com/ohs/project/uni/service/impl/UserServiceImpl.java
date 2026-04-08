package com.ohs.project.uni.service.impl;

import com.ohs.project.uni.dto.*;
import com.ohs.project.uni.entity.User;
import com.ohs.project.uni.repository.UserRepository;
import com.ohs.project.uni.security.JWTGenerator;
import com.ohs.project.uni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;


    @Override
    public void save(CreateDTO createDTO) {
        boolean exists = userRepository.existsByEmail(createDTO.getEmail());
        if(exists) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                createDTO.getFullName(),
                createDTO.getEmail(),
                passwordEncoder.encode(createDTO.getPassword())
        );

        userRepository.save(user);
    }

    @Override
    public User edit(String userId, EditDTO editDTO) {
        User user = findUserById(userId);

        if(StringUtils.hasText(editDTO.getFullName())) user.setFullName(editDTO.getFullName());
        if(StringUtils.hasText(editDTO.getEmail())) user.setEmail(editDTO.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void delete(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserAndTokenDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(()-> new RuntimeException("Email not found"));

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getId(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return UserAndTokenDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .token(jwtGenerator.generateToken(authentication))
                .build();
    }

    @Override
    public void editPassword(String userId, PasswordDTO passwordDTO) {
        var savedUser = findUserById(userId);

        if(passwordEncoder.matches(passwordDTO.getCurrentPassword(), savedUser.getPassword())){
            throw new RuntimeException("the current password does not match!");
        }

        savedUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));

        userRepository.save(savedUser);
    }

    @Override
    public User getDetails(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private User findUserById(String userId){
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user is not found"));
    }
}
