-- ① 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ai_voice_news`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

-- ② 切换到数据库
USE `ai_voice_news`;

DROP TABLE IF EXISTS `users`;

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

DROP TABLE IF EXISTS `news`;

CREATE TABLE `news` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(500) NOT NULL COMMENT '新闻标题',
  `summary` TEXT COMMENT '新闻摘要',
  `content` LONGTEXT COMMENT '新闻正文',
  `tags` VARCHAR(200) COMMENT '新闻标签，逗号分隔，例如 "科技,AI"',
  `source` VARCHAR(200) COMMENT '新闻来源',
  `url` VARCHAR(500) COMMENT '新闻原文链接',
  `published_at` DATETIME COMMENT '新闻实际发布时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '抓取时间',
  PRIMARY KEY (`id`),
  INDEX `idx_published_at` (`published_at`),
  INDEX `idx_tags` (`tags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `news` (title, summary, content, tags, source, url, published_at)
VALUES
('OpenAI 发布 GPT-5，性能大幅提升', 'OpenAI 正式发布 GPT-5，模型能力与效率进一步增强。', 'OpenAI 今日正式发布 GPT-5，相比上一代在推理速度、上下文长度以及多模态能力方面都有显著提升……', '科技,AI', 'OpenAI News', 'https://news.example.com/openai-gpt5', NOW() - INTERVAL 2 HOUR),
('华为推出全新鸿蒙 NEXT 版本', '华为宣布推出鸿蒙 NEXT，完全去 AOSP。', '在今日的发布会上，华为正式公布鸿蒙 NEXT 版本，该系统彻底舍弃 AOSP……', '科技,手机', '华为新闻中心', 'https://news.example.com/huawei-next', NOW() - INTERVAL 5 HOUR),
('比特币突破 10 万美元大关', '比特币今日突破心理关口 100,000 美元。', '全球加密货币市场再度迎来上涨，比特币价格在今日一度突破 10 万美元……', '财经,加密货币', 'CoinDesk', 'https://news.example.com/bitcoin-100k', NOW() - INTERVAL 1 DAY),
('联合国发布最新气候报告', '全球气候变暖趋势持续，极端天气频现。', '联合国今天发布最新全球气候报告，指出全球平均气温持续升高……', '国际,气候', 'UN News', 'https://news.example.com/un-climate', NOW() - INTERVAL 12 HOUR),
('苹果发布 M4 芯片，性能提升 45%', '苹果今天推出全新 M4 芯片。', '在苹果春季发布会上，全新的 M4 芯片首次亮相，处理性能相比 M3 提升 45%……', '科技,苹果', 'Apple Newsroom', 'https://news.example.com/apple-m4', NOW() - INTERVAL 3 HOUR),
('美国就业数据超预期，股市上涨', '最新就业公告显示经济强劲。', '美国劳工部今日公布最新就业数据，新增非农就业人数远超市场预期……', '财经,美国', 'Bloomberg', 'https://news.example.com/us-job', NOW() - INTERVAL 6 HOUR),
('AI 生成音乐爆火，多国讨论监管框架', 'AI 在音乐行业的影响持续扩大。', '随着 AI 生成音乐平台的爆火，多个国家开始讨论对 AI 内容的版权监管……', 'AI,版权', 'BBC News', 'https://news.example.com/ai-music', NOW() - INTERVAL 9 HOUR),
('SpaceX 成功回收火箭，刷新行业纪录', 'SpaceX 再次成功实现一级火箭回收。', 'SpaceX 今天完成了一次里程碑式的发射任务，实现了第 25 次火箭重复使用……', '科技,航天', 'SpaceX', 'https://news.example.com/spacex-launch', NOW() - INTERVAL 4 HOUR),
('Steam 夏季特惠开启，大量游戏史低', 'Steam 夏促正式开启，大批游戏打折。', 'Steam 夏季特惠已正式开启，包括 GTA5、艾尔登法环等热门游戏均迎来史低价……', '游戏,促销', 'Steam', 'https://news.example.com/steam-sale', NOW() - INTERVAL 30 MINUTE),
('国内首条无人驾驶公交正式运营', '无人驾驶公交线路在深圳正式开放。', '全国首条城市级无人驾驶公交线路今日在深圳正式运营，首批车辆已投入试运行……', '科技,自动驾驶', '新华社', 'https://news.example.com/driverless-bus', NOW() - INTERVAL 1 HOUR),
('特斯拉宣布新款电动车 Model Z 发布', '特斯拉推出全新车型 Model Z。', '特斯拉今日发布了全新电动车 Model Z，新车在续航和智能化方面均有提升……', '科技,汽车', 'Tesla News', 'https://news.example.com/tesla-model-z', NOW() - INTERVAL 7 HOUR),
('谷歌发布 Android 16 正式版', 'Android 16 正式版上线，优化性能和隐私保护。', '谷歌今日发布 Android 16 正式版，带来更强大的隐私管理和系统优化……', '科技,安卓', 'Google Blog', 'https://news.example.com/android-16', NOW() - INTERVAL 2 DAY),
('国家统计局发布最新 GDP 数据', '中国 GDP 增长超预期。', '国家统计局今日公布最新 GDP 数据，显示经济稳中向好……', '财经,中国', '国家统计局', 'https://news.example.com/cn-gdp', NOW() - INTERVAL 1 DAY),
('诺贝尔奖公布最新获奖名单', '诺贝尔奖今年获奖者揭晓。', '诺贝尔奖委员会今天公布了今年的获奖名单，涵盖物理、化学和医学等领域……', '国际,科学', 'Nobel Prize', 'https://news.example.com/nobel-2025', NOW() - INTERVAL 3 DAY),
('百度发布新一代大模型文心一言', '百度文心一言迎来大模型升级。', '百度今日宣布文心一言模型升级，提升中文理解和生成能力……', '科技,AI', '百度新闻', 'https://news.example.com/baidu-wenxin', NOW() - INTERVAL 6 HOUR),
('亚马逊 Prime Day 促销开始', '亚马逊 Prime Day 活动正式开启。', '亚马逊今天启动一年一度的 Prime Day 活动，大量商品大幅折扣……', '电商,促销', 'Amazon News', 'https://news.example.com/prime-day', NOW() - INTERVAL 1 DAY),
('微软发布 Windows 12 预览版', 'Windows 12 预览版上线，优化系统体验。', '微软今日推出 Windows 12 预览版，优化了系统性能和安全性……', '科技,微软', 'Microsoft News', 'https://news.example.com/windows-12', NOW() - INTERVAL 8 HOUR),
('上海车展正式开幕，新车亮相', '2025 上海国际车展开幕。', '2025 上海国际车展今日正式开幕，多款新车亮相……', '汽车,展会', '新华社', 'https://news.example.com/shanghai-auto-show', NOW() - INTERVAL 12 HOUR),
('东京奥运会场馆改造计划公布', '东京奥运会场馆升级改造。', '日本政府今日公布东京奥运会场馆改造计划，提升设施现代化水平……', '体育,奥运', 'NHK News', 'https://news.example.com/tokyo-olympic', NOW() - INTERVAL 2 DAY),
('教育部发布新高考政策', '全国高考政策调整。', '教育部今日发布最新高考政策，包括科目调整和考试时间安排……', '教育,高考', '教育部官网', 'https://news.example.com/gaokao-policy', NOW() - INTERVAL 5 HOUR);

