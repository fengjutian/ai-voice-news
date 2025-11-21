package com.voice.news.app.controller;

import com.voice.news.app.security.AuthTokens;
import com.voice.news.app.security.TokenService;
import com.voice.news.app.model.User;
import com.voice.news.app.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        // 认证成功，生成 token
        AuthTokens tokens = tokenService.createTokens(req.getUsername());
        return ResponseEntity.ok(Map.of(
            "accessToken", tokens.getAccessToken(),
            "refreshToken", tokens.getRefreshToken()
        ));
      } catch (BadCredentialsException ex) {
        return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
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
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
        }
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "邮箱已存在"));
        }
        if (userRepository.findByPhone(req.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "手机号已存在"));
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("status", "created"));
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
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @Email
        @NotBlank
        private String email;
        @NotBlank
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
