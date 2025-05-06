Minio启动命令

```cmd
 .\minio.exe server D:\MinIO\Data --console-address ":9000" --address ":9090"
```

Kafka启动命令（首先启动zookeeper）

```cmd
.\windows\kafka-server-start.bat ..\config\server.properties
```

# 德源物流—司机危险行为监控系统

**分布式系统**：数据发送平台、行为分析平台、行为监控平台（算力主要集中在行为分析平台）。

**数据发送平台**—责收集图像数据、传输图像数据、任务调度。

**行为分析平台**—负责对传送过来的图像数据进行分析，返回结果。

**行为监控平台**—整体的后台，对结果进行呈现与分析。

## 数据传输平台

- 搭建基于 Netty 的高性能图像数据长连接通道；
- 引入 Kafka 实现数据流量削峰和异步处理，提高系统吞吐能力；
- 实现双通道数据发送机制（Netty 直发与 Kafka 缓冲），并结合 QPS 动态阈值自动切换；
- 设计故障转移机制，包括网络分区、自恢复、本地磁盘暂存、服务降级；

## **行为监控平台**

- 使用 Spring Boot +MySql+ MyBatis-Plus 搭建后台管理系统，实现违规行为统计、通知推送与申诉管理；
- 基于 Minio 实现对象存储
- 分析司机违规情况，构建用户画像

## **行为分析平台**

- 对发送过来的数据进行分析处理，判断有无违规行为；

## 核心组件说明

### 1. QPS检测模块

- **功能**：实时监测输入请求速率
- **策略**：
  - 令牌桶算法控制实时流量
  - 动态阈值调整算法
  - 异常流量检测

### 2. 路由决策引擎

- **路由规则**：
  - 当QPS < 阈值：直接Netty发送
  - 当QPS ≥ 阈值：写入Kafka队列
  - hysteresis滞回控制防止频繁切换

### 3. 双通道发送系统

- **直接通道**：
  - 纯Netty实现
  - 低延迟路径
- **缓冲通道**：
  - Kafka持久化存储
  - 流量削峰谷

### 4. 消费者服务

- **特性**：
  - 从 Kafka 按需拉取
  - 自适应消费速率控制
  - 批量消息打包发送

## 关键设计决策

1. **动态阈值计算**：
   - 基于历史流量模式自动调整
   - 考虑系统当前负载状态
   - 人工可覆盖配置
2. **状态同步机制**：
   - 路由状态集中管理
   - 各节点状态共识协议
   - 故障时自动降级策略
3. **数据一致性保障**：
   - 直接通道失败转Kafka
   - 消息顺序性保证
   - 精确一次语义实现

## 异常处理设计

1. **Netty通道阻塞**：
   - 实时监控发送队列深度
   - 自动触发切换到Kafka路径
2. **Kafka服务不可用**：
   - 本地磁盘缓冲写入
   - 服务恢复后重放
3. **网络分区场景**：
   - 客户端本地缓存
   - 指数退避重试

## 监控指标建议

1. **路由决策指标**：
   - 直接/缓冲路径比例
   - 切换频率统计
   - 决策延迟分布
2. **性能指标**：
   - 端到端延迟对比
   - 系统吞吐量变化
   - 资源利用率
3. **业务指标**：
   - 消息成功率
   - 异常流量模式
   - 消费者延迟

## 技术栈

### 后端

- SpringBoot
- MySql、MyBatis-plus
- Redis
- Netty 实现高性能通信
- OSS 对象存储
- 消息队列 Kafka 实现流量削峰
- 分布式任务调度平台 xxl-job
- Sa-Token 权限控制
- Druid 数据库连接池和并发编程
- JUC 并发和异步编程

#### Netty 

在 Netty 中实现真正的生产级长连接需要：

1. **保持 Channel 不主动关闭**
2. **完善的心跳机制**
3. **连接状态管理**
4. **断线自动恢复**
5. **严格的资源管理**

#### Kafka

- **进行流量削峰，减轻服务器压力**
- **重复消费、漏消费问题、对业务影响甚微**



## 数据库

> [!NOTE]
>
> 具体以 sql 文件为准

- admin表
- dept表
- staff表
- illegal_behavior表
- user_illegal表
- illegal_notice表
- appeal表

```sql
CREATE DATABASE IF NOT EXISTS deyuan_logistics 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE deyuan_logistics;
```


```sql
CREATE TABLE admin (
    admin_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    admin_name VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
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
```

```sql
CREATE TABLE dept(
  dept_id bigint  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
  dept_name varchar(255) DEFAULT NULL COMMENT '部门名称',
  dept_description varchar(255) DEFAULT NULL COMMENT '部门描述',
  leader_id bigint DEFAULT NULL COMMENT '负责人ID',
  leader_name VARCHAR(50) NOT NULL COMMENT '负责人姓名',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0-启用 1-禁用 2-未激活',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
);
```

```sql
CREATE TABLE staff(
    staff_id BIGINT  PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    staff_name VARCHAR(50) NOT NULL COMMENT '登录用户名',
    staff_type tinyint NOT NULL COMMENT '员工类型',
    dept_id bigint NOT NULL COMMENT '部门id',
    plate_number VARCHAR(36) NOT NULL COMMENT '车牌号',
    email VARCHAR(100) UNIQUE COMMENT '电子邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号码',
    grade TINYINT NOT NULL DEFAULT 5 COMMENT '员工等级',
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
```

```sql
CREATE TABLE behavior (
    behavior_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Id',
    behavior_code TINYINT(4) NOT NULL UNIQUE COMMENT '行为编码',
    severity_level TINYINT NOT NULL COMMENT '严重等级(1-5)',
    description TEXT COMMENT '行为详细描述',
    detection_method TINYINT DEFAULT 0 COMMENT '检测方法 0-机器检测 1-人工检测',
    is_active TINYINT DEFAULT 0 COMMENT '0-启用检测，1-未启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag tinyint  NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
);
```

```sql
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
```

```sql
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
```

```sql
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
```





