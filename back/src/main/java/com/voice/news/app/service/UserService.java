package com.voice.news.app.service;

import com.voice.news.app.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
