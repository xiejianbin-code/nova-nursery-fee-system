-- =====================================================
-- Nova托育园管理系统 - 数据库初始化脚本
-- 数据库：nova_nursery
-- 字符集：utf8mb4
-- 排序规则：utf8mb4_unicode_ci
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `nova_nursery` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `nova_nursery`;

-- =====================================================
-- 1. 用户表 t_user
-- =====================================================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `login_fail_count` INT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
    `lock_time` DATETIME DEFAULT NULL COMMENT '锁定时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 角色表 t_role
-- =====================================================
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =====================================================
-- 3. 菜单表 t_menu
-- =====================================================
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
    `route_path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component_path` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- =====================================================
-- 4. 用户角色关联表 t_user_role
-- =====================================================
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 5. 角色菜单关联表 t_role_menu
-- =====================================================
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- =====================================================
-- 6. 班级表 t_class
-- =====================================================
DROP TABLE IF EXISTS `t_class`;
CREATE TABLE `t_class` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `teacher_id` BIGINT DEFAULT NULL COMMENT '班主任ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已关闭，1-正常',
    `current_count` INT NOT NULL DEFAULT 0 COMMENT '在园人数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- =====================================================
-- 7. 幼儿表 t_child
-- =====================================================
DROP TABLE IF EXISTS `t_child`;
CREATE TABLE `t_child` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT NOT NULL COMMENT '性别：1-男，2-女',
    `birth_date` DATE NOT NULL COMMENT '出生日期',
    `class_id` BIGINT DEFAULT NULL COMMENT '班级ID',
    `enroll_date` DATE NOT NULL COMMENT '入托日期',
    `parent_name` VARCHAR(50) NOT NULL COMMENT '家长姓名',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `wechat` VARCHAR(50) DEFAULT NULL COMMENT '微信',
    `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    `pickup_person` VARCHAR(200) DEFAULT NULL COMMENT '接送人信息',
    `allergy_history` VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在园，2-休学，3-退学，4-毕业',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='幼儿表';

-- =====================================================
-- 8. 收费标准模板表 t_fee_template
-- =====================================================
DROP TABLE IF EXISTS `t_fee_template`;
CREATE TABLE `t_fee_template` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `version` VARCHAR(20) NOT NULL DEFAULT '1.0' COMMENT '版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收费标准模板表';

-- =====================================================
-- 9. 收费标准模板明细表 t_fee_template_item
-- =====================================================
DROP TABLE IF EXISTS `t_fee_template_item`;
CREATE TABLE `t_fee_template_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `template_id` BIGINT NOT NULL COMMENT '模板ID',
    `age_range` VARCHAR(20) NOT NULL COMMENT '年龄段：4-12月、13-18月、19-24月、25-36月、37月以上',
    `education_fee` DECIMAL(10,2) NOT NULL COMMENT '保教费标准',
    `meal_fee` DECIMAL(10,2) NOT NULL COMMENT '伙食费标准',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收费标准模板明细表';

-- =====================================================
-- 10. 其他费用模板表 t_other_fee_template
-- =====================================================
DROP TABLE IF EXISTS `t_other_fee_template`;
CREATE TABLE `t_other_fee_template` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `template_id` BIGINT NOT NULL COMMENT '模板ID',
    `fee_code` VARCHAR(50) NOT NULL COMMENT '费用项目编码',
    `fee_name` VARCHAR(100) NOT NULL COMMENT '费用项目名称',
    `charge_cycle` VARCHAR(20) NOT NULL COMMENT '收取周期：ONCE-一次性，MONTHLY-按月，SEMESTER-按学期',
    `fee_standard` DECIMAL(10,2) NOT NULL COMMENT '计费标准',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='其他费用模板表';

-- =====================================================
-- 11. 合同表 t_contract
-- =====================================================
DROP TABLE IF EXISTS `t_contract`;
CREATE TABLE `t_contract` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `contract_no` VARCHAR(50) NOT NULL COMMENT '合同编号',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `contract_name` VARCHAR(100) NOT NULL COMMENT '合同名称',
    `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '到期日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-生效中，2-已到期，3-已变更，4-已终止',
    `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contract_no` (`contract_no`),
    KEY `idx_child_id` (`child_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';

-- =====================================================
-- 12. 合同年龄段收费明细表 t_contract_fee_item
-- =====================================================
DROP TABLE IF EXISTS `t_contract_fee_item`;
CREATE TABLE `t_contract_fee_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `contract_id` BIGINT NOT NULL COMMENT '合同ID',
    `age_range` VARCHAR(20) NOT NULL COMMENT '年龄段',
    `education_fee` DECIMAL(10,2) NOT NULL COMMENT '保教费标准',
    `meal_fee` DECIMAL(10,2) NOT NULL COMMENT '伙食费标准',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同年龄段收费明细表';

-- =====================================================
-- 13. 合同其他费用表 t_contract_other_fee
-- =====================================================
DROP TABLE IF EXISTS `t_contract_other_fee`;
CREATE TABLE `t_contract_other_fee` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `contract_id` BIGINT NOT NULL COMMENT '合同ID',
    `fee_code` VARCHAR(50) NOT NULL COMMENT '费用项目编码',
    `fee_name` VARCHAR(100) NOT NULL COMMENT '费用项目名称',
    `charge_cycle` VARCHAR(20) NOT NULL COMMENT '收取周期',
    `fee_standard` DECIMAL(10,2) NOT NULL COMMENT '计费标准',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同其他费用表';

-- =====================================================
-- 14. 合同变更记录表 t_contract_change_log
-- =====================================================
DROP TABLE IF EXISTS `t_contract_change_log`;
CREATE TABLE `t_contract_change_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `contract_id` BIGINT NOT NULL COMMENT '合同ID',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变更类型：CHANGE-变更, TERMINATE-终止, RENEW-续签',
    `change_reason` VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    `before_content` TEXT DEFAULT NULL COMMENT '变更前内容（JSON格式）',
    `after_content` TEXT DEFAULT NULL COMMENT '变更后内容（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同变更记录表';

