package com.voice.news.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.news.app.common.R;
import com.voice.news.app.exception.ErrorCode;
import com.voice.news.app.model.User;
import com.voice.news.app.security.JwtUtil;
import com.voice.news.app.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "User Controller", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/users")
    @Operation(summary = "获取用户列表")
    public R<List<User>> getAllUsers() {
        return R.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "根据ID获取用户")
    public R<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(R::ok)
                .orElse(R.build(ErrorCode.NOT_FOUND));
    }

    @PostMapping("/users")
    @Operation(summary = "创建新用户")
    public R<User> createUser(@RequestBody User user) {
        return R.ok(userService.createUser(user));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "更新用户信息")
    public R<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        if (updated == null) {
            return R.build(ErrorCode.NOT_FOUND);
        }
        return R.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public R<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @GetMapping("/currentUser")
    @Operation(summary = "根据token获取当前用户信息")
    public R<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // 从Authorization header中获取token
            // 通常Authorization header格式为 "Bearer {token}"
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // 移除"Bearer "前缀
            }
            
            if (token == null || token.isEmpty()) {
                return R.error(ErrorCode.UNAUTHORIZED.code, "缺少token参数");
            }
            
            // 解析token获取用户名
            String username = jwtUtil.parseToken(token).getBody().getSubject();
            
            // 使用新添加的getUserByUsername方法获取用户信息
            return userService.getUserByUsername(username)
                    .map(R::ok)
                    .orElse(R.build(ErrorCode.NOT_FOUND));
        } catch (Exception e) {
            return R.error(ErrorCode.UNAUTHORIZED.code, "无效的token");
        }
    }
}

