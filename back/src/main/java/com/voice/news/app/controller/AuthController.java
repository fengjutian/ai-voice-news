package com.voice.news.app.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.news.app.model.User;
import com.voice.news.app.repository.UserRepository;
import com.voice.news.app.security.AuthTokens;
import com.voice.news.app.security.TokenService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
      this.authenticationManager = authenticationManager;
      this.tokenService = tokenService;
      this.passwordEncoder = passwordEncoder;
      this.userRepository = userRepository;
    }

    // 登录：username + password -> 返回 access + refresh
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
      try {
        // 1. 进行身份验证
        Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 2. 生成令牌
        AuthTokens tokens = tokenService.createTokens(req.getUsername());
        
        // 3. 返回成功响应
        return ResponseEntity.ok(Map.of(
            "accessToken", tokens.getAccessToken(),
            "refreshToken", tokens.getRefreshToken()
        ));
      } catch (BadCredentialsException ex) {
        // 处理认证失败的情况
        return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
      } catch (Exception ex) {
        // 处理其他所有异常，包括Redis连接问题、令牌生成问题等
        // 记录异常日志
        ex.printStackTrace();
        // 返回友好的错误信息
        return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误，请稍后重试"));
      }
    }

    // 刷新 access token：传 refreshToken -> 返回新 accessToken (并可返回新 refreshToken，下面做同样逻辑)
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !tokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token 无效或已过期"));
        }
        String username = tokenService.getUsernameFromRefreshToken(refreshToken);

        // 先撤销旧 refresh
        tokenService.revokeRefreshToken(refreshToken);
        // 生成新的一组 tokens
        AuthTokens tokens = tokenService.createTokens(username);
        return ResponseEntity.ok(Map.of(
                "accessToken", tokens.getAccessToken(),
                "refreshToken", tokens.getRefreshToken()
        ));
    }

    // 登出：撤销传入的 refresh token（前端应把 access token 删掉）
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null) {
            tokenService.revokeRefreshToken(refreshToken);
        }
        // 如果想登出当前用户全部设备，可调用 tokenService.revokeAllForUser(username)
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // 注册示例（简化）
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        // 检查用户名唯一性
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
          return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
        }
        // 检查邮箱唯一性
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
          return ResponseEntity.badRequest().body(Map.of("error", "邮箱已存在"));
        }
        // 检查手机号唯一性
        if (userRepository.findByPhone(req.getPhone()).isPresent()) {
          return ResponseEntity.badRequest().body(Map.of("error", "手机号已存在"));
        }
        
        // 密码复杂度验证
        if (req.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码长度不能少于6位"));
        }
        
        // 创建用户对象并设置属性
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        
        // 保存用户到数据库
        User savedUser = userRepository.save(u);
        
        // 返回成功响应，不包含敏感信息
        return ResponseEntity.ok(Map.of(
            "status", "created",
            "username", savedUser.getUsername(),
            "message", "用户注册成功"
        ));
    }

    // DTOs
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        // getters/setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        @NotBlank(message = "手机号不能为空")
        private String phone;
        // getters/setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
