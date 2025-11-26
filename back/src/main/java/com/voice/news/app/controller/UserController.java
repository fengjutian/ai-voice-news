package com.voice.news.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.news.app.common.R;
import com.voice.news.app.exception.ErrorCode;
import com.voice.news.app.model.User;
import com.voice.news.app.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    public R<List<User>> getAllUsers() {
        return R.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户")
    public R<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(R::ok)
                .orElse(R.build(ErrorCode.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "创建新用户")
    public R<User> createUser(@RequestBody User user) {
        return R.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public R<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        if (updated == null) {
            return R.build(ErrorCode.NOT_FOUND);
        }
        return R.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public R<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }
}

