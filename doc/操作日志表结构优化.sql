-- 优化操作日志表结构
-- 如果表已存在，先删除旧表（注意：这会删除现有数据，生产环境请使用ALTER语句）
-- DROP TABLE IF EXISTS t_operation_log;

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    trace_id VARCHAR(64) COMMENT '追踪ID',
    operator VARCHAR(50) COMMENT '操作人姓名',
    operator_id BIGINT COMMENT '操作人ID',
    operation_ip VARCHAR(128) COMMENT '操作IP地址',
    operation_location VARCHAR(100) COMMENT '操作地点',
    operation_module VARCHAR(100) COMMENT '操作模块',
    operation_type VARCHAR(20) COMMENT '操作类型(INSERT/UPDATE/DELETE/SELECT等)',
    operation_name VARCHAR(100) COMMENT '操作名称',
    request_method VARCHAR(10) COMMENT '请求方法(GET/POST/PUT/DELETE)',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '返回结果',
    operation_status VARCHAR(20) COMMENT '操作状态(成功/失败)',
    error_msg TEXT COMMENT '错误信息',
    exception_class VARCHAR(255) COMMENT '异常类名',
    exception_stack TEXT COMMENT '异常堆栈',
    cost_time BIGINT COMMENT '耗时(毫秒)',
    browser VARCHAR(100) COMMENT '浏览器类型',
    os VARCHAR(100) COMMENT '操作系统',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_operator (operator),
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_module (operation_module),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_status (operation_status),
    INDEX idx_create_time (create_time),
    INDEX idx_trace_id (trace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 如果表已存在，使用以下ALTER语句添加新字段
-- ALTER TABLE t_operation_log ADD COLUMN trace_id VARCHAR(64) COMMENT '追踪ID' AFTER id;
-- ALTER TABLE t_operation_log ADD COLUMN operation_location VARCHAR(100) COMMENT '操作地点' AFTER operation_ip;
-- ALTER TABLE t_operation_log ADD COLUMN operation_type VARCHAR(20) COMMENT '操作类型' AFTER operation_module;
-- ALTER TABLE t_operation_log ADD COLUMN operation_name VARCHAR(100) COMMENT '操作名称' AFTER operation_type;
-- ALTER TABLE t_operation_log ADD COLUMN request_method VARCHAR(10) COMMENT '请求方法' AFTER operation_name;
-- ALTER TABLE t_operation_log ADD COLUMN request_url VARCHAR(500) COMMENT '请求URL' AFTER request_method;
-- ALTER TABLE t_operation_log ADD COLUMN request_params TEXT COMMENT '请求参数' AFTER request_url;
-- ALTER TABLE t_operation_log ADD COLUMN response_result TEXT COMMENT '返回结果' AFTER request_params;
-- ALTER TABLE t_operation_log ADD COLUMN exception_class VARCHAR(255) COMMENT '异常类名' AFTER error_msg;
-- ALTER TABLE t_operation_log ADD COLUMN exception_stack TEXT COMMENT '异常堆栈' AFTER exception_class;
-- ALTER TABLE t_operation_log ADD COLUMN cost_time BIGINT COMMENT '耗时(毫秒)' AFTER exception_stack;
-- ALTER TABLE t_operation_log ADD COLUMN browser VARCHAR(100) COMMENT '浏览器类型' AFTER cost_time;
-- ALTER TABLE t_operation_log ADD COLUMN os VARCHAR(100) COMMENT '操作系统' AFTER browser;
-- ALTER TABLE t_operation_log MODIFY COLUMN operation_result VARCHAR(20) COMMENT '操作状态(成功/失败)';
-- ALTER TABLE t_operation_log CHANGE COLUMN operation_result operation_status VARCHAR(20) COMMENT '操作状态(成功/失败)';
-- ALTER TABLE t_operation_log DROP COLUMN create_user;
-- ALTER TABLE t_operation_log DROP COLUMN update_time;
-- ALTER TABLE t_operation_log DROP COLUMN update_user;
-- ALTER TABLE t_operation_log DROP COLUMN deleted;