-- =====================================================
-- 15. 出勤记录表 t_attendance
-- =====================================================
DROP TABLE IF EXISTS `t_attendance`;
CREATE TABLE `t_attendance` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `attendance_date` DATE NOT NULL COMMENT '日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常到园，2-请假，3-病假，4-缺勤',
    `register_user` BIGINT DEFAULT NULL COMMENT '登记人',
    `modify_user` BIGINT DEFAULT NULL COMMENT '修改人',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_child_date` (`child_id`, `attendance_date`),
    KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出勤记录表';

-- =====================================================
-- 15. 收费记录表 t_payment
-- =====================================================
DROP TABLE IF EXISTS `t_payment`;
CREATE TABLE `t_payment` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `receipt_no` VARCHAR(50) NOT NULL COMMENT '票据号',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `payment_type` VARCHAR(50) NOT NULL COMMENT '收费类型',
    `fee_item` VARCHAR(100) NOT NULL COMMENT '费用项目',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '收费方式：CASH-现金，WECHAT-微信，ALIPAY-支付宝，BANK-银行转账',
    `status` VARCHAR(20) NOT NULL DEFAULT 'VALID' COMMENT '状态：VALID-有效，VOIDED-已作废',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_receipt_no` (`receipt_no`),
    KEY `idx_child_id` (`child_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收费记录表';

-- =====================================================
-- 16. 退费记录表 t_refund
-- =====================================================
DROP TABLE IF EXISTS `t_refund`;
CREATE TABLE `t_refund` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `refund_no` VARCHAR(32) NOT NULL COMMENT '退费编号',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `account_type` VARCHAR(20) NOT NULL COMMENT '账户类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
    `fee_item` VARCHAR(100) NOT NULL COMMENT '费用项目',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '退费原因',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，COMPLETED-已完成，CANCELLED-已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_child_id` (`child_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退费记录表';

-- =====================================================
-- 17. 余额账户表 t_balance_account
-- =====================================================
DROP TABLE IF EXISTS `t_balance_account`;
CREATE TABLE `t_balance_account` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `account_type` VARCHAR(20) NOT NULL COMMENT '账户类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
    `balance` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '余额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_child_account` (`child_id`, `account_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额账户表';

-- =====================================================
-- 18. 余额变动记录表 t_balance_record
-- =====================================================
DROP TABLE IF EXISTS `t_balance_record`;
CREATE TABLE `t_balance_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `account_type` VARCHAR(20) NOT NULL COMMENT '账户类型',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型：INCOME-收入，EXPENSE-支出，REFUND-退费，ADJUST-调整',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '变动金额',
    `after_balance` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    `related_no` VARCHAR(50) DEFAULT NULL COMMENT '关联单据号',
    `fee_item` VARCHAR(100) DEFAULT NULL COMMENT '费用项目',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_child_id` (`child_id`),
    KEY `idx_related_no` (`related_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额变动记录表';

-- =====================================================
-- 19. 账单表 t_bill
-- =====================================================
DROP TABLE IF EXISTS `t_bill`;
CREATE TABLE `t_bill` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `bill_no` VARCHAR(50) NOT NULL COMMENT '账单编号',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `contract_id` BIGINT DEFAULT NULL COMMENT '合同ID',
    `bill_month` VARCHAR(7) NOT NULL COMMENT '账单月份，格式：YYYY-MM',
    `education_fee_receivable` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '保教费应收',
    `meal_fee_receivable` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '伙食费应收',
    `other_fee_receivable` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '其他费用应收',
    `education_fee_refund` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '应退保教费',
    `meal_fee_refund` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '应退伙食费',
    `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '减免金额',
    `actual_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实际应交',
    `due_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实际需缴',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认，CONFIRMED-已确认',
    `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
    `confirm_user` BIGINT DEFAULT NULL COMMENT '确认人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bill_no` (`bill_no`),
    UNIQUE KEY `uk_child_month` (`child_id`, `bill_month`),
    KEY `idx_bill_month` (`bill_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- =====================================================
-- 20. 账单明细表 t_bill_item
-- =====================================================
DROP TABLE IF EXISTS `t_bill_item`;
CREATE TABLE `t_bill_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `bill_id` BIGINT NOT NULL COMMENT '账单ID',
    `item_type` VARCHAR(20) NOT NULL COMMENT '费用类型：EDUCATION-保教费，MEAL-伙食费，OTHER-其他费用',
    `fee_item` VARCHAR(100) NOT NULL COMMENT '费用项目',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '说明',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单明细表';

-- =====================================================
-- 21. 预警记录表 t_alert
-- =====================================================
DROP TABLE IF EXISTS `t_alert`;
CREATE TABLE `t_alert` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `child_id` BIGINT NOT NULL COMMENT '幼儿ID',
    `alert_type` VARCHAR(50) NOT NULL COMMENT '预警类型：CONTRACT_EXPIRE-合同到期提醒，EDUCATION_FEE_EXPIRE-保教费到期提醒，MEAL_FEE_EXPIRE-伙食费到期提醒，OVERDUE-逾期未缴费提醒，EDUCATION_FEE_LOW-保教费余额不足提醒，MEAL_FEE_LOW-伙食费余额不足提醒，OTHER_FEE_LOW-其他费用余额不足提醒',
    `alert_content` VARCHAR(500) NOT NULL COMMENT '预警内容',
    `trigger_time` DATETIME NOT NULL COMMENT '触发时间',
    `handle_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING-待处理，HANDLED-已处理',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `handle_user` BIGINT DEFAULT NULL COMMENT '处理人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_child_id` (`child_id`),
    KEY `idx_alert_type` (`alert_type`),
    KEY `idx_handle_status` (`handle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警记录表';

-- =====================================================
-- 22. 操作日志表 t_operation_log
-- =====================================================
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operation_ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `operation_module` VARCHAR(100) DEFAULT NULL COMMENT '操作模块',
    `operation_content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    `operation_result` VARCHAR(20) DEFAULT NULL COMMENT '操作结果：SUCCESS-成功，FAIL-失败',
    `error_msg` VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_operation_module` (`operation_module`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 22. 系统配置表 t_system_config
-- =====================================================
DROP TABLE IF EXISTS `t_system_config`;
CREATE TABLE `t_system_config` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
    `config_desc` VARCHAR(200) DEFAULT NULL COMMENT '配置说明',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 插入预置系统配置数据
-- =====================================================
INSERT INTO `t_system_config` (`id`, `config_key`, `config_value`, `config_desc`, `status`) VALUES
(1, 'LEAVE_REFUND_THRESHOLD', '3', '事假起算天数，超过此天数才退保教费', 1),
(2, 'SICK_REFUND_THRESHOLD', '3', '病假起算天数，超过此天数才退保教费', 1);

-- =====================================================
-- 插入预置角色数据
-- =====================================================
INSERT INTO `t_role` (`id`, `role_code`, `role_name`, `status`, `sort`, `create_user`) VALUES
(1, 'PRINCIPAL', '园长', 1, 1, NULL),
(2, 'FINANCE', '财务', 1, 2, NULL),
(3, 'TEACHER', '老师', 1, 3, NULL);

-- =====================================================
-- 插入预置菜单数据
-- =====================================================
INSERT INTO `t_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `permission`, `icon`, `sort`, `status`, `create_user`) VALUES
(1, '系统管理', 0, 1, '/system', 'Layout', NULL, 'setting', 1, 1, NULL),
(2, '用户管理', 1, 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, NULL),
(3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, 1, NULL),
(4, '菜单管理', 1, 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3, 1, NULL),
(5, '用户查询', 2, 3, NULL, NULL, 'system:user:query', NULL, 1, 1, NULL),
(6, '用户新增', 2, 3, NULL, NULL, 'system:user:add', NULL, 2, 1, NULL),
(7, '用户修改', 2, 3, NULL, NULL, 'system:user:edit', NULL, 3, 1, NULL),
(8, '用户删除', 2, 3, NULL, NULL, 'system:user:delete', NULL, 4, 1, NULL),
(9, '角色查询', 3, 3, NULL, NULL, 'system:role:query', NULL, 1, 1, NULL),
(10, '角色新增', 3, 3, NULL, NULL, 'system:role:add', NULL, 2, 1, NULL),
(11, '角色修改', 3, 3, NULL, NULL, 'system:role:edit', NULL, 3, 1, NULL),
(12, '角色删除', 3, 3, NULL, NULL, 'system:role:delete', NULL, 4, 1, NULL),
(13, '托育管理', 0, 1, '/nursery', 'Layout', NULL, 'education', 2, 1, NULL),
(14, '班级管理', 13, 2, '/class', 'class/index', 'nursery:class:list', 'peoples', 1, 1, NULL),
(15, '幼儿管理', 13, 2, '/child', 'child/index', 'nursery:child:list', 'user', 2, 1, NULL),
(16, '出勤管理', 13, 2, '/attendance', 'attendance/index', 'nursery:attendance:list', 'date', 3, 1, NULL),
(17, '班级查询', 14, 3, NULL, NULL, 'nursery:class:query', NULL, 1, 1, NULL),
(18, '班级新增', 14, 3, NULL, NULL, 'nursery:class:add', NULL, 2, 1, NULL),
(19, '班级修改', 14, 3, NULL, NULL, 'nursery:class:edit', NULL, 3, 1, NULL),
(20, '班级删除', 14, 3, NULL, NULL, 'nursery:class:delete', NULL, 4, 1, NULL),
(21, '幼儿查询', 15, 3, NULL, NULL, 'nursery:child:query', NULL, 1, 1, NULL),
(22, '幼儿新增', 15, 3, NULL, NULL, 'nursery:child:add', NULL, 2, 1, NULL),
(23, '幼儿修改', 15, 3, NULL, NULL, 'nursery:child:edit', NULL, 3, 1, NULL),
(24, '幼儿删除', 15, 3, NULL, NULL, 'nursery:child:delete', NULL, 4, 1, NULL),
(25, '财务管理', 0, 1, '/finance', 'Layout', NULL, 'money', 3, 1, NULL),
(26, '收费标准', 25, 2, '/payment/item', 'payment/item', 'finance:template:list', 'skill', 1, 1, NULL),
(27, '合同管理', 25, 2, '/contract', 'contract/index', 'finance:contract:list', 'documentation', 2, 1, NULL),
(28, '收费管理', 25, 2, '/payment/list', 'payment/index', 'finance:payment:list', 'money', 3, 1, NULL),
(29, '退费管理', 25, 2, '/payment/refund', 'payment/refund', 'finance:refund:list', 'money', 4, 1, NULL),
(30, '账单管理', 25, 2, '/bill', 'bill/index', 'finance:bill:list', 'list', 5, 1, NULL),
(31, '余额管理', 25, 2, '/payment/balance', 'payment/balance', 'finance:balance:list', 'money', 6, 1, NULL),
(32, '预警中心', 0, 1, '/alert', 'Layout', NULL, 'message', 4, 1, NULL),
(33, '预警记录', 32, 2, '/alert', 'alert/index', 'alert:record:list', 'message', 1, 1, NULL),
(34, '日志管理', 1, 2, '/system/log', 'system/log/index', 'system:log:list', 'log', 5, 1, NULL);

-- =====================================================
-- 插入测试用户数据
-- 密码均为 123456，使用BCrypt加密
-- =====================================================
INSERT INTO `t_user` (`id`, `username`, `password`, `real_name`, `phone`, `status`, `create_user`) VALUES
(1, 'admin', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '张园长', '13800138001', 1, NULL),
(2, 'finance01', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '李财务', '13800138002', 1, NULL),
(3, 'finance02', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '王财务', '13800138003', 1, NULL),
(4, 'teacher01', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '陈老师', '13800138004', 1, NULL),
(5, 'teacher02', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '刘老师', '13800138005', 1, NULL),
(6, 'teacher03', '$2a$10$.7/qjJDs0Vg6wCTMiE/YI.7wkb2vYCHEMu0aqQw3yGVZQzc7YNhj6', '赵老师', '13800138006', 1, NULL);

-- =====================================================
-- 插入用户角色关联数据
-- =====================================================
INSERT INTO `t_user_role` (`id`, `user_id`, `role_id`, `create_user`) VALUES
(1, 1, 1, NULL),
(2, 2, 2, NULL),
(3, 3, 2, NULL),
(4, 4, 3, NULL),
(5, 5, 3, NULL),
(6, 6, 3, NULL);

-- =====================================================
-- 插入角色菜单关联数据（园长拥有所有权限）
-- =====================================================
INSERT INTO `t_role_menu` (`id`, `role_id`, `menu_id`, `create_user`) VALUES
(1, 1, 1, NULL),
(2, 1, 2, NULL),
(3, 1, 3, NULL),
(4, 1, 4, NULL),
(5, 1, 5, NULL),
(6, 1, 6, NULL),
(7, 1, 7, NULL),
(8, 1, 8, NULL),
(9, 1, 9, NULL),
(10, 1, 10, NULL),
(11, 1, 11, NULL),
(12, 1, 12, NULL),
(13, 1, 13, NULL),
(14, 1, 14, NULL),
(15, 1, 15, NULL),
(16, 1, 16, NULL),
(17, 1, 17, NULL),
(18, 1, 18, NULL),
(19, 1, 19, NULL),
(20, 1, 20, NULL),
(21, 1, 21, NULL),
(22, 1, 22, NULL),
(23, 1, 23, NULL),
(24, 1, 24, NULL),
(25, 1, 25, NULL),
(26, 1, 26, NULL),
(27, 1, 27, NULL),
(28, 1, 28, NULL),
(29, 1, 29, NULL),
(30, 1, 30, NULL),
(31, 1, 31, NULL),
(32, 1, 32, NULL),
(33, 1, 33, NULL),
(34, 1, 34, NULL);

-- 财务角色权限
INSERT INTO `t_role_menu` (`id`, `role_id`, `menu_id`, `create_user`) VALUES
(35, 2, 25, NULL),
(36, 2, 26, NULL),
(37, 2, 27, NULL),
(38, 2, 28, NULL),
(39, 2, 29, NULL),
(40, 2, 30, NULL),
(41, 2, 31, NULL),
(42, 2, 32, NULL),
(43, 2, 33, NULL);

-- 老师角色权限
INSERT INTO `t_role_menu` (`id`, `role_id`, `menu_id`, `create_user`) VALUES
(44, 3, 13, NULL),
(45, 3, 14, NULL),
(46, 3, 15, NULL),
(47, 3, 16, NULL),
(48, 3, 17, NULL),
(49, 3, 21, NULL);

-- =====================================================
-- 插入测试班级数据
-- =====================================================
INSERT INTO `t_class` (`id`, `class_name`, `teacher_id`, `remark`, `status`, `current_count`, `create_user`) VALUES
(1, '小班A班', 4, '适合1-2岁幼儿', 1, 4, NULL),
(2, '小班B班', 5, '适合1-2岁幼儿', 1, 3, NULL),
(3, '中班', 6, '适合2-3岁幼儿', 1, 3, NULL);

-- =====================================================
-- 插入测试幼儿数据
-- =====================================================
INSERT INTO `t_child` (`id`, `name`, `gender`, `birth_date`, `class_id`, `enroll_date`, `parent_name`, `contact_phone`, `wechat`, `emergency_contact`, `emergency_phone`, `pickup_person`, `allergy_history`, `status`, `create_user`) VALUES
(1, '张小明', 1, '2023-06-15', 1, '2024-01-15', '张三', '13900139001', 'zhangsan_wx', '李四', '13900139002', '张三、李四', '无', 1, NULL),
(2, '李小红', 2, '2023-08-20', 1, '2024-02-01', '李四', '13900139003', 'lisi_wx', '王五', '13900139004', '李四、王五', '海鲜过敏', 1, NULL),
(3, '王小刚', 1, '2023-04-10', 1, '2024-01-20', '王五', '13900139005', 'wangwu_wx', '赵六', '13900139006', '王五', '无', 1, NULL),
(4, '赵小芳', 2, '2023-03-25', 1, '2024-03-01', '赵六', '13900139007', 'zhaoliu_wx', '钱七', '13900139008', '赵六、钱七', '花生过敏', 1, NULL),
(5, '钱小宝', 1, '2023-05-12', 2, '2024-02-15', '钱七', '13900139009', 'qianqi_wx', '孙八', '13900139010', '钱七', '无', 1, NULL),
(6, '孙小美', 2, '2023-07-08', 2, '2024-02-20', '孙八', '13900139011', 'sunba_wx', '周九', '13900139012', '孙八、周九', '牛奶过敏', 1, NULL),
(7, '周小龙', 1, '2023-09-01', 2, '2024-03-05', '周九', '13900139013', 'zhoujiu_wx', '吴十', '13900139014', '周九', '无', 1, NULL),
(8, '吴小燕', 2, '2022-12-15', 3, '2024-01-10', '吴十', '13900139015', 'wushi_wx', '郑一', '13900139016', '吴十、郑一', '鸡蛋过敏', 1, NULL),
(9, '郑小强', 1, '2022-10-20', 3, '2024-01-25', '郑一', '13900139017', 'zhengyi_wx', '冯二', '13900139018', '郑一', '无', 1, NULL),
(10, '冯小丽', 2, '2022-11-30', 3, '2024-02-10', '冯二', '13900139019', 'fenger_wx', '陈三', '13900139020', '冯二、陈三', '无', 1, NULL);

-- =====================================================
-- 插入收费标准模板数据
-- =====================================================
INSERT INTO `t_fee_template` (`id`, `template_name`, `course_type`, `status`, `version`, `create_user`) VALUES
(1, '2024年春季收费标准', '全日制', 1, '1.0', NULL),
(2, '2024年春季收费标准', '半日制', 1, '1.0', NULL);

-- =====================================================
-- 插入收费标准模板明细数据
-- =====================================================
INSERT INTO `t_fee_template_item` (`id`, `template_id`, `age_range`, `education_fee`, `meal_fee`, `create_user`) VALUES
(1, 1, '4-12月', 4500.00, 800.00, NULL),
(2, 1, '13-18月', 4200.00, 800.00, NULL),
(3, 1, '19-24月', 3800.00, 800.00, NULL),
(4, 1, '25-36月', 3500.00, 800.00, NULL),
(5, 1, '37月以上', 3200.00, 800.00, NULL),
(6, 2, '4-12月', 2800.00, 500.00, NULL),
(7, 2, '13-18月', 2600.00, 500.00, NULL),
(8, 2, '19-24月', 2400.00, 500.00, NULL),
(9, 2, '25-36月', 2200.00, 500.00, NULL),
(10, 2, '37月以上', 2000.00, 500.00, NULL);

-- =====================================================
-- 插入其他费用模板数据
-- =====================================================
INSERT INTO `t_other_fee_template` (`id`, `template_id`, `fee_code`, `fee_name`, `charge_cycle`, `fee_standard`, `status`, `create_user`) VALUES
(1, 1, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(2, 1, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, NULL),
(3, 1, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, NULL),
(4, 2, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, NULL),
(5, 2, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, NULL);

-- =====================================================
-- 插入合同数据
-- =====================================================
INSERT INTO `t_contract` (`id`, `contract_no`, `child_id`, `contract_name`, `course_type`, `start_date`, `end_date`, `status`, `create_user`) VALUES
(1, 'HT20240001', 1, '张小明入园合同', '全日制', '2024-01-15', '2025-01-14', 1, NULL),
(2, 'HT20240002', 2, '李小红入园合同', '全日制', '2024-02-01', '2025-01-31', 1, NULL),
(3, 'HT20240003', 3, '王小刚入园合同', '全日制', '2024-01-20', '2025-01-19', 1, NULL),
(4, 'HT20240004', 4, '赵小芳入园合同', '全日制', '2024-03-01', '2025-02-28', 1, NULL),
(5, 'HT20240005', 5, '钱小宝入园合同', '半日制', '2024-02-15', '2025-02-14', 1, NULL),
(6, 'HT20240006', 6, '孙小美入园合同', '半日制', '2024-02-20', '2025-02-19', 1, NULL),
(7, 'HT20240007', 7, '周小龙入园合同', '半日制', '2024-03-05', '2025-03-04', 1, NULL),
(8, 'HT20240008', 8, '吴小燕入园合同', '全日制', '2024-01-10', '2025-01-09', 1, NULL),
(9, 'HT20240009', 9, '郑小强入园合同', '全日制', '2024-01-25', '2025-01-24', 1, NULL),
(10, 'HT20240010', 10, '冯小丽入园合同', '全日制', '2024-02-10', '2025-02-09', 1, NULL);

-- =====================================================
-- 插入合同年龄段收费明细数据
-- =====================================================
INSERT INTO `t_contract_fee_item` (`id`, `contract_id`, `age_range`, `education_fee`, `meal_fee`, `create_user`) VALUES
(1, 1, '13-18月', 4200.00, 800.00, NULL),
(2, 1, '19-24月', 3800.00, 800.00, NULL),
(3, 2, '13-18月', 4200.00, 800.00, NULL),
(4, 2, '19-24月', 3800.00, 800.00, NULL),
(5, 3, '13-18月', 4200.00, 800.00, NULL),
(6, 3, '19-24月', 3800.00, 800.00, NULL),
(7, 4, '13-18月', 4200.00, 800.00, NULL),
(8, 5, '13-18月', 2600.00, 500.00, NULL),
(9, 5, '19-24月', 2400.00, 500.00, NULL),
(10, 6, '13-18月', 2600.00, 500.00, NULL),
(11, 7, '4-12月', 2800.00, 500.00, NULL),
(12, 7, '13-18月', 2600.00, 500.00, NULL),
(13, 8, '25-36月', 3500.00, 800.00, NULL),
(14, 9, '25-36月', 3500.00, 800.00, NULL),
(15, 10, '25-36月', 3500.00, 800.00, NULL);

-- =====================================================
-- 插入合同其他费用数据
-- =====================================================
INSERT INTO `t_contract_other_fee` (`id`, `contract_id`, `fee_code`, `fee_name`, `charge_cycle`, `fee_standard`, `status`, `create_user`) VALUES
(1, 1, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(2, 1, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, NULL),
(3, 1, 'UNIFORM', '园服费', 'ONCE', 200.00, 1, NULL),
(4, 2, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(5, 2, 'INSURANCE', '保险费', 'ONCE', 100.00, 1, NULL),
(6, 3, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(7, 4, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(8, 5, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, NULL),
(9, 6, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, NULL),
(10, 7, 'MATERIAL', '材料费', 'SEMESTER', 300.00, 1, NULL),
(11, 8, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(12, 9, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL),
(13, 10, 'MATERIAL', '材料费', 'SEMESTER', 500.00, 1, NULL);

-- =====================================================
-- 插入出勤记录数据（2024年3月部分数据）
-- =====================================================
INSERT INTO `t_attendance` (`id`, `child_id`, `attendance_date`, `status`, `register_user`, `modify_user`, `remark`, `create_user`) VALUES
(1, 1, '2024-03-01', 1, 4, NULL, NULL, NULL),
(2, 1, '2024-03-04', 1, 4, NULL, NULL, NULL),
(3, 1, '2024-03-05', 1, 4, NULL, NULL, NULL),
(4, 1, '2024-03-06', 2, 4, NULL, '家中有事', NULL),
(5, 1, '2024-03-07', 1, 4, NULL, NULL, NULL),
(6, 2, '2024-03-01', 1, 4, NULL, NULL, NULL),
(7, 2, '2024-03-04', 1, 4, NULL, NULL, NULL),
(8, 2, '2024-03-05', 3, 4, NULL, '感冒发烧', NULL),
(9, 2, '2024-03-06', 3, 4, NULL, '感冒发烧', NULL),
(10, 2, '2024-03-07', 1, 4, NULL, NULL, NULL),
(11, 3, '2024-03-01', 1, 4, NULL, NULL, NULL),
(12, 3, '2024-03-04', 1, 4, NULL, NULL, NULL),
(13, 3, '2024-03-05', 1, 4, NULL, NULL, NULL),
(14, 3, '2024-03-06', 1, 4, NULL, NULL, NULL),
(15, 3, '2024-03-07', 1, 4, NULL, NULL, NULL),
(16, 8, '2024-03-01', 1, 6, NULL, NULL, NULL),
(17, 8, '2024-03-04', 1, 6, NULL, NULL, NULL),
(18, 8, '2024-03-05', 1, 6, NULL, NULL, NULL),
(19, 8, '2024-03-06', 1, 6, NULL, NULL, NULL),
(20, 8, '2024-03-07', 1, 6, NULL, NULL, NULL),
(21, 9, '2024-03-01', 1, 6, NULL, NULL, NULL),
(22, 9, '2024-03-04', 1, 6, NULL, NULL, NULL),
(23, 9, '2024-03-05', 1, 6, NULL, NULL, NULL),
(24, 9, '2024-03-06', 1, 6, NULL, NULL, NULL),
(25, 9, '2024-03-07', 1, 6, NULL, NULL, NULL);

-- =====================================================
-- 插入余额账户数据
-- =====================================================
INSERT INTO `t_balance_account` (`id`, `child_id`, `account_type`, `balance`, `create_user`) VALUES
(1, 1, 'EDUCATION', 4200.00, NULL),
(2, 1, 'MEAL', 800.00, NULL),
(3, 2, 'EDUCATION', 4200.00, NULL),
(4, 2, 'MEAL', 800.00, NULL),
(5, 3, 'EDUCATION', 4200.00, NULL),
(6, 3, 'MEAL', 800.00, NULL),
(7, 4, 'EDUCATION', 4200.00, NULL),
(8, 4, 'MEAL', 800.00, NULL),
(9, 5, 'EDUCATION', 2600.00, NULL),
(10, 5, 'MEAL', 500.00, NULL),
(11, 6, 'EDUCATION', 2600.00, NULL),
(12, 6, 'MEAL', 500.00, NULL),
(13, 7, 'EDUCATION', 2800.00, NULL),
(14, 7, 'MEAL', 500.00, NULL),
(15, 8, 'EDUCATION', 3500.00, NULL),
(16, 8, 'MEAL', 800.00, NULL),
(17, 9, 'EDUCATION', 3500.00, NULL),
(18, 9, 'MEAL', 800.00, NULL),
(19, 10, 'EDUCATION', 3500.00, NULL),
(20, 10, 'MEAL', 800.00, NULL);

-- =====================================================
-- 插入收费记录数据
-- =====================================================
INSERT INTO `t_payment` (`id`, `receipt_no`, `child_id`, `payment_type`, `fee_item`, `amount`, `payment_method`, `status`, `remark`, `create_user`) VALUES
(1, 'SK202403001', 1, 'EDUCATION', '保教费', 4200.00, 'WECHAT', 'VALID', '3月份保教费', 2),
(2, 'SK202403002', 1, 'MEAL', '伙食费', 800.00, 'WECHAT', 'VALID', '3月份伙食费', 2),
(3, 'SK202403003', 1, 'OTHER', '材料费', 500.00, 'CASH', 'VALID', '春季学期材料费', 2),
(4, 'SK202403004', 2, 'EDUCATION', '保教费', 4200.00, 'ALIPAY', 'VALID', '3月份保教费', 2),
(5, 'SK202403005', 2, 'MEAL', '伙食费', 800.00, 'ALIPAY', 'VALID', '3月份伙食费', 2),
(6, 'SK202403006', 3, 'EDUCATION', '保教费', 4200.00, 'WECHAT', 'VALID', '3月份保教费', 3),
(7, 'SK202403007', 3, 'MEAL', '伙食费', 800.00, 'WECHAT', 'VALID', '3月份伙食费', 3),
(8, 'SK202403008', 8, 'EDUCATION', '保教费', 3500.00, 'BANK', 'VALID', '3月份保教费', 2),
(9, 'SK202403009', 8, 'MEAL', '伙食费', 800.00, 'BANK', 'VALID', '3月份伙食费', 2);

-- =====================================================
-- 插入余额变动记录数据
-- =====================================================
INSERT INTO `t_balance_record` (`id`, `child_id`, `account_type`, `change_type`, `change_amount`, `after_balance`, `related_no`, `fee_item`, `remark`, `create_user`) VALUES
(1, 1, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403001', '保教费', '3月份保教费充值', 2),
(2, 1, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403002', '伙食费', '3月份伙食费充值', 2),
(3, 2, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403004', '保教费', '3月份保教费充值', 2),
(4, 2, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403005', '伙食费', '3月份伙食费充值', 2),
(5, 3, 'EDUCATION', 'INCOME', 4200.00, 4200.00, 'SK202403006', '保教费', '3月份保教费充值', 3),
(6, 3, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403007', '伙食费', '3月份伙食费充值', 3),
(7, 8, 'EDUCATION', 'INCOME', 3500.00, 3500.00, 'SK202403008', '保教费', '3月份保教费充值', 2),
(8, 8, 'MEAL', 'INCOME', 800.00, 800.00, 'SK202403009', '伙食费', '3月份伙食费充值', 2);

-- =====================================================
-- 插入账单数据
-- =====================================================
INSERT INTO `t_bill` (`id`, `bill_no`, `child_id`, `bill_month`, `education_receivable`, `meal_receivable`, `other_receivable`, `education_refundable`, `meal_refundable`, `deduction_amount`, `actual_receivable`, `actual_payable`, `status`, `confirm_time`, `confirm_user`, `create_user`) VALUES
(1, 'ZD202403001', 1, '2024-03', 4200.00, 800.00, 500.00, 0.00, 0.00, 0.00, 5500.00, 5500.00, 2, '2024-03-01 10:00:00', 2, NULL),
(2, 'ZD202403002', 2, '2024-03', 4200.00, 800.00, 500.00, 0.00, 0.00, 0.00, 5500.00, 5500.00, 2, '2024-03-01 10:00:00', 2, NULL),
(3, 'ZD202403003', 3, '2024-03', 4200.00, 800.00, 500.00, 0.00, 0.00, 0.00, 5500.00, 5500.00, 2, '2024-03-01 10:00:00', 2, NULL),
(4, 'ZD202403004', 4, '2024-03', 4200.00, 800.00, 500.00, 0.00, 0.00, 0.00, 5500.00, 5500.00, 1, NULL, NULL, NULL),
(5, 'ZD202403005', 5, '2024-03', 2600.00, 500.00, 300.00, 0.00, 0.00, 0.00, 3400.00, 3400.00, 1, NULL, NULL, NULL),
(6, 'ZD202403006', 6, '2024-03', 2600.00, 500.00, 300.00, 0.00, 0.00, 0.00, 3400.00, 3400.00, 1, NULL, NULL, NULL),
(7, 'ZD202403007', 7, '2024-03', 2800.00, 500.00, 300.00, 0.00, 0.00, 0.00, 3600.00, 3600.00, 1, NULL, NULL, NULL),
(8, 'ZD202403008', 8, '2024-03', 3500.00, 800.00, 500.00, 0.00, 0.00, 0.00, 4800.00, 4800.00, 2, '2024-03-01 10:00:00', 2, NULL),
(9, 'ZD202403009', 9, '2024-03', 3500.00, 800.00, 500.00, 0.00, 0.00, 0.00, 4800.00, 4800.00, 1, NULL, NULL, NULL),
(10, 'ZD202403010', 10, '2024-03', 3500.00, 800.00, 500.00, 0.00, 0.00, 0.00, 4800.00, 4800.00, 1, NULL, NULL, NULL);

-- =====================================================
-- 插入账单明细数据
-- =====================================================
INSERT INTO `t_bill_item` (`id`, `bill_id`, `fee_type`, `fee_item`, `amount`, `description`, `create_user`) VALUES
(1, 1, 'EDUCATION', '保教费', 4200.00, '3月份保教费', NULL),
(2, 1, 'MEAL', '伙食费', 800.00, '3月份伙食费', NULL),
(3, 1, 'OTHER', '材料费', 500.00, '春季学期材料费', NULL),
(4, 2, 'EDUCATION', '保教费', 4200.00, '3月份保教费', NULL),
(5, 2, 'MEAL', '伙食费', 800.00, '3月份伙食费', NULL),
(6, 2, 'OTHER', '材料费', 500.00, '春季学期材料费', NULL),
(7, 3, 'EDUCATION', '保教费', 4200.00, '3月份保教费', NULL),
(8, 3, 'MEAL', '伙食费', 800.00, '3月份伙食费', NULL),
(9, 3, 'OTHER', '材料费', 500.00, '春季学期材料费', NULL),
(10, 8, 'EDUCATION', '保教费', 3500.00, '3月份保教费', NULL),
(11, 8, 'MEAL', '伙食费', 800.00, '3月份伙食费', NULL),
(12, 8, 'OTHER', '材料费', 500.00, '春季学期材料费', NULL);

-- =====================================================
-- 插入预警记录数据
-- =====================================================
INSERT INTO `t_alert` (`id`, `child_id`, `alert_type`, `alert_content`, `trigger_time`, `handle_status`, `handle_time`, `handle_user`, `create_user`) VALUES
(1, 4, 'EDUCATION_FEE_LOW', '赵小芳保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(2, 5, 'EDUCATION_FEE_LOW', '钱小宝保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(3, 6, 'EDUCATION_FEE_LOW', '孙小美保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(4, 7, 'EDUCATION_FEE_LOW', '周小龙保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(5, 9, 'EDUCATION_FEE_LOW', '郑小强保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(6, 10, 'EDUCATION_FEE_LOW', '冯小丽保教费余额不足', '2024-03-01 00:00:00', 'PENDING', NULL, NULL, NULL),
(7, 4, 'EDUCATION_FEE_LOW', '幼儿赵小芳保教费余额不足，请及时充值', '2024-03-01 09:00:00', 'PENDING', NULL, NULL, NULL),
(8, 5, 'MEAL_FEE_LOW', '幼儿钱小宝伙食费余额不足，请及时充值', '2024-03-01 09:00:00', 'HANDLED', '2024-03-02 10:00:00', 2, NULL),
(9, 6, 'CONTRACT_EXPIRE', '幼儿孙小美合同将于30天后到期，请及时续签', '2024-03-01 09:00:00', 'PENDING', NULL, NULL, NULL);

-- =====================================================
-- 插入操作日志数据
-- =====================================================
INSERT INTO `t_operation_log` (`id`, `operator`, `operator_id`, `operation_ip`, `operation_module`, `operation_content`, `operation_result`, `error_msg`, `create_user`) VALUES
(1, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：finance01', 'SUCCESS', NULL, NULL),
(2, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：finance02', 'SUCCESS', NULL, NULL),
(3, 'admin', 1, '127.0.0.1', '用户管理', '新增用户：teacher01', 'SUCCESS', NULL, NULL),
(4, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：小班A班', 'SUCCESS', NULL, NULL),
(5, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：小班B班', 'SUCCESS', NULL, NULL),
(6, 'admin', 1, '127.0.0.1', '班级管理', '新增班级：中班', 'SUCCESS', NULL, NULL),
(7, 'finance01', 2, '127.0.0.1', '收费管理', '收取张小明保教费4200元', 'SUCCESS', NULL, NULL),
(8, 'finance01', 2, '127.0.0.1', '收费管理', '收取张小明伙食费800元', 'SUCCESS', NULL, NULL),
(9, 'teacher01', 4, '127.0.0.1', '出勤管理', '登记2024-03-01出勤记录', 'SUCCESS', NULL, NULL),
(10, 'teacher01', 4, '127.0.0.1', '出勤管理', '登记2024-03-04出勤记录', 'SUCCESS', NULL, NULL);

-- =====================================================
-- 创建额外索引
-- =====================================================
CREATE INDEX `idx_child_status` ON `t_child` (`status`, `deleted`);
CREATE INDEX `idx_contract_status` ON `t_contract` (`status`, `deleted`);
CREATE INDEX `idx_payment_status` ON `t_payment` (`status`, `deleted`);
CREATE INDEX `idx_bill_status` ON `t_bill` (`status`, `deleted`);
CREATE INDEX `idx_alert_handle_status` ON `t_alert` (`handle_status`, `deleted`);
CREATE INDEX `idx_user_status` ON `t_user` (`status`, `deleted`);
CREATE INDEX `idx_role_status` ON `t_role` (`status`, `deleted`);
CREATE INDEX `idx_menu_status` ON `t_menu` (`status`, `deleted`);

-- =====================================================
-- 初始化完成
-- =====================================================
