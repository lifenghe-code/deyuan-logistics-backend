CREATE DATABASE IF NOT EXISTS deyuan_logistics 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE deyuan_logistics;

DROP TABLE IF EXISTS `admin`;
CREATE TABLE admin (
    admin_id BIGINT  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    admin_name VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    account VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(50) NOT NULL COMMENT '加密后的密码',
    email VARCHAR(50) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-启用 1-禁用 2-未激活',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
);

DROP TABLE IF EXISTS `dept`;
CREATE TABLE dept(
  dept_id BIGINT  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
  dept_name varchar(255) DEFAULT NULL COMMENT '部门名称',
  dept_description varchar(255) DEFAULT NULL COMMENT '部门描述',
  leader_id bigint DEFAULT NULL COMMENT '负责人ID',
  leader_name VARCHAR(50) NOT NULL COMMENT '负责人姓名',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0-启用 1-禁用 2-未激活',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
);

DROP TABLE IF EXISTS `staff`;
CREATE TABLE staff(
    staff_id BIGINT  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    staff_name VARCHAR(50) NOT NULL COMMENT '登录用户名',
    staff_type tinyint NOT NULL COMMENT '员工类型',
    dept_id bigint NOT NULL COMMENT '部门id',
    plate_number VARCHAR(36) NOT NULL COMMENT '车牌号',
    email VARCHAR(100) UNIQUE COMMENT '电子邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号码',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    gender TINYINT COMMENT '0-未知 1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-启用 1-禁用 2-未激活',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    INDEX idx_staff_name (staff_name),
    INDEX idx_phone (phone)
);


DROP TABLE IF EXISTS `behavior`;
CREATE TABLE behavior (
    behavior_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    behavior_code TINYINT(4) NOT NULL UNIQUE COMMENT '行为编码',
    severity_level TINYINT NOT NULL COMMENT '严重等级(1-5)',
    description TEXT COMMENT '行为详细描述',
    detection_method TINYINT DEFAULT 0 COMMENT '检测方法 0-机器检测 1-人工检测',
    is_active TINYINT DEFAULT 0 COMMENT '0-启用检测，1-未启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
);

DROP TABLE IF EXISTS `record`;
CREATE TABLE record (
        record_id BIGINT  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
        driver_id BIGINT NOT NULL COMMENT '司机ID',
        copilot_id BIGINT NOT NULL COMMENT '押运员ID',
        driver_name VARCHAR(50) NOT NULL COMMENT '驾驶员姓名',
        copilot_name VARCHAR(50) NOT NULL COMMENT '押运员员姓名',
        plate_number VARCHAR(36) NOT NULL COMMENT '车牌号',
        behavior_id BIGINT  NULL COMMENT '关联的违规行为类型ID',
        behavior_code VARCHAR(50) NOT NULL COMMENT '行为编码',
        occurrence_time DATETIME NOT NULL  COMMENT '违规发生时间',
        discovery_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发现时间',
        discovery_method tinyint COMMENT '发现方式(系统检测/人工举报等)',
        description VARCHAR(50) NULL COMMENT '违规行为详细描述',
        evidence VARCHAR(255) COMMENT '证据材料(图片/视频/日志等)',
        severity_level TINYINT NULL COMMENT '严重等级(1-5)',
        status TINYINT NOT NULL DEFAULT 0 COMMENT '0-处理中 1-成功 2-失败',
        reporter_id BIGINT DEFAULT NULL COMMENT '报告人ID',
        process_comment VARCHAR(50) COMMENT '处理意见',
        process_by BIGINT COMMENT '处理人ID',
        process_time DATETIME COMMENT '处理时间',
        create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
        del_flag TINYINT  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
);


DROP TABLE IF EXISTS `notice`;
CREATE TABLE notice (
    notice_id bigint PRIMARY KEY COMMENT '通知唯一标识',
    staff_id VARCHAR(36) NOT NULL COMMENT '员工ID',
    copilot_id BIGINT NOT NULL COMMENT '押运员ID',
    plate_number VARCHAR(36) NOT NULL COMMENT '车牌号',
    behavior_code TINYINT NOT NULL UNIQUE COMMENT '行为编码',
    record_id BIGINT NOT NULL COMMENT '关联的违规记录ID',
    notice_type TINYINT NOT NULL COMMENT '通知类型0-短信 1-电话',
    title VARCHAR(20) NOT NULL COMMENT '通知标题',
    content VARCHAR(50) NOT NULL COMMENT '通知内容',
    sender_id BIGINT NOT NULL COMMENT '发送人/部门ID',
    sender_type ENUM('HR', 'SYSTEM', 'MANAGER', 'AUDIT') NOT NULL COMMENT '发送方类型',
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-处理中 1-通知成功 2-通知失败',
    del_flag TINYINT  NULL DEFAULT 0 COMMENT '删除标志（0代表存在， 1代表删除）',
    INDEX idx_staff_id (staff_id)
);


DROP TABLE IF EXISTS `appeal`;
CREATE TABLE appeal (
    appeal_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    record_id BIGINT NOT NULL COMMENT '关联的违规记录ID',
    staff_id  BIGINT NOT NULL COMMENT '申诉员工ID',
    staff_name VARCHAR(50) NOT NULL COMMENT '申诉人员姓名',
    plate_number VARCHAR(36) NOT NULL COMMENT '车牌号',
    appeal_type TINYINT  NOT NULL DEFAULT 0 COMMENT '申诉类型',
    appeal_reason VARCHAR(36) NOT NULL COMMENT '申诉理由',
    appeal_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申诉时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-申诉中 1-申诉成功 2-申诉拒绝 3-申诉撤回',
    evidence VARCHAR(255) COMMENT '申诉证据(图片/文件/视频等)',
    processor_id VARCHAR(36) COMMENT '处理人ID',
    process_comment VARCHAR(255) COMMENT '处理意见',
    process_time DATETIME COMMENT '处理时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    del_flag TINYINT  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    INDEX idx_staff_id (staff_id)
);