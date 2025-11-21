-- ① 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ai_voice_news`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

-- ② 切换到数据库
USE `ai_voice_news`;

-- ③ 创建 users 表
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',

  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别（枚举：MALE/FEMALE/OTHER）',

  `age` INT DEFAULT NULL COMMENT '年龄',
  `height` DOUBLE DEFAULT NULL COMMENT '身高（米）',

  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',

  `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',

  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',

  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO users (username, gender, age, height, email, phone, password, created_at, updated_at)
VALUES
('alice', 'FEMALE', 23, 1.62, 'alice@example.com', '13800000001', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('bob', 'MALE', 28, 1.75, 'bob@example.com', '13800000002', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('charlie', 'MALE', 31, 1.80, 'charlie@example.com', '13800000003', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('diana', 'FEMALE', 26, 1.55, 'diana@example.com', '13800000004', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('eric', 'MALE', 35, 1.70, 'eric@example.com', '13800000005', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('fiona', 'FEMALE', 29, 1.60, 'fiona@example.com', '13800000006', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('george', 'MALE', 40, 1.78, 'george@example.com', '13800000007', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('helen', 'FEMALE', 33, 1.65, 'helen@example.com', '13800000008', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('ivan', 'MALE', 22, 1.72, 'ivan@example.com', '13800000009', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW()),
('julia', 'FEMALE', 27, 1.68, 'julia@example.com', '13800000010', '$2a$10$GmNqB4X9g5R62lSlZZfEgeAaDnz1xFZXmp5GMzsaOEQhnIvOqnHkq', NOW(), NOW());

