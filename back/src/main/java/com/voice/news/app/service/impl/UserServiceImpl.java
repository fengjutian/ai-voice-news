package com.voice.news.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.voice.news.app.model.User;
import com.voice.news.app.repository.UserRepository;
import com.voice.news.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setUsername(user.getUsername());
                    u.setEmail(user.getEmail());
                    u.setPhone(user.getPhone());
                    if (StringUtils.hasText(user.getPassword())) {
                        u.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    u.setAge(user.getAge());
                    u.setHeight(user.getHeight());
                    u.setGender(user.getGender());
                    return userRepository.save(u);
                })
                .orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
