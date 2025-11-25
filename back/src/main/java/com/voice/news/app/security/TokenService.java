package com.voice.news.app.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_KEY_PREFIX = "auth:refresh:"; // auth:refresh:{refreshId} -> username
    private static final String USER_REFRESH_LIST_PREFIX = "auth:user:refresh:"; // auth:user:refresh:{username} -> set of refreshIds

    public TokenService(StringRedisTemplate redisTemplate, JwtUtil jwtUtil, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
    }

    public AuthTokens createTokens(String username) {
        logger.info("Creating tokens for user: {}", username);
        try {
            // access token claims 可放 roles 等（此示例简单）
            String accessToken = jwtUtil.generateAccessToken(username, null);
            logger.debug("Access token generated successfully for user: {}", username);

            // refresh token 使用 id 管理
            String refreshId = UUID.randomUUID().toString();
            String refreshToken = jwtUtil.generateRefreshToken(username, refreshId);
            logger.debug("Refresh token generated successfully for user: {}", username);

            try {
                // 将 refreshId 存 redis，过期时间与 refresh token 一致
                String key = REFRESH_KEY_PREFIX + refreshId;
                redisTemplate.opsForValue().set(key, username, jwtProperties.getRefreshTokenExpireSeconds(), TimeUnit.SECONDS);
                logger.debug("Refresh token stored in Redis with key: {}", key);

                // 记录用户的 refreshId 列表（便于登出全部设备）
                String userListKey = USER_REFRESH_LIST_PREFIX + username;
                redisTemplate.opsForSet().add(userListKey, refreshId);
                redisTemplate.expire(userListKey, Duration.ofSeconds(jwtProperties.getRefreshTokenExpireSeconds()));
                logger.debug("User refresh token list updated in Redis with key: {}", userListKey);
            } catch (Exception e) {
                logger.error("Failed to store tokens in Redis for user: {}", username, e);
                throw new RuntimeException("Failed to store authentication tokens", e);
            }

            logger.info("Tokens created successfully for user: {}", username);
            return new AuthTokens(accessToken, refreshToken);
        } catch (Exception e) {
            logger.error("Error creating tokens for user: {}", username, e);
            throw new RuntimeException("Failed to create authentication tokens", e);
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            var jws = jwtUtil.parseToken(refreshToken);
            String refreshId = jws.getBody().getId();
            String key = REFRESH_KEY_PREFIX + refreshId;
            String username = redisTemplate.opsForValue().get(key);
            return username != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        var jws = jwtUtil.parseToken(refreshToken);
        return jws.getBody().getSubject();
    }

    public void revokeRefreshToken(String refreshToken) {
        try {
            var jws = jwtUtil.parseToken(refreshToken);
            String refreshId = jws.getBody().getId();
            String username = jws.getBody().getSubject();

            String key = REFRESH_KEY_PREFIX + refreshId;
            redisTemplate.delete(key);

            String userListKey = USER_REFRESH_LIST_PREFIX + username;
            redisTemplate.opsForSet().remove(userListKey, refreshId);
        } catch (Exception ignored) {}
    }

    public void revokeAllForUser(String username) {
        String userListKey = USER_REFRESH_LIST_PREFIX + username;
        var refreshIds = redisTemplate.opsForSet().members(userListKey);
        if (refreshIds != null) {
            for (String id : refreshIds) {
                redisTemplate.delete(REFRESH_KEY_PREFIX + id);
            }
        }
        redisTemplate.delete(userListKey);
    }
}

