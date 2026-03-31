/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : nova_nursery

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 30/03/2026 01:09:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_alert
-- ----------------------------
DROP TABLE IF EXISTS `t_alert`;
CREATE TABLE `t_alert`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `alert_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警类型：CONTRACT_EXPIRE-合同到期提醒，EDUCATION_FEE_EXPIRE-保教费到期提醒，MEAL_FEE_EXPIRE-伙食费到期提醒，OVERDUE-逾期未缴费提醒，EDUCATION_FEE_LOW-保教费余额不足提醒，MEAL_FEE_LOW-伙食费余额不足提醒，OTHER_FEE_LOW-其他费用余额不足提醒',
  `alert_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警内容',
  `trigger_time` datetime NOT NULL COMMENT '触发时间',
  `handle_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING-待处理，HANDLED-已处理',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handle_user` bigint NULL DEFAULT NULL COMMENT '处理人',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_alert_type`(`alert_type` ASC) USING BTREE,
  INDEX `idx_handle_status`(`handle_status` ASC) USING BTREE,
  INDEX `idx_alert_handle_status`(`handle_status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_alert
-- ----------------------------
INSERT INTO `t_alert` VALUES (1, 4, 'EDUCATION_FEE_LOW', '赵小芳保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (2, 5, 'EDUCATION_FEE_LOW', '钱小宝保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (3, 6, 'EDUCATION_FEE_LOW', '孙小美保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (4, 7, 'EDUCATION_FEE_LOW', '周小龙保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (5, 9, 'EDUCATION_FEE_LOW', '郑小强保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (6, 10, 'EDUCATION_FEE_LOW', '冯小丽保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (7, 4, 'EDUCATION_FEE_LOW', '幼儿赵小芳保教费余额不足，请及时充值', '2024-03-01 09:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (8, 5, 'EDUCATION_FEE_LOW', '幼儿钱小宝伙食费余额不足，请及时充值', '2024-03-01 09:00:00', 'HANDLED', '2024-03-02 10:00:00', 2, NULL, '2026-03-14 19:42:25', NULL, '2026-03-30 00:17:01', NULL, 0);
INSERT INTO `t_alert` VALUES (9, 6, 'CONTRACT_EXPIRE', '幼儿孙小美合同将于30天后到期，请及时续签', '2024-03-01 09:00:00', 'PENDING', NULL, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-29 23:11:08', NULL, 0);

-- ----------------------------
-- Table structure for t_attendance
-- ----------------------------
DROP TABLE IF EXISTS `t_attendance`;
CREATE TABLE `t_attendance`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `attendance_date` date NOT NULL COMMENT '日期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常到园，2-请假，3-病假，4-缺勤',
  `register_user` bigint NULL DEFAULT NULL COMMENT '登记人',
  `modify_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_child_date`(`child_id` ASC, `attendance_date` ASC) USING BTREE,
  INDEX `idx_attendance_date`(`attendance_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '出勤记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_attendance
-- ----------------------------
INSERT INTO `t_attendance` VALUES (1, 1, '2024-03-01', 3, 4, 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-15 01:03:10', 1, 0);
INSERT INTO `t_attendance` VALUES (2, 1, '2024-03-04', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (3, 1, '2024-03-05', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (4, 1, '2024-03-06', 2, 4, NULL, '家中有事', '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (5, 1, '2024-03-07', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (6, 2, '2024-03-01', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (7, 2, '2024-03-04', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (8, 2, '2024-03-05', 3, 4, NULL, '感冒发烧', '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (9, 2, '2024-03-06', 3, 4, NULL, '感冒发烧', '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (10, 2, '2024-03-07', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (11, 3, '2024-03-01', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (12, 3, '2024-03-04', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (13, 3, '2024-03-05', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (14, 3, '2024-03-06', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (15, 3, '2024-03-07', 1, 4, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (16, 8, '2024-03-01', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (17, 8, '2024-03-04', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (18, 8, '2024-03-05', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (19, 8, '2024-03-06', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (20, 8, '2024-03-07', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (21, 9, '2024-03-01', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (22, 9, '2024-03-04', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (23, 9, '2024-03-05', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (24, 9, '2024-03-06', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (25, 9, '2024-03-07', 1, 6, NULL, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_attendance` VALUES (2032842542410260482, 5, '2024-03-01', 1, 1, NULL, NULL, '2026-03-14 23:33:25', 1, '2026-03-14 23:33:25', 1, 0);
INSERT INTO `t_attendance` VALUES (2032842542410260483, 6, '2024-03-01', 1, 1, NULL, NULL, '2026-03-14 23:33:25', 1, '2026-03-14 23:33:25', 1, 0);
INSERT INTO `t_attendance` VALUES (2032842542443814913, 7, '2024-03-01', 1, 1, NULL, NULL, '2026-03-14 23:33:25', 1, '2026-03-14 23:33:25', 1, 0);
INSERT INTO `t_attendance` VALUES (2032865164384747521, 2, '2026-03-14', 1, 1, 1, NULL, '2026-03-15 01:03:19', 1, '2026-03-15 01:06:39', 1, 0);
INSERT INTO `t_attendance` VALUES (2032865282886418433, 1, '2026-03-14', 1, 1, 1, NULL, '2026-03-15 01:03:47', 1, '2026-03-15 01:06:39', 1, 0);
INSERT INTO `t_attendance` VALUES (2032865737578332161, 3, '2026-03-14', 1, 1, 1, NULL, '2026-03-15 01:05:35', 1, '2026-03-15 01:06:39', 1, 0);
INSERT INTO `t_attendance` VALUES (2032865737578332162, 4, '2026-03-14', 1, 1, 1, NULL, '2026-03-15 01:05:35', 1, '2026-03-15 01:06:39', 1, 0);
INSERT INTO `t_attendance` VALUES (2032866209525612545, 5, '2026-03-14', 4, 1, NULL, NULL, '2026-03-15 01:07:28', 1, '2026-03-15 01:07:28', 1, 0);
INSERT INTO `t_attendance` VALUES (2032866209592721409, 6, '2026-03-14', 4, 1, NULL, NULL, '2026-03-15 01:07:28', 1, '2026-03-15 01:07:28', 1, 0);
INSERT INTO `t_attendance` VALUES (2032866209592721410, 7, '2026-03-14', 1, 1, NULL, NULL, '2026-03-15 01:07:28', 1, '2026-03-15 01:07:28', 1, 0);
INSERT INTO `t_attendance` VALUES (2035339324205776897, 4, '2026-03-21', 1, 1, NULL, NULL, '2026-03-21 20:54:44', 1, '2026-03-21 20:54:44', 1, 0);
INSERT INTO `t_attendance` VALUES (2037931604594860033, 4, '2026-03-28', 4, 1, 1, '', '2026-03-29 00:35:32', 1, '2026-03-29 02:49:40', 1, 0);
INSERT INTO `t_attendance` VALUES (2037931921910734850, 4, '2026-03-25', 2, 1, NULL, NULL, '2026-03-29 00:36:48', 1, '2026-03-29 00:36:48', 1, 0);
INSERT INTO `t_attendance` VALUES (2037932025858170882, 4, '2026-03-24', 2, 1, 1, NULL, '2026-03-29 00:37:13', 1, '2026-03-29 00:40:44', 1, 0);
INSERT INTO `t_attendance` VALUES (2037932103041753089, 4, '2026-03-02', 2, 1, NULL, NULL, '2026-03-29 00:37:31', 1, '2026-03-29 00:37:31', 1, 0);
INSERT INTO `t_attendance` VALUES (2038188535490228226, 4, '2026-03-26', 2, 1, NULL, NULL, '2026-03-29 17:36:29', 1, '2026-03-29 17:36:29', 1, 0);

-- ----------------------------
-- Table structure for t_balance_account
-- ----------------------------
DROP TABLE IF EXISTS `t_balance_account`;
CREATE TABLE `t_balance_account`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `account_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_child_account`(`child_id` ASC, `account_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '余额账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_balance_account
-- ----------------------------
INSERT INTO `t_balance_account` VALUES (1, 1, 'EDUCATION', 4200.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (2, 1, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (3, 2, 'EDUCATION', 4200.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (4, 2, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (5, 3, 'EDUCATION', 4200.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (6, 3, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (9, 5, 'EDUCATION', 2600.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (10, 5, 'MEAL', 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (11, 6, 'EDUCATION', 2600.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (12, 6, 'MEAL', 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (13, 7, 'EDUCATION', 2800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (14, 7, 'MEAL', 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (15, 8, 'EDUCATION', 3500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (16, 8, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (17, 9, 'EDUCATION', 3500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (18, 9, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (19, 10, 'EDUCATION', 3500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (20, 10, 'MEAL', 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_account` VALUES (2038144864241766403, 4, 'EDUCATION', 3500.00, '2026-03-29 14:42:57', NULL, '2026-03-29 14:42:57', NULL, 0);
INSERT INTO `t_balance_account` VALUES (2038260647093534722, 4, 'MEAL', 200.00, '2026-03-29 22:23:02', NULL, '2026-03-29 22:43:01', NULL, 0);

-- ----------------------------
-- Table structure for t_balance_record
-- ----------------------------
DROP TABLE IF EXISTS `t_balance_record`;
CREATE TABLE `t_balance_record`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `account_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户类型',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '变动类型：INCOME-收入，EXPENSE-支出，REFUND-退费，ADJUST-调整',
  `amount` decimal(10, 2) NOT NULL COMMENT '变动金额',
  `after_balance` decimal(10, 2) NOT NULL COMMENT '变动后余额',
  `related_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联单据号',
  `fee_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费用项目',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_related_no`(`related_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '余额变动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_balance_record
-- ----------------------------
INSERT INTO `t_balance_record` VALUES (1, 1, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403001', '保教费', '3月份保教费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (2, 1, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403002', '伙食费', '3月份伙食费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (3, 2, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403004', '保教费', '3月份保教费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (4, 2, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403005', '伙食费', '3月份伙食费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (5, 3, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403006', '保教费', '3月份保教费充值', '2026-03-14 19:42:25', 3, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (6, 3, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403007', '伙食费', '3月份伙食费充值', '2026-03-14 19:42:25', 3, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (7, 8, 'EDUCATION', 'INCOME', 3500.00, 3500.00, 'SK202403008', '保教费', '3月份保教费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (8, 8, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403009', '伙食费', '3月份伙食费充值', '2026-03-14 19:42:25', 2, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_balance_record` VALUES (2038144864308875265, 4, 'EDUCATION', 'INCOME', 3500.00, 3500.00, 'SK202603291056', '3月保育费', '收费入账', '2026-03-29 14:42:57', NULL, '2026-03-29 14:42:57', NULL, 0);
INSERT INTO `t_balance_record` VALUES (2038260647156449282, 4, 'MEAL', 'INCOME', 300.00, 300.00, 'SK202603292656', '3月伙食费', '收费入账', '2026-03-29 22:23:02', NULL, '2026-03-29 22:23:02', NULL, 0);
INSERT INTO `t_balance_record` VALUES (2038265675736883201, 4, 'MEAL', 'EXPENSE', 100.00, 200.00, 'TF202603292560', '伙食费退100', '退费：伙食费退100', '2026-03-29 22:43:01', NULL, '2026-03-29 22:43:01', NULL, 0);

-- ----------------------------
-- Table structure for t_bill
-- ----------------------------
DROP TABLE IF EXISTS `t_bill`;
CREATE TABLE `t_bill`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账单编号',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `contract_id` bigint NULL DEFAULT NULL COMMENT '合同ID',
  `bill_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账单月份，格式：YYYY-MM',
  `education_fee_receivable` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '保教费应收',
  `meal_fee_receivable` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '伙食费应收',
  `other_fee_receivable` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '其他费用应收',
  `education_fee_refund` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '应退保教费',
  `meal_fee_refund` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '应退伙食费',
  `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '减免金额',
  `actual_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际应交',
  `due_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际需缴',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认，CONFIRMED-已确认',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `confirm_user` bigint NULL DEFAULT NULL COMMENT '确认人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bill_no`(`bill_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_child_month`(`child_id` ASC, `bill_month` ASC) USING BTREE,
  INDEX `idx_bill_month`(`bill_month` ASC) USING BTREE,
  INDEX `idx_bill_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '账单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_bill
-- ----------------------------
INSERT INTO `t_bill` VALUES (2035346783095967746, 'ZD202603214176', 4, 2035345341924691970, '2026-03', 3431.82, 800.00, 800.00, 609.09, 727.27, 0.00, 3695.46, 0.00, 'PENDING', NULL, NULL, '2026-03-21 21:24:23', NULL, '2026-03-29 22:23:24', NULL, 0);

-- ----------------------------
-- Table structure for t_bill_item
-- ----------------------------
DROP TABLE IF EXISTS `t_bill_item`;
CREATE TABLE `t_bill_item`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
  `fee_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bill_id`(`bill_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '账单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_bill_item
-- ----------------------------
INSERT INTO `t_bill_item` VALUES (2038188594567000066, 2035346783095967746, 'EDUCATION', '保教费', 3431.82, '月度保教费', '2026-03-29 17:36:43', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038188594567000067, 2035346783095967746, 'MEAL', '伙食费', 800.00, '月度伙食费', '2026-03-29 17:36:43', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038188594567000068, 2035346783095967746, 'OTHER_FEE', '其他费用', 800.00, '其他费用', '2026-03-29 17:36:43', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038188594567000069, 2035346783095967746, 'EDUCATION_REFUND', '保教费退费', 609.09, '请假/病假退费', '2026-03-29 17:36:43', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038188594567000070, 2035346783095967746, 'MEAL_REFUND', '伙食费退费', 727.27, '未到园退费', '2026-03-29 17:36:43', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038253953382764545, 2035346783095967746, 'DISCOUNT', '账单减免', 100.00, '减免100', '2026-03-29 21:56:26', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038254120995540994, 2035346783095967746, 'OTHER_FEE', '添加其他费用100', 100.00, '添加其他费用100', '2026-03-29 21:57:06', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038254346544238594, 2035346783095967746, 'DISCOUNT', '账单减免', 100.00, '再减100', '2026-03-29 21:58:00', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038256151101591554, 2035346783095967746, 'DISCOUNT', '账单减免', 100.00, '111', '2026-03-29 22:05:10', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038256412167655425, 2035346783095967746, 'OTHER_FEE', '100', 100.00, '', '2026-03-29 22:06:12', NULL, '2026-03-29 22:21:32', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260273955667969, 2035346783095967746, 'EDUCATION', '保教费', 3431.82, '月度保教费', '2026-03-29 22:21:33', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260273955667970, 2035346783095967746, 'MEAL', '伙食费', 800.00, '月度伙食费', '2026-03-29 22:21:33', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260273955667971, 2035346783095967746, 'OTHER_FEE', '其他费用', 800.00, '其他费用', '2026-03-29 22:21:33', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260273955667972, 2035346783095967746, 'EDUCATION_REFUND', '保教费退费', 609.09, '请假/病假退费', '2026-03-29 22:21:33', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260274010193921, 2035346783095967746, 'MEAL_REFUND', '伙食费退费', 727.27, '未到园退费', '2026-03-29 22:21:33', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260357577506818, 2035346783095967746, 'DISCOUNT', '账单减免', 100.00, '111', '2026-03-29 22:21:53', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260386795028481, 2035346783095967746, 'OTHER_FEE', '111', 100.00, '', '2026-03-29 22:22:00', NULL, '2026-03-29 22:23:24', NULL, 1);
INSERT INTO `t_bill_item` VALUES (2038260739875733505, 2035346783095967746, 'EDUCATION', '保教费', 3431.82, '月度保教费', '2026-03-29 22:23:24', NULL, '2026-03-29 22:23:24', NULL, 0);
INSERT INTO `t_bill_item` VALUES (2038260739875733506, 2035346783095967746, 'MEAL', '伙食费', 800.00, '月度伙食费', '2026-03-29 22:23:24', NULL, '2026-03-29 22:23:24', NULL, 0);
INSERT INTO `t_bill_item` VALUES (2038260739875733507, 2035346783095967746, 'OTHER_FEE', '其他费用', 800.00, '其他费用', '2026-03-29 22:23:24', NULL, '2026-03-29 22:23:24', NULL, 0);
INSERT INTO `t_bill_item` VALUES (2038260739875733508, 2035346783095967746, 'EDUCATION_REFUND', '保教费退费', 609.09, '请假/病假退费', '2026-03-29 22:23:24', NULL, '2026-03-29 22:23:24', NULL, 0);
INSERT INTO `t_bill_item` VALUES (2038260739938648066, 2035346783095967746, 'MEAL_REFUND', '伙食费退费', 727.27, '未到园退费', '2026-03-29 22:23:24', NULL, '2026-03-29 22:23:24', NULL, 0);

-- ----------------------------
-- Table structure for t_child
-- ----------------------------
DROP TABLE IF EXISTS `t_child`;
CREATE TABLE `t_child`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` tinyint NOT NULL COMMENT '性别：1-男，2-女',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `enroll_date` date NOT NULL COMMENT '入托日期',
  `parent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家长姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `wechat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  `pickup_person` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接送人信息',
  `allergy_history` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '过敏史',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-在园，2-休学，3-退学，4-毕业',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_child_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '幼儿表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_child
-- ----------------------------
INSERT INTO `t_child` VALUES (1, '张小明2', 1, '2023-06-15', 1, '2024-01-15', '张三', '13900139001', 'zhangsan_wx', '李四', '13900139002', '张三、李四', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (2, '李小红', 2, '2023-08-20', 1, '2024-02-01', '李四', '13900139003', 'lisi_wx', '王五', '13900139004', '李四、王五', '海鲜过敏', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (3, '王小刚', 1, '2023-04-10', 1, '2024-01-20', '王五', '13900139005', 'wangwu_wx', '赵六', '13900139006', '王五', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (4, '赵小芳', 2, '2023-02-25', 1, '2024-03-01', '赵六', '13900139007', 'zhaoliu_wx', '钱七', '13900139008', '赵六、钱七', '花生过敏', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (5, '钱小宝', 1, '2023-05-12', 2, '2024-02-15', '钱七', '13900139009', 'qianqi_wx', '孙八', '13900139010', '钱七', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (6, '孙小美', 2, '2023-07-08', 2, '2024-02-20', '孙八', '13900139011', 'sunba_wx', '周九', '13900139012', '孙八、周九', '牛奶过敏', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (7, '周小龙', 1, '2023-09-01', 2, '2024-03-05', '周九', '13900139013', 'zhoujiu_wx', '吴十', '13900139014', '周九', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (8, '吴小燕', 2, '2022-12-15', 3, '2024-01-10', '吴十', '13900139015', 'wushi_wx', '郑一', '13900139016', '吴十、郑一', '鸡蛋过敏', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (9, '郑小强', 1, '2022-10-20', 3, '2024-01-25', '郑一', '13900139017', 'zhengyi_wx', '冯二', '13900139018', '郑一', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_child` VALUES (10, '冯小丽', 2, '2022-11-30', 3, '2024-02-10', '冯二', '13900139019', 'fenger_wx', '陈三', '13900139020', '冯二、陈三', '无', 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_class
-- ----------------------------
DROP TABLE IF EXISTS `t_class`;
CREATE TABLE `t_class`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '班主任ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-已关闭，1-正常',
  `current_count` int NOT NULL DEFAULT 0 COMMENT '在园人数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_class
-- ----------------------------
INSERT INTO `t_class` VALUES (1, '小班A班', 4, '适合1-2岁幼儿', 1, 4, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_class` VALUES (2, '小班B班', 5, '适合1-2岁幼儿', 1, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_class` VALUES (3, '中班', 6, '适合2-3岁幼儿', 1, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_contract
-- ----------------------------
DROP TABLE IF EXISTS `t_contract`;
CREATE TABLE `t_contract`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合同编号',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `contract_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合同名称',
  `course_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程类型',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '到期日期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-生效中，2-已到期，3-已变更，4-已终止',
  `attachment_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_contract_no`(`contract_no` ASC) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_contract_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '合同表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_contract
-- ----------------------------
INSERT INTO `t_contract` VALUES (1, 'HT20240001', 1, '张小明入园合同', '全日制', '2024-01-15', '2025-01-14', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (2, 'HT20240002', 2, '李小红入园合同', '全日制', '2024-02-01', '2025-01-31', 4, NULL, '2026-03-14 19:42:25', NULL, '2026-03-21 10:48:42', NULL, 0);
INSERT INTO `t_contract` VALUES (3, 'HT20240003', 3, '王小刚入园合同', '全日制', '2024-01-20', '2025-01-19', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (4, 'HT20240004', 4, '赵小芳入园合同', '全日制', '2024-03-01', '2025-02-28', 2, NULL, '2026-03-14 19:42:25', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract` VALUES (5, 'HT20240005', 5, '钱小宝入园合同', '半日制', '2024-02-15', '2025-02-14', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (6, 'HT20240006', 6, '孙小美入园合同', '半日制', '2024-02-20', '2025-02-19', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (7, 'HT20240007', 7, '周小龙入园合同', '半日制', '2024-03-05', '2025-03-04', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (8, 'HT20240008', 8, '吴小燕入园合同', '全日制', '2024-01-10', '2025-01-09', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (9, 'HT20240009', 9, '郑小强入园合同', '全日制', '2024-01-25', '2025-01-24', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (10, 'HT20240010', 10, '冯小丽入园合同', '全日制', '2024-02-10', '2025-02-09', 1, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract` VALUES (2035191944722493442, 'HT202603219104', 2, '测试李小红', 'MONTHLY', '2026-03-01', '2026-03-31', 3, NULL, '2026-03-21 11:09:06', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract` VALUES (2035197311825125377, 'HT202603216240', 2, '测试李小红(变更)', 'MONTHLY', '2026-04-01', '2026-03-21', 2, NULL, '2026-03-21 11:30:26', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract` VALUES (2035198046948278274, 'HT202603212752', 2, '测试李小红(变更)(续签)', 'MONTHLY', '2026-03-31', '2026-04-30', 4, NULL, '2026-03-21 11:33:21', NULL, '2026-03-21 21:17:23', NULL, 0);
INSERT INTO `t_contract` VALUES (2035345341924691970, 'HT202603219408', 4, '赵小芳入园合同(续签)', '全日制', '2026-03-01', '2026-03-31', 1, NULL, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);

-- ----------------------------
-- Table structure for t_contract_change_log
-- ----------------------------
DROP TABLE IF EXISTS `t_contract_change_log`;
CREATE TABLE `t_contract_change_log`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '变更类型：CHANGE-变更, TERMINATE-终止, RENEW-续签',
  `change_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '变更原因',
  `before_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更前内容（JSON格式）',
  `after_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更后内容（JSON格式）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '合同变更记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_contract_change_log
-- ----------------------------
INSERT INTO `t_contract_change_log` VALUES (2035186808721715201, 2, 'TERMINATE', '测试', '{\"id\":2,\"contractNo\":\"HT20240002\",\"childId\":2,\"contractName\":\"李小红入园合同\",\"courseType\":\"全日制\",\"startDate\":\"2024-02-01\",\"endDate\":\"2025-01-31\",\"status\":1,\"attachmentUrl\":null,\"createTime\":\"2026-03-14T19:42:25\",\"createUser\":null,\"updateTime\":\"2026-03-14T19:42:25\",\"updateUser\":null,\"deleted\":0}', NULL, '2026-03-21 10:48:42', NULL);
INSERT INTO `t_contract_change_log` VALUES (2035197312060006402, 2035191944722493442, 'CHANGE', '变更测试', '{\"id\":2035191944722493442,\"contractNo\":\"HT202603219104\",\"childId\":2,\"contractName\":\"测试李小红\",\"courseType\":\"MONTHLY\",\"startDate\":\"2026-03-01\",\"endDate\":\"2026-03-31\",\"status\":3,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T11:09:06\",\"createUser\":null,\"updateTime\":\"2026-03-21T11:30:25.8907745\",\"updateUser\":null,\"deleted\":0}', '{\"id\":2035197311825125377,\"contractNo\":\"HT202603216240\",\"childId\":2,\"contractName\":\"测试李小红(变更)\",\"courseType\":\"MONTHLY\",\"startDate\":\"2026-04-01\",\"endDate\":\"2026-03-21\",\"status\":1,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T11:30:25.9189449\",\"createUser\":null,\"updateTime\":\"2026-03-21T11:30:25.9189449\",\"updateUser\":null,\"deleted\":null}', '2026-03-21 11:30:26', NULL);
INSERT INTO `t_contract_change_log` VALUES (2035198047095078913, 2035197311825125377, 'RENEW', '合同续签', '{\"id\":2035197311825125377,\"contractNo\":\"HT202603216240\",\"childId\":2,\"contractName\":\"测试李小红(变更)\",\"courseType\":\"MONTHLY\",\"startDate\":\"2026-04-01\",\"endDate\":\"2026-03-21\",\"status\":2,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T11:30:26\",\"createUser\":null,\"updateTime\":\"2026-03-21T11:33:21.1719659\",\"updateUser\":null,\"deleted\":0}', '{\"id\":2035198046948278274,\"contractNo\":\"HT202603212752\",\"childId\":2,\"contractName\":\"测试李小红(变更)(续签)\",\"courseType\":\"MONTHLY\",\"startDate\":\"2026-03-31\",\"endDate\":\"2026-04-30\",\"status\":1,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T11:33:21.1966704\",\"createUser\":null,\"updateTime\":\"2026-03-21T11:33:21.1966704\",\"updateUser\":null,\"deleted\":null}', '2026-03-21 11:33:21', NULL);
INSERT INTO `t_contract_change_log` VALUES (2035345023354720257, 2035198046948278274, 'TERMINATE', '测试', '{\"id\":2035198046948278274,\"contractNo\":\"HT202603212752\",\"childId\":2,\"contractName\":\"测试李小红(变更)(续签)\",\"courseType\":\"MONTHLY\",\"startDate\":\"2026-03-31\",\"endDate\":\"2026-04-30\",\"status\":1,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T11:33:21\",\"createUser\":null,\"updateTime\":\"2026-03-21T11:33:21\",\"updateUser\":null,\"deleted\":0}', NULL, '2026-03-21 21:17:23', NULL);
INSERT INTO `t_contract_change_log` VALUES (2035345342054715397, 4, 'RENEW', '合同续签', '{\"id\":4,\"contractNo\":\"HT20240004\",\"childId\":4,\"contractName\":\"赵小芳入园合同\",\"courseType\":\"全日制\",\"startDate\":\"2024-03-01\",\"endDate\":\"2025-02-28\",\"status\":2,\"attachmentUrl\":null,\"createTime\":\"2026-03-14T19:42:25\",\"createUser\":null,\"updateTime\":\"2026-03-21T21:18:39.0561763\",\"updateUser\":null,\"deleted\":0}', '{\"id\":2035345341924691970,\"contractNo\":\"HT202603219408\",\"childId\":4,\"contractName\":\"赵小芳入园合同(续签)\",\"courseType\":\"全日制\",\"startDate\":\"2026-03-01\",\"endDate\":\"2026-03-31\",\"status\":1,\"attachmentUrl\":null,\"createTime\":\"2026-03-21T21:18:39.0608397\",\"createUser\":null,\"updateTime\":\"2026-03-21T21:18:39.0608397\",\"updateUser\":null,\"deleted\":null}', '2026-03-21 21:18:39', NULL);

-- ----------------------------
-- Table structure for t_contract_fee_item
-- ----------------------------
DROP TABLE IF EXISTS `t_contract_fee_item`;
CREATE TABLE `t_contract_fee_item`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年龄段',
  `education_fee` decimal(10, 2) NOT NULL COMMENT '保教费标准',
  `meal_fee` decimal(10, 2) NOT NULL COMMENT '伙食费标准',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '合同年龄段收费明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_contract_fee_item
-- ----------------------------
INSERT INTO `t_contract_fee_item` VALUES (1, 1, '13-18月', 4200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2, 1, '19-24月', 3800.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (3, 2, '13-18月', 4200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (4, 2, '19-24月', 3800.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (5, 3, '13-18月', 4200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (6, 3, '19-24月', 3800.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (7, 4, '13-18月', 4200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (8, 5, '13-18月', 2600.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (9, 5, '19-24月', 2400.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (10, 6, '13-18月', 2600.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (11, 7, '4-12月', 2800.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (12, 7, '13-18月', 2600.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (13, 8, '25-36月', 3500.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (14, 9, '25-36月', 3500.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (15, 10, '25-36月', 3500.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035191944781213698, 2035191944722493442, '4-12月', 4610.00, 800.00, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035191944781213699, 2035191944722493442, '13-18月', 4200.00, 800.00, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035191944781213700, 2035191944722493442, '19-24月', 3800.00, 800.00, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035191944781213701, 2035191944722493442, '25-36月', 3500.00, 800.00, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035191944844128258, 2035191944722493442, '37月以上', 3200.00, 800.00, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035197311867068417, 2035197311825125377, '4-12月', 4610.00, 800.00, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035197311925788673, 2035197311825125377, '13-18月', 4100.00, 800.00, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035197311925788674, 2035197311825125377, '19-24月', 3800.00, 800.00, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035197311925788675, 2035197311825125377, '25-36月', 3500.00, 800.00, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035197311925788676, 2035197311825125377, '37月以上', 3200.00, 800.00, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035198046965055489, 2035198046948278274, '4-12月', 4610.00, 800.00, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035198046965055490, 2035198046948278274, '13-18月', 4100.00, 800.00, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035198046965055491, 2035198046948278274, '19-24月', 3800.00, 800.00, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035198046965055492, 2035198046948278274, '25-36月', 3500.00, 800.00, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035198047027970049, 2035198046948278274, '37月以上', 3200.00, 800.00, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035345341924691971, 2035345341924691970, '4-12月', 4610.00, 800.00, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035345341987606529, 2035345341924691970, '13-18月', 4200.00, 800.00, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035345341987606530, 2035345341924691970, '19-24月', 3800.00, 800.00, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035345341987606531, 2035345341924691970, '25-36月', 3500.00, 800.00, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_fee_item` VALUES (2035345341987606532, 2035345341924691970, '37月以上', 3200.00, 800.00, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);

-- ----------------------------
-- Table structure for t_contract_other_fee
-- ----------------------------
DROP TABLE IF EXISTS `t_contract_other_fee`;
CREATE TABLE `t_contract_other_fee`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `fee_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目编码',
  `fee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目名称',
  `charge_cycle` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收取周期',
  `fee_standard` decimal(10, 2) NOT NULL COMMENT '计费标准',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '合同其他费用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_contract_other_fee
-- ----------------------------
INSERT INTO `t_contract_other_fee` VALUES (1, 1, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2, 1, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (3, 1, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (4, 2, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (5, 2, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (6, 3, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (7, 4, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (8, 5, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (9, 6, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (10, 7, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (11, 8, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (12, 9, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (13, 10, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035191944844128259, 2035191944722493442, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035191944844128260, 2035191944722493442, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035191944844128261, 2035191944722493442, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 11:09:06', NULL, '2026-03-21 11:09:06', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035197311997091841, 2035197311825125377, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035197311997091842, 2035197311825125377, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035197311997091843, 2035197311825125377, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 11:30:26', NULL, '2026-03-21 11:30:26', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035198047027970050, 2035198046948278274, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035198047027970051, 2035198046948278274, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035198047027970052, 2035198046948278274, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 11:33:21', NULL, '2026-03-21 11:33:21', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035345342054715394, 2035345341924691970, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035345342054715395, 2035345341924691970, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);
INSERT INTO `t_contract_other_fee` VALUES (2035345342054715396, 2035345341924691970, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 21:18:39', NULL, '2026-03-21 21:18:39', NULL, 0);

-- ----------------------------
-- Table structure for t_fee_template
-- ----------------------------
DROP TABLE IF EXISTS `t_fee_template`;
CREATE TABLE `t_fee_template`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `course_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程类型',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1.0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收费标准模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_fee_template
-- ----------------------------
INSERT INTO `t_fee_template` VALUES (1, '2024年春季收费标准', '全日制', 0, '1', '2026-03-14 19:42:25', NULL, '2026-03-21 09:59:34', NULL, 0);
INSERT INTO `t_fee_template` VALUES (2, '2024年春季收费标准', '半日制', 0, '1', '2026-03-14 19:42:25', NULL, '2026-03-21 09:59:35', NULL, 0);
INSERT INTO `t_fee_template` VALUES (2035172648143228929, '首报体验课程', 'MONTHLY', 1, '2', '2026-03-21 09:52:26', NULL, '2026-03-21 09:57:11', NULL, 0);
INSERT INTO `t_fee_template` VALUES (2035174315928543234, '按学期收费', 'SEMESTER', 1, '2', '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:31', NULL, 0);

-- ----------------------------
-- Table structure for t_fee_template_item
-- ----------------------------
DROP TABLE IF EXISTS `t_fee_template_item`;
CREATE TABLE `t_fee_template_item`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年龄段：4-12月、13-18月、19-24月、25-36月、37月以上',
  `education_fee` decimal(10, 2) NOT NULL COMMENT '保教费标准',
  `meal_fee` decimal(10, 2) NOT NULL COMMENT '伙食费标准',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收费标准模板明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_fee_template_item
-- ----------------------------
INSERT INTO `t_fee_template_item` VALUES (1, 1, '4-12月', 4500.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2, 1, '13-18月', 4200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (3, 1, '19-24月', 3800.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (4, 1, '25-36月', 3500.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (5, 1, '37月以上', 3200.00, 800.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (6, 2, '4-12月', 2800.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (7, 2, '13-18月', 2600.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (8, 2, '19-24月', 2400.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (9, 2, '25-36月', 2200.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (10, 2, '37月以上', 2000.00, 500.00, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035172648155811842, 2035172648143228929, '4-12月', 4610.00, 800.00, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035172648222920705, 2035172648143228929, '13-18月', 4200.00, 800.00, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035172648222920706, 2035172648143228929, '19-24月', 3800.00, 800.00, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035172648222920707, 2035172648143228929, '25-36月', 3500.00, 800.00, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035172648222920708, 2035172648143228929, '37月以上', 3200.00, 800.00, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035174315928543235, 2035174315928543234, '4-12月', 4500.00, 800.00, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035174315928543236, 2035174315928543234, '13-18月', 4200.00, 800.00, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035174316029206530, 2035174315928543234, '19-24月', 3800.00, 800.00, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035174316079538178, 2035174315928543234, '25-36月', 3500.00, 800.00, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_fee_template_item` VALUES (2035174316079538179, 2035174315928543234, '37月以上', 3200.00, 800.00, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `menu_type` tinyint NOT NULL COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
  `route_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_menu_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (1, '系统管理', 0, 1, '/system', 'Layout', NULL, 'setting', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (2, '用户管理', 1, 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (4, '菜单管理', 1, 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (5, '用户查询', 2, 3, NULL, NULL, 'system:user:query', NULL, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (6, '用户新增', 2, 3, NULL, NULL, 'system:user:add', NULL, 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (7, '用户修改', 2, 3, NULL, NULL, 'system:user:edit', NULL, 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (8, '用户删除', 2, 3, NULL, NULL, 'system:user:delete', NULL, 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (9, '角色查询', 3, 3, NULL, NULL, 'system:role:query', NULL, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (10, '角色新增', 3, 3, NULL, NULL, 'system:role:add', NULL, 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (11, '角色修改', 3, 3, NULL, NULL, 'system:role:edit', NULL, 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (12, '角色删除', 3, 3, NULL, NULL, 'system:role:delete', NULL, 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (13, '托育管理', 0, 1, '/nursery', 'Layout', NULL, 'education', 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (14, '班级管理', 13, 2, '/class', 'class/index', 'nursery:class:list', 'peoples', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (15, '幼儿管理', 13, 2, '/child', 'child/index', 'nursery:child:list', 'user', 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (16, '出勤管理', 13, 2, '/attendance', 'attendance/index', 'nursery:attendance:list', 'date', 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (17, '班级查询', 14, 3, NULL, NULL, 'nursery:class:query', NULL, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (18, '班级新增', 14, 3, NULL, NULL, 'nursery:class:add', NULL, 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (19, '班级修改', 14, 3, NULL, NULL, 'nursery:class:edit', NULL, 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (20, '班级删除', 14, 3, NULL, NULL, 'nursery:class:delete', NULL, 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (21, '幼儿查询', 15, 3, NULL, NULL, 'nursery:child:query', NULL, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (22, '幼儿新增', 15, 3, NULL, NULL, 'nursery:child:add', NULL, 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (23, '幼儿修改', 15, 3, NULL, NULL, 'nursery:child:edit', NULL, 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (24, '幼儿删除', 15, 3, NULL, NULL, 'nursery:child:delete', NULL, 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (25, '财务管理', 0, 1, '/finance', 'Layout', NULL, 'money', 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (26, '收费标准', 25, 2, '/payment/item', 'payment/item', 'finance:template:list', 'skill', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-21 12:12:32', NULL, 0);
INSERT INTO `t_menu` VALUES (27, '合同管理', 25, 2, '/contract', 'contract/index', 'finance:contract:list', 'documentation', 2, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (28, '收费管理', 25, 2, '/payment/list', 'payment/index', 'finance:payment:list', 'money', 3, 1, '2026-03-14 19:42:25', NULL, '2026-03-21 11:45:30', NULL, 0);
INSERT INTO `t_menu` VALUES (29, '退费管理', 25, 2, '/payment/refund', 'payment/refund', 'finance:refund:list', 'money', 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-21 11:45:30', NULL, 0);
INSERT INTO `t_menu` VALUES (30, '账单管理', 25, 2, '/bill', 'bill/index', 'finance:bill:list', 'list', 5, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (31, '余额管理', 25, 2, '/payment/balance', 'payment/balance', 'finance:balance:list', 'money', 6, 1, '2026-03-14 19:42:25', NULL, '2026-03-21 11:45:30', NULL, 0);
INSERT INTO `t_menu` VALUES (32, '预警中心', 0, 1, '/alert', 'Layout', NULL, 'message', 4, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (33, '预警记录', 32, 2, '/alert', 'alert/index', 'alert:record:list', 'message', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_menu` VALUES (34, '日志管理', 1, 2, '/system/log', 'system/log/index', 'system:log:list', 'log', 5, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operation_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作IP',
  `operation_module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作模块',
  `operation_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作内容',
  `operation_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作结果：SUCCESS-成功，FAIL-失败',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_operation_module`(`operation_module` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_operation_log
-- ----------------------------
INSERT INTO `t_operation_log` VALUES (1, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：finance01', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (2, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：finance02', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (3, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：teacher01', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (4, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：小班A班', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (5, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：小班B班', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (6, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：中班', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (7, 'finance01', 2, '127.0.0.1', '收费管理', '收取张小明保教费4200元', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (8, 'finance01', 2, '127.0.0.1', '收费管理', '收取张小明伙食费800元', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (9, 'teacher01', 4, '127.0.0.1', '出勤管理', '登记2024-03-01出勤记录', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_operation_log` VALUES (10, 'teacher01', 4, '127.0.0.1', '出勤管理', '登记2024-03-04出勤记录', 'SUCCESS', NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_other_fee_template
-- ----------------------------
DROP TABLE IF EXISTS `t_other_fee_template`;
CREATE TABLE `t_other_fee_template`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `fee_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目编码',
  `fee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目名称',
  `charge_cycle` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收取周期：ONCE-一次性，MONTHLY-按月，SEMESTER-按学期',
  `fee_standard` decimal(10, 2) NOT NULL COMMENT '计费标准',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '其他费用模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_other_fee_template
-- ----------------------------
INSERT INTO `t_other_fee_template` VALUES (1, 1, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2, 1, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (3, 1, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (4, 2, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (5, 2, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035172648285835265, 2035172648143228929, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035172648285835266, 2035172648143228929, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035172648285835267, 2035172648143228929, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 09:52:26', NULL, '2026-03-21 09:52:26', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035174316079538180, 2035174315928543234, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035174316142452738, 2035174315928543234, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);
INSERT INTO `t_other_fee_template` VALUES (2035174316142452739, 2035174315928543234, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, '2026-03-21 09:59:03', NULL, '2026-03-21 09:59:03', NULL, 0);

-- ----------------------------
-- Table structure for t_payment
-- ----------------------------
DROP TABLE IF EXISTS `t_payment`;
CREATE TABLE `t_payment`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `receipt_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '票据号',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `payment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收费类型',
  `fee_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收费方式：CASH-现金，WECHAT-微信，ALIPAY-支付宝，BANK-银行转账',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VALID' COMMENT '状态：VALID-有效，VOIDED-已作废',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_receipt_no`(`receipt_no` ASC) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_payment_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收费记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_payment
-- ----------------------------
INSERT INTO `t_payment` VALUES (1, 'SK202403001', 1, 'EDUCATION', '保教费', 4200.00, 'WECHAT', 'VALID', '3月份保教费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (2, 'SK202403002', 1, 'MEAL', '伙食费', 800.00, 'WECHAT', 'VALID', '3月份伙食费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (3, 'SK202403003', 1, 'OTHER', '材料费', 500.00, 'CASH', 'VALID', '春季学期材料费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (4, 'SK202403004', 2, 'EDUCATION', '保教费', 4200.00, 'ALIPAY', 'VALID', '3月份保教费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (5, 'SK202403005', 2, 'MEAL', '伙食费', 800.00, 'ALIPAY', 'VALID', '3月份伙食费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (6, 'SK202403006', 3, 'EDUCATION', '保教费', 4200.00, 'WECHAT', 'VALID', '3月份保教费', '2026-03-14 19:42:25', 3, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (7, 'SK202403007', 3, 'MEAL', '伙食费', 800.00, 'WECHAT', 'VALID', '3月份伙食费', '2026-03-14 19:42:25', 3, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (8, 'SK202403008', 8, 'EDUCATION', '保教费', 3500.00, 'BANK', 'VALID', '3月份保教费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (9, 'SK202403009', 8, 'MEAL', '伙食费', 800.00, 'BANK', 'VALID', '3月份伙食费', '2026-03-14 19:42:25', 2, '2026-03-29 14:26:28', NULL, 0);
INSERT INTO `t_payment` VALUES (2038144864241766402, 'SK202603291056', 4, 'EDUCATION', '3月保育费', 3500.00, 'WECHAT', 'VALID', '', '2026-03-29 14:42:57', NULL, '2026-03-29 14:42:57', NULL, 0);
INSERT INTO `t_payment` VALUES (2038260647093534721, 'SK202603292656', 4, 'MEAL', '3月伙食费', 300.00, 'CASH', 'VALID', '', '2026-03-29 22:23:02', NULL, '2026-03-29 22:23:02', NULL, 0);

-- ----------------------------
-- Table structure for t_refund
-- ----------------------------
DROP TABLE IF EXISTS `t_refund`;
CREATE TABLE `t_refund`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '退费编号',
  `child_id` bigint NOT NULL COMMENT '幼儿ID',
  `account_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
  `fee_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用项目',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退费原因',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，COMPLETED-已完成，CANCELLED-已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '退费记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_refund
-- ----------------------------
INSERT INTO `t_refund` VALUES (2038265675661385729, 'TF202603292560', 4, 'MEAL', '伙食费退100', 100.00, '伙食费退100', 'COMPLETED', '2026-03-29 22:43:01', NULL, '2026-03-29 22:43:01', NULL, 0);

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_role_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, 'PRINCIPAL', '园长', 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role` VALUES (2, 'FINANCE', '财务', 1, 2, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role` VALUES (3, 'TEACHER', '老师', 1, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role_menu
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (1, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (2, 1, 2, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (3, 1, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (4, 1, 4, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (5, 1, 5, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (6, 1, 6, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (7, 1, 7, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (8, 1, 8, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (9, 1, 9, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (10, 1, 10, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (11, 1, 11, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (12, 1, 12, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (13, 1, 13, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (14, 1, 14, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (15, 1, 15, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (16, 1, 16, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (17, 1, 17, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (18, 1, 18, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (19, 1, 19, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (20, 1, 20, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (21, 1, 21, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (22, 1, 22, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (23, 1, 23, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (24, 1, 24, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (25, 1, 25, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (26, 1, 26, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (27, 1, 27, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (28, 1, 28, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (29, 1, 29, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (30, 1, 30, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (31, 1, 31, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (32, 1, 32, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (33, 1, 33, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (34, 1, 34, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (35, 2, 25, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (36, 2, 26, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (37, 2, 27, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (38, 2, 28, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (39, 2, 29, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (40, 2, 30, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (41, 2, 31, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (42, 2, 32, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (43, 2, 33, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (44, 3, 13, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (45, 3, 14, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (46, 3, 15, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (47, 3, 16, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (48, 3, 17, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_role_menu` VALUES (49, 3, 21, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_system_config
-- ----------------------------
DROP TABLE IF EXISTS `t_system_config`;
CREATE TABLE `t_system_config`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_system_config
-- ----------------------------
INSERT INTO `t_system_config` VALUES (1, 'LEAVE_REFUND_THRESHOLD', '3', '事假起算天数，超过此天数才退保教费', 1, '2026-03-28 23:25:09', NULL, '2026-03-28 23:25:09', NULL, 0);
INSERT INTO `t_system_config` VALUES (2, 'SICK_REFUND_THRESHOLD', '3', '病假起算天数，超过此天数才退保教费', 1, '2026-03-28 23:25:09', NULL, '2026-03-28 23:25:09', NULL, 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `login_fail_count` int NOT NULL DEFAULT 0 COMMENT '登录失败次数',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '张园长', '13800138001', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user` VALUES (2, 'finance01', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '李财务', '13800138002', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user` VALUES (3, 'finance02', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '王财务1', '13800138003', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:43:41', NULL, 0);
INSERT INTO `t_user` VALUES (4, 'teacher01', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '陈老师', '13800138004', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user` VALUES (5, 'teacher02', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '刘老师', '13800138005', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user` VALUES (6, 'teacher03', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '赵老师', '13800138006', 1, 0, NULL, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES (1, 1, 1, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user_role` VALUES (2, 2, 2, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user_role` VALUES (4, 4, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user_role` VALUES (5, 5, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user_role` VALUES (6, 6, 3, '2026-03-14 19:42:25', NULL, '2026-03-14 19:42:25', NULL, 0);
INSERT INTO `t_user_role` VALUES (2032784728895758338, 3, 2, '2026-03-14 19:43:41', NULL, '2026-03-14 19:43:41', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
