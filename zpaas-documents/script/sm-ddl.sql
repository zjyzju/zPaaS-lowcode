-- sm.sys_area definition

CREATE TABLE `sys_area` (
  `id` varchar(32) NOT NULL COMMENT '区域标识',
  `area_name` varchar(90) NOT NULL COMMENT '区域名称',
  `parent_area_id` varchar(32) DEFAULT NULL COMMENT '上级区域标识',
  `area_level` varchar(1) NOT NULL COMMENT '区域级别',
  `area_code` varchar(32) NOT NULL COMMENT '区域编码',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='区域表;';


-- sm.sys_data_right definition

CREATE TABLE `sys_data_right` (
  `id` varchar(32) NOT NULL COMMENT '数据权限标识',
  `data_right_code` varchar(32) NOT NULL COMMENT '数据权限编码',
  `data_right_name` varchar(90) NOT NULL COMMENT '数据权限名称',
  `right_ctl_type` varchar(1) DEFAULT NULL COMMENT '权限控制类型',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_data_right_unique` (`data_right_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限;';


-- sm.sys_data_right_value definition

CREATE TABLE `sys_data_right_value` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `data_right_id` varchar(32) NOT NULL COMMENT '数据权限标识',
  `data_right_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据权限值',
  `description` varchar(255) DEFAULT NULL COMMENT '数据权限值描述',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限值;';


-- sm.sys_dict definition

CREATE TABLE `sys_dict` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `dict_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典编码',
  `dict_name` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '字典名称',
  `parent_dict_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '上级字典编码',
  `parent_dict_value` varchar(900) DEFAULT NULL COMMENT '上级参数值',
  `dict_value` varchar(900) NOT NULL COMMENT '参数值',
  `dict_value_label` varchar(900) DEFAULT NULL COMMENT '参数值名称',
  `dict_value2` varchar(900) DEFAULT NULL COMMENT '字典值2',
  `dict_value3` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '字典值3',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公共参数配置表;';


-- sm.sys_login_log definition

CREATE TABLE `sys_login_log` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '登录用户',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `logout_time` datetime DEFAULT NULL COMMENT '登出时间',
  `login_result` varchar(1) DEFAULT NULL COMMENT '登录结果',
  `ip` varchar(64) DEFAULT NULL COMMENT '客户端ip',
  `terminal_type` varchar(10) DEFAULT NULL COMMENT '终端类型',
  `terminal_id` varchar(64) DEFAULT NULL COMMENT '终端唯一码',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  PRIMARY KEY (`id`),
  KEY `sys_login_log_user_id_IDX` (`user_id`) USING BTREE,
  KEY `sys_login_log_user_id_time_IDX` (`user_id`,`login_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志;';


-- sm.sys_operation_log definition

CREATE TABLE `sys_operation_log` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `content` varchar(2000) DEFAULT NULL COMMENT '日志内容',
  `result` varchar(90) DEFAULT NULL COMMENT '操作结果',
  `type` varchar(10) DEFAULT NULL COMMENT '日志类型',
  `user_id` varchar(32) NOT NULL COMMENT '操作账号',
  `ip` varchar(64) DEFAULT NULL COMMENT '客户端IP',
  `http_method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `url` varchar(255) DEFAULT NULL COMMENT '请求地址',
  `param` varchar(900) DEFAULT NULL COMMENT '请求参数',
  `time` int DEFAULT NULL COMMENT '耗时',
  `business_domain` varchar(32) DEFAULT NULL COMMENT '业务模块',
  `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志;';


-- sm.sys_organization definition

CREATE TABLE `sys_organization` (
  `id` varchar(32) NOT NULL COMMENT '机构标识',
  `code` varchar(32) NOT NULL COMMENT '机构编码',
  `name` varchar(90) NOT NULL COMMENT '机构名称',
  `parent_org_id` varchar(32) DEFAULT NULL COMMENT '上级机构标识',
  `area_id` varchar(32) DEFAULT NULL COMMENT '归属区域标识',
  `org_type` varchar(1) DEFAULT NULL COMMENT '机构类型',
  `email` varchar(90) DEFAULT NULL COMMENT '邮件地址',
  `contact_person` varchar(90) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `cn_spell` varchar(32) DEFAULT NULL COMMENT '拼音码',
  `short_name` varchar(32) DEFAULT NULL COMMENT '机构简称',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构表;';


-- sm.sys_password_history definition

CREATE TABLE `sys_password_history` (
  `id` varchar(32) DEFAULT NULL COMMENT '主键',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户标识',
  `password` varchar(90) DEFAULT NULL COMMENT '密码',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='密码历史表;';


-- sm.sys_right definition

CREATE TABLE `sys_right` (
  `id` varchar(32) NOT NULL COMMENT '权限标识',
  `right_type` varchar(1) NOT NULL COMMENT '权限类型',
  `right_name` varchar(90) NOT NULL COMMENT '权限名称',
  `parent_right_id` varchar(32) DEFAULT NULL COMMENT '上级权限标识',
  `url` varchar(255) DEFAULT NULL COMMENT '访问地址',
  `icon` varchar(90) DEFAULT NULL COMMENT '权限图标',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `http_method` varchar(16) DEFAULT NULL COMMENT '请求类型',
  `right_ctl_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限控制类型',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sys_right_parent_right_id_IDX` (`parent_right_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表;';


-- sm.sys_role definition

CREATE TABLE `sys_role` (
  `id` varchar(32) NOT NULL COMMENT '角色标识',
  `role_code` varchar(32) NOT NULL COMMENT '角色编码',
  `role_name` varchar(90) NOT NULL COMMENT '角色名称',
  `role_type` varchar(1) NOT NULL COMMENT '角色类型',
  `visible` varchar(1) DEFAULT NULL COMMENT '是否可见',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `auto_assign` varchar(1) DEFAULT NULL COMMENT '自动分配',
  `right_ctl_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限控制类型',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表;';


-- sm.sys_role_data_right definition

CREATE TABLE `sys_role_data_right` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_id` varchar(32) NOT NULL COMMENT '角色标识',
  `data_right_id` varchar(32) NOT NULL COMMENT '数据权限标识',
  `data_right_value_id` varchar(32) NOT NULL COMMENT '数据权限值标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色数据权限;';


-- sm.sys_role_right definition

CREATE TABLE `sys_role_right` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_id` varchar(32) NOT NULL COMMENT '角色标识',
  `right_id` varchar(32) NOT NULL COMMENT '权限标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sys_role_right_role_id_IDX` (`role_id`) USING BTREE,
  KEY `sys_role_right_right_id_IDX` (`right_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限;';


-- sm.sys_tenant definition

CREATE TABLE `sys_tenant` (
  `id` varchar(32) NOT NULL COMMENT '租户标识',
  `name` varchar(90) NOT NULL COMMENT '租户名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户表;';


-- sm.sys_user definition

CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '用户标识',
  `user_name` varchar(32) NOT NULL COMMENT '登录名',
  `organization_id` varchar(32) NOT NULL COMMENT '机构标识',
  `department_id` varchar(32) DEFAULT NULL COMMENT '部门标识',
  `user_type` varchar(1) NOT NULL COMMENT '用户类型',
  `is_admin` varchar(1) DEFAULT NULL COMMENT '是否管理员',
  `real_name` varchar(90) NOT NULL COMMENT '用户姓名',
  `password` varchar(90) NOT NULL COMMENT '用户密码;密文',
  `status` varchar(1) NOT NULL COMMENT '用户状态',
  `valid_status` varchar(1) NOT NULL COMMENT '验证状态',
  `gender` varchar(1) NOT NULL COMMENT '性别',
  `telephone` varchar(32) DEFAULT NULL COMMENT '固定电话',
  `mobile` varchar(32) DEFAULT NULL COMMENT '移动号码',
  `email` varchar(90) DEFAULT NULL COMMENT '电子邮件',
  `expire_date` datetime DEFAULT NULL COMMENT '有效期',
  `password_expire_date` datetime DEFAULT NULL COMMENT '密码过期时间',
  `lock_date` datetime DEFAULT NULL COMMENT '锁定时间',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_UN` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表;';


-- sm.sys_user_data_right definition

CREATE TABLE `sys_user_data_right` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户标识',
  `data_right_id` varchar(32) NOT NULL COMMENT '数据权限标识',
  `data_right_value_id` varchar(32) NOT NULL COMMENT '数据权限值标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户数据权限;';


-- sm.sys_user_right definition

CREATE TABLE `sys_user_right` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户标识',
  `right_id` varchar(32) NOT NULL COMMENT '权限标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sys_user_right_user_id_IDX` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户权限;';


-- sm.sys_user_role definition

CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户标识',
  `role_id` varchar(32) NOT NULL COMMENT '角色标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sys_user_role_user_id_IDX` (`user_id`) USING BTREE,
  KEY `sys_user_role_role_id_IDX` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色;';