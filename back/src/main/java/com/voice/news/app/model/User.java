package com.voice.news.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // 数据库表名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 主键

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 用户名

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;   // 性别，使用枚举

    @Column
    private Integer age;     // 年龄

    @Column
    private Double height;   // 身高（米）

    @Column(nullable = false, unique = true, length = 100)
    private String email;    // 邮箱

    @Column(nullable = false, unique = true, length = 20)
    private String phone;    // 手机号

    @Column(nullable = false, length = 100)
    private String password; // 密码

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    // 自动在插入时设置创建时间
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 自动在更新时设置更新时间
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

