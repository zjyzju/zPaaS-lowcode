-- zpaas_lowcode_revise.be_attribute definition

CREATE TABLE `be_attribute` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `is_list_attr` varchar(1) NOT NULL COMMENT '是否列表类型',
  `attr_type` varchar(1) NOT NULL COMMENT '属性类型',
  `attr_class` varchar(90) NOT NULL COMMENT '属性类',
  `object_type` varchar(1) NOT NULL COMMENT '归属对象类型',
  `object_id` varchar(32) NOT NULL COMMENT '归属对象标识',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='属性定义';


-- zpaas_lowcode_revise.be_attribute_column_mapping definition

CREATE TABLE `be_attribute_column_mapping` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `file_object_mapping_id` varchar(32) NOT NULL COMMENT '文件对象映射标识',
  `attribute_id` varchar(32) NOT NULL COMMENT '属性标识',
  `column_index` varchar(64) NOT NULL COMMENT '列序号',
  `header_name` varchar(64) DEFAULT NULL COMMENT '列名',
  `need_merge` varchar(1) DEFAULT NULL COMMENT '是否合并',
  `is_sum` varchar(1) DEFAULT NULL COMMENT '是否累加',
  `sum_by_column_index` varchar(64) DEFAULT NULL COMMENT '累加维度列',
  `sum_column_index` varchar(64) DEFAULT NULL COMMENT '累加列',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='属性列映射;';


-- zpaas_lowcode_revise.be_attribute_mapping definition

CREATE TABLE `be_attribute_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `from_attribute_id` varchar(32) NOT NULL COMMENT '源属性标识',
  `to_attribute_id` varchar(32) NOT NULL COMMENT '目标属性标识',
  `data_mapping_id` varchar(32) NOT NULL COMMENT '数据映射标识',
  `transfer_method` varchar(32) DEFAULT NULL COMMENT '转换方法',
  `transfer_cfg` varchar(2048) DEFAULT NULL COMMENT '转换配置信息',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据对象属性映射';


-- zpaas_lowcode_revise.be_business_flow definition

CREATE TABLE `be_business_flow` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `transaction_required` varchar(1) NOT NULL COMMENT '是否需要事务',
  `db_schema_id` varchar(32) DEFAULT NULL COMMENT '对应数据库标识',
  `flow_type` varchar(1) NOT NULL COMMENT '业务流类型',
  `parent_flow_id` varchar(32) DEFAULT NULL COMMENT '上级业务流标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务流定义';


-- zpaas_lowcode_revise.be_business_flow_bpmn_file definition

CREATE TABLE `be_business_flow_bpmn_file` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `business_flow_id` varchar(32) DEFAULT NULL COMMENT '业务流标识',
  `bpmn_xml` text COMMENT 'BPMN文件',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务流BPMN文件表';


-- zpaas_lowcode_revise.be_business_flow_node definition

CREATE TABLE `be_business_flow_node` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `flow_node_id` varchar(32) NOT NULL COMMENT '节点标识',
  `business_flow_id` varchar(32) NOT NULL COMMENT '业务流标识',
  `bpmn_node_id` varchar(32) DEFAULT NULL COMMENT 'BPMN节点标识',
  `node_order` int DEFAULT NULL COMMENT '节点顺序',
  `node_pre_cfg` varchar(4000) DEFAULT NULL COMMENT '节点预处理配置信息',
  `node_cfg` varchar(4000) DEFAULT NULL COMMENT '节点配置信息',
  `node_post_cfg` varchar(1500) DEFAULT NULL COMMENT '节点后处理配置信息',
  `node_reserved_cfg1` varchar(1500) DEFAULT NULL COMMENT '节点预留配置信息1',
  `node_reserved_cfg2` varchar(1500) DEFAULT NULL COMMENT '节点预留配置信息2',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务流节点';


-- zpaas_lowcode_revise.be_business_system_auth definition

CREATE TABLE `be_business_system_auth` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `auth_type` varchar(2) NOT NULL COMMENT '鉴权方式',
  `user_info_key` varchar(90) DEFAULT NULL COMMENT '存放用户信息的Key',
  `user_id_attr` varchar(90) DEFAULT NULL COMMENT '存放用户标识的属性',
  `user_info_service` varchar(32) DEFAULT NULL COMMENT '用户信息服务对象',
  `user_info_method` varchar(32) DEFAULT NULL COMMENT '用户信息服务方法',
  `auth_config` varchar(255) DEFAULT NULL COMMENT '鉴权方式配置信息',
  `ignore_urls` varchar(2000) DEFAULT NULL COMMENT '忽略的URL',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务系统认证信息表';


-- zpaas_lowcode_revise.be_business_system_auto_load definition

CREATE TABLE `be_business_system_auto_load` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识',
  `auto_load_service` varchar(32) DEFAULT NULL COMMENT '自动加载服务对象标识',
  `auto_load_method` varchar(32) DEFAULT NULL COMMENT '自动加载服务方法标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务系统自动加载信息;';


-- zpaas_lowcode_revise.be_column_mapping definition

CREATE TABLE `be_column_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `attribute_id` varchar(32) NOT NULL COMMENT '属性标识',
  `column_id` varchar(32) NOT NULL COMMENT '字段标识',
  `object_relation_mapping_id` varchar(32) NOT NULL COMMENT '对象关系映射标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字段属性映射';


-- zpaas_lowcode_revise.be_cron_job definition

CREATE TABLE `be_cron_job` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(32) NOT NULL COMMENT '编码',
  `job_type` varchar(1) NOT NULL COMMENT '任务类型',
  `job_schedule` varchar(90) NOT NULL COMMENT '任务定时设置',
  `job_handler_service` varchar(32) NOT NULL COMMENT '任务执行器服务对象',
  `job_handler_method` varchar(32) NOT NULL COMMENT '任务执行器服务方法',
  `job_cfg` varchar(900) DEFAULT NULL COMMENT '任务配置信息',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务管理;';


-- zpaas_lowcode_revise.be_data_mapping definition

CREATE TABLE `be_data_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `from_object_type` varchar(1) NOT NULL COMMENT '源对象类型',
  `from_object_id` varchar(32) NOT NULL COMMENT '源对象标识',
  `to_object_type` varchar(1) NOT NULL COMMENT '目标对象类型',
  `to_object_id` varchar(32) NOT NULL COMMENT '目标对象标识',
  `sub_data_mappings` varchar(900) DEFAULT NULL COMMENT '子映射规则',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据对象映射';


-- zpaas_lowcode_revise.be_data_transfer_method definition

CREATE TABLE `be_data_transfer_method` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `impl_class` varchar(90) NOT NULL COMMENT '转换方法实现类',
  `cfg_requirement` varchar(900) NOT NULL COMMENT '配置要求',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='转换方法定义;';


-- zpaas_lowcode_revise.be_db_column definition

CREATE TABLE `be_db_column` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `type` varchar(32) NOT NULL COMMENT '字段类型',
  `length` varchar(16) NOT NULL COMMENT '字段长度',
  `is_primary_key` varchar(1) NOT NULL COMMENT '是否主键',
  `is_null` varchar(1) DEFAULT NULL COMMENT '是否可空',
  `key_generate_method` varchar(90) DEFAULT NULL COMMENT '主键生成方法',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `table_id` varchar(32) NOT NULL COMMENT '库表标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据库表定段定义';


-- zpaas_lowcode_revise.be_db_schema definition

CREATE TABLE `be_db_schema` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `db_type` varchar(1) NOT NULL COMMENT '数据库类型;O：Oracle；M：Mysql：E：ElasticSearch',
  `username` varchar(90) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码;需要加密存储',
  `url` varchar(512) NOT NULL COMMENT '访问串',
  `driver_class` varchar(90) DEFAULT NULL COMMENT '驱动类',
  `data_source_cfg` varchar(900) DEFAULT NULL COMMENT '数据源配置',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据库定义';


-- zpaas_lowcode_revise.be_db_table definition

CREATE TABLE `be_db_table` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `schema_id` varchar(32) NOT NULL COMMENT '数据库标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据库表定义';


-- zpaas_lowcode_revise.be_dict_resource definition

CREATE TABLE `be_dict_resource` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '字典资源名称',
  `type` varchar(1) NOT NULL COMMENT '字典类型',
  `db_schema_id` varchar(32) NOT NULL COMMENT '归属数据库',
  `dict_table` varchar(90) NOT NULL COMMENT '字典表',
  `sub_dict_table` varchar(90) NOT NULL COMMENT '字典表从表',
  `dict_id_col` varchar(90) NOT NULL COMMENT '字典主键列',
  `dict_code_col` varchar(90) NOT NULL COMMENT '字典编码列',
  `dict_name_col` varchar(90) NOT NULL COMMENT '字典名称列',
  `parent_dict_code_col` varchar(90) NOT NULL COMMENT '上级字典编码列',
  `parent_dict_value_col` varchar(90) NOT NULL COMMENT '上级字典值列',
  `dict_value_col` varchar(90) NOT NULL COMMENT '字典值列',
  `dict_value_label_col` varchar(90) NOT NULL COMMENT '字典值名称列',
  `dict_value2_col` varchar(90) DEFAULT NULL COMMENT '字典值列2',
  `dict_value3_col` varchar(90) DEFAULT NULL COMMENT '字典值列3',
  `dict_status_col` varchar(90) DEFAULT NULL COMMENT '字典状态列',
  `eff_status_value` varchar(90) DEFAULT NULL COMMENT '有效状态值',
  `sub_table_dict_code_col` varchar(90) DEFAULT NULL COMMENT '子表字典编码列',
  `sub_table_status_col` varchar(90) DEFAULT NULL COMMENT '子表状态列',
  `eff_sub_table_status_value` varchar(90) DEFAULT NULL COMMENT '有效子表状态值',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典表资源配置;';


-- zpaas_lowcode_revise.be_domain_object definition

CREATE TABLE `be_domain_object` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `domain_id` varchar(32) NOT NULL COMMENT '业务域标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='领域对象定义';


-- zpaas_lowcode_revise.be_dynamic_mapping definition

CREATE TABLE `be_dynamic_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `mapping_type` varchar(6) NOT NULL COMMENT '映射类型',
  `business_type` varchar(90) DEFAULT NULL COMMENT '业务类型',
  `sub_business_type` varchar(90) DEFAULT NULL COMMENT '子业务类型',
  `key_attribute` varchar(90) DEFAULT NULL COMMENT '关键属性',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态映射配置;';


-- zpaas_lowcode_revise.be_dynamic_mapping_detail definition

CREATE TABLE `be_dynamic_mapping_detail` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `dynamic_mapping_id` varchar(32) NOT NULL COMMENT '归属动态映射标识',
  `key_attr_value` varchar(255) NOT NULL COMMENT '关键属性值',
  `mapping_object_id` varchar(255) NOT NULL COMMENT '映射对象标识',
  `description` varchar(255) DEFAULT NULL COMMENT '映射说明',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态映射明细信息;';


-- zpaas_lowcode_revise.be_exposed_service definition

CREATE TABLE `be_exposed_service` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `service_id` varchar(32) NOT NULL,
  `operation_id` varchar(32) NOT NULL,
  `http_method` varchar(32) NOT NULL,
  `http_mapping` varchar(90) NOT NULL,
  `status` varchar(1) NOT NULL COMMENT '状态',
  `domain_id` varchar(32) NOT NULL COMMENT '业务域标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暴露服务定义';


-- zpaas_lowcode_revise.be_file_object_mapping definition

CREATE TABLE `be_file_object_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `object_type` varchar(1) NOT NULL COMMENT '对象类型',
  `object_id` varchar(32) NOT NULL COMMENT '对象标识',
  `mapping_direct` varchar(1) NOT NULL COMMENT '映射方向',
  `file_desc` varchar(32) NOT NULL COMMENT '文件说明',
  `file_type` varchar(1) NOT NULL COMMENT '文件类型',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件对象映射;';


-- zpaas_lowcode_revise.be_filter definition

CREATE TABLE `be_filter` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `filter_class` varchar(90) NOT NULL COMMENT '实现类',
  `filter_type` varchar(1) NOT NULL COMMENT '过滤器类型',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='过滤器定义';


-- zpaas_lowcode_revise.be_filter_chain definition

CREATE TABLE `be_filter_chain` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `chain_type` varchar(1) NOT NULL COMMENT '过滤器链类型',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='过滤器链定义';


-- zpaas_lowcode_revise.be_filter_chain_filter definition

CREATE TABLE `be_filter_chain_filter` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `filter_chain_id` varchar(32) DEFAULT NULL COMMENT '过滤器链标识',
  `filter_id` varchar(32) DEFAULT NULL COMMENT '过滤器标识',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='过滤器链的过滤器';


-- zpaas_lowcode_revise.be_flow_node definition

CREATE TABLE `be_flow_node` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `type` varchar(1) DEFAULT NULL COMMENT '类型',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `node_class` varchar(90) NOT NULL COMMENT '节点实现类',
  `cfg_requirement` varchar(900) NOT NULL COMMENT '配置要求',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='节点定义';


-- zpaas_lowcode_revise.be_message_consumer definition

CREATE TABLE `be_message_consumer` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(90) DEFAULT NULL COMMENT '描述',
  `consumer_resource_id` varchar(32) NOT NULL COMMENT '消息消费端资源标识',
  `message_topics` varchar(900) NOT NULL COMMENT '订阅主题',
  `service_object_id` varchar(32) NOT NULL COMMENT '服务对象标识',
  `operation_id` varchar(32) NOT NULL COMMENT '方法标识',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息消费服务定义;';


-- zpaas_lowcode_revise.be_object_relation_mapping definition

CREATE TABLE `be_object_relation_mapping` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `domain_object_id` varchar(32) NOT NULL COMMENT '领域对象标识',
  `table_id` varchar(32) NOT NULL COMMENT '库表标识',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象关系映射定义';


-- zpaas_lowcode_revise.be_operation definition

CREATE TABLE `be_operation` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `object_type` varchar(1) NOT NULL COMMENT '归属对象类型;S：服务对象；D：领域对象',
  `object_id` varchar(32) NOT NULL COMMENT '归属对象标识',
  `business_flow_id` varchar(32) NOT NULL COMMENT '业务流标识',
  `rule_group_id` varchar(32) DEFAULT NULL COMMENT '入参校验规则',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='方法定义';


-- zpaas_lowcode_revise.be_parameter definition

CREATE TABLE `be_parameter` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `is_list_param` varchar(1) NOT NULL COMMENT '是否列表参数',
  `param_type` varchar(1) NOT NULL COMMENT '参数类型',
  `param_class` varchar(90) NOT NULL COMMENT '参数类',
  `is_in_param` varchar(1) NOT NULL COMMENT '是否入参',
  `display_order` int NOT NULL COMMENT '显示顺序',
  `operation_id` varchar(32) NOT NULL COMMENT '归属方法标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数定义';


-- zpaas_lowcode_revise.be_security_key_resource definition

CREATE TABLE `be_security_key_resource` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `type` varchar(1) NOT NULL COMMENT '类型',
  `security_key` varchar(1024) DEFAULT NULL COMMENT '密钥',
  `cipher_algorithm` varchar(32) DEFAULT NULL COMMENT '加密算法',
  `private_key_file` blob COMMENT '私钥',
  `private_key_file_name` varchar(90) DEFAULT NULL COMMENT '私钥文件名',
  `public_key_file` blob COMMENT '公钥',
  `public_key_file_name` varchar(90) DEFAULT NULL COMMENT '公钥文件名',
  `private_key_pwd` varchar(256) DEFAULT NULL COMMENT '私钥密码',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安全密钥资源;';


-- zpaas_lowcode_revise.be_server_resource definition

CREATE TABLE `be_server_resource` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `resource_type` varchar(1) NOT NULL COMMENT '资源类型',
  `username` varchar(90) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码;需要加密存储',
  `host` varchar(90) DEFAULT NULL COMMENT '地址',
  `port` varchar(32) DEFAULT NULL COMMENT '端口',
  `url` varchar(512) DEFAULT NULL COMMENT '访问串',
  `server_cfg` varchar(900) DEFAULT NULL COMMENT '服务器配置',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服务器资源定义';


-- zpaas_lowcode_revise.be_service_object definition

CREATE TABLE `be_service_object` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `domain_id` varchar(32) NOT NULL COMMENT '业务域标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服务对象定义';


-- zpaas_lowcode_revise.be_sql_management definition

CREATE TABLE `be_sql_management` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `code` varchar(32) NOT NULL COMMENT '编码',
  `description` varchar(90) DEFAULT NULL COMMENT '描述',
  `sql_sentence` varchar(2000) NOT NULL COMMENT 'SQL语句',
  `resource_type` varchar(1) DEFAULT NULL COMMENT '服务器资源类型',
  `resource_id` varchar(32) NOT NULL COMMENT '服务器资源标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SQL管理';


-- zpaas_lowcode_revise.be_thirdparty_system definition

CREATE TABLE `be_thirdparty_system` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) DEFAULT NULL COMMENT '名称',
  `access_key` varchar(90) NOT NULL COMMENT '访问密钥账号',
  `access_secret` varchar(90) NOT NULL COMMENT '访问密钥',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='第三方接入系统表';


-- zpaas_lowcode_revise.be_validate_rule definition

CREATE TABLE `be_validate_rule` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `rule_group_id` varchar(32) NOT NULL COMMENT '校验规则组标识',
  `attribute_id` varchar(32) NOT NULL COMMENT '属性标识',
  `sub_attribute_id` varchar(32) DEFAULT NULL COMMENT '子属性标识',
  `rule_type_id` varchar(32) NOT NULL COMMENT '规则类型标识',
  `rule_value1` varchar(900) DEFAULT NULL COMMENT '规则值1',
  `rule_value2` varchar(900) DEFAULT NULL COMMENT '规则值2',
  `rule_value3` varchar(900) DEFAULT NULL COMMENT '规则值3',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校验规则定义';


-- zpaas_lowcode_revise.be_validate_rule_group definition

CREATE TABLE `be_validate_rule_group` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `object_type` varchar(1) DEFAULT NULL COMMENT '对象类型',
  `object_id` varchar(32) DEFAULT NULL COMMENT '对象标识',
  `sub_rule_groups` varchar(900) DEFAULT NULL COMMENT '子校验规则组配置',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校验规则组定义';


-- zpaas_lowcode_revise.be_validate_rule_type definition

CREATE TABLE `be_validate_rule_type` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `rule_class` varchar(90) NOT NULL COMMENT '校验规则实现类',
  `cfg_requirement` varchar(900) NOT NULL COMMENT '配置要求',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校验规则类型定义';


-- zpaas_lowcode_revise.be_value_object definition

CREATE TABLE `be_value_object` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `code` varchar(90) NOT NULL COMMENT '编码',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `domain_id` varchar(32) NOT NULL COMMENT '业务域标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='值对象定义';


-- zpaas_lowcode_revise.be_workflow_process_resource definition

CREATE TABLE `be_workflow_process_resource` (
  `id` varchar(32) NOT NULL COMMENT '流程资源标识',
  `name` varchar(90) NOT NULL COMMENT '流程资源名称',
  `bpmn_xml` text NOT NULL COMMENT '流程定义文件',
  `process_def_id` varchar(64) DEFAULT NULL COMMENT '流程定义标识',
  `process_def_key` varchar(90) DEFAULT NULL COMMENT '流程定义Key',
  `act_model_id` varchar(64) DEFAULT NULL COMMENT '流程模型标识',
  `description` varchar(900) DEFAULT NULL COMMENT '流程资源描述',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作流流程资源;';


-- zpaas_lowcode_revise.bi_data_model definition

CREATE TABLE `bi_data_model` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '数据模型编码',
  `name` varchar(90) NOT NULL COMMENT '数据模型名称',
  `type` varchar(1) NOT NULL COMMENT '类型',
  `data_source_type` varchar(16) NOT NULL COMMENT '数据源类型',
  `data_source_id` varchar(32) NOT NULL COMMENT '数据源标识',
  `physical_src_object` varchar(4000) NOT NULL COMMENT '物理来源对象',
  `src_object_desc` varchar(900) DEFAULT NULL COMMENT '来源对象说明',
  `domain_id` varchar(32) DEFAULT NULL COMMENT '归属业务域标识',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型定义;';


-- zpaas_lowcode_revise.bi_data_model_column definition

CREATE TABLE `bi_data_model_column` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '字段编码',
  `name` varchar(90) NOT NULL COMMENT '字段名称',
  `data_model_id` varchar(32) NOT NULL COMMENT '数据模型标识',
  `column_type` varchar(1) NOT NULL COMMENT '字段类型',
  `value_type` varchar(32) NOT NULL COMMENT '值类型',
  `translate_type` varchar(1) DEFAULT NULL COMMENT '翻译类型',
  `option_type` varchar(1) DEFAULT NULL COMMENT '翻译选项配置类型',
  `option_cfg` varchar(900) DEFAULT NULL COMMENT '翻译选项配置',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型字段定义;';


-- zpaas_lowcode_revise.bi_data_model_filter definition

CREATE TABLE `bi_data_model_filter` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '筛选器编码',
  `name` varchar(90) NOT NULL COMMENT '筛选器名称',
  `data_model_id` varchar(32) NOT NULL COMMENT '数据模型标识',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型筛选器定义;';


-- zpaas_lowcode_revise.bi_data_model_filter_value definition

CREATE TABLE `bi_data_model_filter_value` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '筛选值编码',
  `name` varchar(90) NOT NULL COMMENT '筛选值名称',
  `filter_id` varchar(32) NOT NULL COMMENT '筛选器标识',
  `filter_formula` varchar(900) NOT NULL COMMENT '过滤器',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型筛选值定义;';


-- zpaas_lowcode_revise.bi_data_model_metrics definition

CREATE TABLE `bi_data_model_metrics` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '指标编码',
  `name` varchar(90) NOT NULL COMMENT '指标名称',
  `metric_type` varchar(1) NOT NULL COMMENT '指标类型',
  `data_model_id` varchar(32) NOT NULL COMMENT '数据模型标识',
  `metric_formula` varchar(900) DEFAULT NULL COMMENT '指标公式',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型指标定义;';


-- zpaas_lowcode_revise.bi_data_model_tag definition

CREATE TABLE `bi_data_model_tag` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '标签编码',
  `name` varchar(90) NOT NULL COMMENT '标签名称',
  `data_model_id` varchar(32) NOT NULL COMMENT '数据模型标识',
  `filter_formula` varchar(900) NOT NULL COMMENT '标签过滤公式',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型标签定义;';


-- zpaas_lowcode_revise.bi_data_set definition

CREATE TABLE `bi_data_set` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(90) NOT NULL COMMENT '数据集编码',
  `name` varchar(90) NOT NULL COMMENT '数据集名称',
  `domain_id` varchar(32) DEFAULT NULL COMMENT '归属业务域标识',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据集定义;';


-- zpaas_lowcode_revise.bi_data_set_composition definition

CREATE TABLE `bi_data_set_composition` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `data_set_id` varchar(32) NOT NULL COMMENT '数据集标识',
  `data_model_id` varchar(32) NOT NULL COMMENT '数据模型标识',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据集构成;';


-- zpaas_lowcode_revise.bi_data_set_composition_join definition

CREATE TABLE `bi_data_set_composition_join` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `data_set_id` varchar(32) NOT NULL COMMENT '数据集标识',
  `join_main_compo_id` varchar(32) NOT NULL COMMENT '关联主构成标识',
  `main_col_id` varchar(32) NOT NULL COMMENT '主表字段标识',
  `cond_type` varchar(2) DEFAULT NULL COMMENT '条件类型',
  `join_type` varchar(1) DEFAULT NULL COMMENT '关联类型',
  `join_rel_data_model_id` varchar(32) DEFAULT NULL COMMENT '关联模型标识',
  `rel_col_type` varchar(1) NOT NULL COMMENT '关联字段类型',
  `rel_col_id` varchar(32) NOT NULL COMMENT '关联表字段标识',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据集涉及模型间关联设置;';


-- zpaas_lowcode_revise.bi_data_set_detail definition

CREATE TABLE `bi_data_set_detail` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `data_set_id` varchar(32) NOT NULL COMMENT '数据集标识',
  `detail_type` varchar(1) NOT NULL COMMENT '明细内容类型',
  `detail_content_id` varchar(32) NOT NULL COMMENT '明细内容标识',
  `detail_content_alias` varchar(32) DEFAULT NULL COMMENT '明细内容别名;编码',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据集明细内容;';


-- zpaas_lowcode_revise.fe_combo_func_define definition

CREATE TABLE `fe_combo_func_define` (
  `id` varchar(32) NOT NULL COMMENT '组合功能标识',
  `name` varchar(90) NOT NULL COMMENT '组合功能名称',
  `combo_template_id` varchar(32) NOT NULL COMMENT '组合功能模板标识',
  `domain_id` varchar(32) DEFAULT NULL COMMENT '归属业务域标识',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组合功能定义表';


-- zpaas_lowcode_revise.fe_combo_func_tab definition

CREATE TABLE `fe_combo_func_tab` (
  `id` varchar(32) NOT NULL COMMENT '组合功能标签页标识',
  `name` varchar(90) NOT NULL COMMENT '组合功能标签页名称',
  `combo_func_id` varchar(32) NOT NULL COMMENT '组合功能标识',
  `tab_params` varchar(900) DEFAULT NULL COMMENT '标签页参数',
  `tab_func_id` varchar(32) NOT NULL COMMENT '标签页功能标识',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组合功能标签页';


-- zpaas_lowcode_revise.fe_combo_func_template definition

CREATE TABLE `fe_combo_func_template` (
  `id` varchar(32) NOT NULL COMMENT '组合功能模板标识',
  `name` varchar(90) NOT NULL COMMENT '组合功能模板名称',
  `url` varchar(255) NOT NULL COMMENT '访问URL',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组合功能模板';


-- zpaas_lowcode_revise.fe_func_customized_layout definition

CREATE TABLE `fe_func_customized_layout` (
  `id` varchar(32) NOT NULL COMMENT '布局标识',
  `name` varchar(90) NOT NULL COMMENT '布局名称',
  `component_code` varchar(10) NOT NULL COMMENT '组件编码',
  `component_type` varchar(10) NOT NULL COMMENT '组件类型',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `page_id` varchar(32) NOT NULL COMMENT '页面标识',
  `parent_layout_id` varchar(32) DEFAULT NULL COMMENT '上级布局标识',
  `display_order` int NOT NULL COMMENT '显示顺序',
  `layout_cfg` varchar(900) DEFAULT NULL COMMENT '页面配置信息',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能自定义页面布局;';


-- zpaas_lowcode_revise.fe_func_customized_layout_region definition

CREATE TABLE `fe_func_customized_layout_region` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `page_id` varchar(32) NOT NULL COMMENT '页面标识',
  `layout_id` varchar(32) NOT NULL COMMENT '布局标识',
  `region_id` varchar(32) NOT NULL COMMENT '区域标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能自定义页面布局区域关系;';


-- zpaas_lowcode_revise.fe_func_customized_page definition

CREATE TABLE `fe_func_customized_page` (
  `id` varchar(32) NOT NULL COMMENT '功能自定义页面标识',
  `name` varchar(90) NOT NULL COMMENT '自定义页面名称',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `page_cfg` varchar(900) DEFAULT NULL COMMENT '页面配置信息',
  `is_main_page` varchar(1) DEFAULT NULL COMMENT '是否主页面',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能自定义页面;';


-- zpaas_lowcode_revise.fe_func_define definition

CREATE TABLE `fe_func_define` (
  `id` varchar(32) NOT NULL COMMENT '功能标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `domain_id` varchar(32) DEFAULT NULL COMMENT '归属业务域标识',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `template_id` varchar(32) NOT NULL COMMENT '功能模板标识',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能定义表';


-- zpaas_lowcode_revise.fe_func_object_assign definition

CREATE TABLE `fe_func_object_assign` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `object_type` varchar(1) NOT NULL COMMENT '管理对象类型',
  `object_id` varchar(32) NOT NULL COMMENT '管理对象标识',
  `assign_type` varchar(1) NOT NULL COMMENT '绑定对象类型',
  `key_attr_id` varchar(32) DEFAULT NULL COMMENT '关键属性标识',
  `rel_attr_id` varchar(32) DEFAULT NULL COMMENT '关联主对象属性标识',
  `main_rel_attr_id` varchar(32) DEFAULT NULL COMMENT '主对象关联属性标识',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能管理对象绑定表';


-- zpaas_lowcode_revise.fe_func_object_attr_options definition

CREATE TABLE `fe_func_object_attr_options` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `object_assign_id` varchar(32) NOT NULL COMMENT '对象绑定标识',
  `attribute_id` varchar(32) NOT NULL COMMENT '属性标识',
  `interaction_type` varchar(1) DEFAULT NULL COMMENT '联动类型',
  `option_cfg_type` varchar(1) DEFAULT NULL COMMENT '可选值配置方式',
  `option_cfg` varchar(2000) DEFAULT NULL COMMENT '可选值配置',
  `parent_object_assign_id` varchar(32) DEFAULT NULL COMMENT '上级绑定对象标识',
  `parent_attribute_id` varchar(32) DEFAULT NULL COMMENT '上级属性标识',
  `display_hide_cfg` varchar(2000) DEFAULT NULL COMMENT '显示隐藏配置',
  `popup_cfg` varchar(2000) DEFAULT NULL COMMENT '弹出选择配置',
  `query_type` varchar(1) DEFAULT NULL COMMENT '查询类型',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能绑定对象属性选项设置';


-- zpaas_lowcode_revise.fe_func_region definition

CREATE TABLE `fe_func_region` (
  `id` varchar(32) NOT NULL COMMENT '功能区域标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `tpl_composite_id` varchar(32) NOT NULL COMMENT '功能模板构成标识',
  `tpl_region_id` varchar(32) NOT NULL COMMENT '功能模板区域标识',
  `region_cfg` varchar(900) DEFAULT NULL COMMENT '区域配置信息',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能区域定义表';


-- zpaas_lowcode_revise.fe_func_region_attr_assign definition

CREATE TABLE `fe_func_region_attr_assign` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `func_region_id` varchar(32) NOT NULL COMMENT '功能区域标识',
  `object_assign_id` varchar(32) NOT NULL COMMENT '对象绑定标识',
  `object_type` varchar(1) NOT NULL COMMENT '绑定对象类型',
  `object_id` varchar(32) NOT NULL COMMENT '绑定对象标识',
  `attribute_id` varchar(32) NOT NULL COMMENT '绑定属性标识',
  `display_type` varchar(1) NOT NULL COMMENT '显示类型',
  `display_name` varchar(90) NOT NULL COMMENT '显示名称',
  `display_width` int DEFAULT NULL COMMENT '显示宽度',
  `col_span` int DEFAULT NULL COMMENT '占用列数',
  `row_span` int DEFAULT NULL COMMENT '占用行数',
  `readonly` varchar(1) DEFAULT NULL COMMENT '是否只读',
  `required` varchar(1) DEFAULT NULL COMMENT '是否必填',
  `display_cfg` varchar(2000) DEFAULT NULL COMMENT '显示样式配置',
  `hidden` varchar(1) DEFAULT NULL COMMENT '是否隐藏',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能区域属性绑定表';


-- zpaas_lowcode_revise.fe_func_region_operation definition

CREATE TABLE `fe_func_region_operation` (
  `id` varchar(32) NOT NULL COMMENT '功能区域操作标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `func_id` varchar(32) NOT NULL COMMENT '功能标识',
  `func_region_id` varchar(32) NOT NULL COMMENT '功能区域标识',
  `tpl_operation_id` varchar(32) DEFAULT NULL COMMENT '模板区域操作标识',
  `operation_type` varchar(1) DEFAULT NULL COMMENT '操作类型',
  `operation_cfg` varchar(2000) DEFAULT NULL COMMENT '操作配置信息',
  `is_user_defined` varchar(1) DEFAULT NULL COMMENT '是否自定义操作',
  `exposed_service_id` varchar(32) DEFAULT NULL COMMENT '暴露服务标识',
  `service_object_id` varchar(32) DEFAULT NULL COMMENT '服务对象标识',
  `service_operation_id` varchar(32) DEFAULT NULL COMMENT '服务对象方法标识',
  `object_assign_id` varchar(32) DEFAULT NULL COMMENT '绑定对象标识',
  `target_region_id` varchar(32) DEFAULT NULL COMMENT '目标功能区域标识',
  `display_type` varchar(1) DEFAULT NULL COMMENT '显示类型',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `system_id` varchar(32) DEFAULT NULL COMMENT '业务系统标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能区域操作定义表';


-- zpaas_lowcode_revise.fe_func_template definition

CREATE TABLE `fe_func_template` (
  `id` varchar(32) NOT NULL COMMENT '功能模板标识',
  `name` varchar(90) NOT NULL COMMENT '模板名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `url` varchar(255) NOT NULL COMMENT '访问URL',
  `template_type` varchar(1) NOT NULL COMMENT '模板类型',
  `is_main_func` varchar(1) NOT NULL COMMENT '是否主功能',
  `tpl_cfg` varchar(900) DEFAULT NULL COMMENT '模板配置信息',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能模板表';


-- zpaas_lowcode_revise.fe_func_template_composite definition

CREATE TABLE `fe_func_template_composite` (
  `id` varchar(32) NOT NULL COMMENT '模板构成标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `template_id` varchar(32) NOT NULL COMMENT '功能模板标识',
  `tpl_region_id` varchar(32) NOT NULL COMMENT '模板区域标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能模板构成表';


-- zpaas_lowcode_revise.fe_func_template_region definition

CREATE TABLE `fe_func_template_region` (
  `id` varchar(32) NOT NULL COMMENT '模板区域标识',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `region_type` varchar(2) DEFAULT NULL COMMENT '区域类型',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `support_user_def_ops` varchar(1) DEFAULT NULL COMMENT '是否支持自定义操作',
  `support_operation` varchar(1) DEFAULT NULL COMMENT '是否支持操作',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能模板区域定义表';


-- zpaas_lowcode_revise.fe_func_template_region_operation definition

CREATE TABLE `fe_func_template_region_operation` (
  `id` varchar(32) NOT NULL COMMENT '操作标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `tpl_region_id` varchar(32) NOT NULL COMMENT '模板区域标识',
  `operation_type` varchar(1) NOT NULL COMMENT '操作类型',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `target_tpl_region_id` varchar(32) DEFAULT NULL COMMENT '目标模板区域标识',
  `display_type` varchar(1) DEFAULT NULL COMMENT '显示类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能模板区域操作定义表';


-- zpaas_lowcode_revise.pub_business_domain definition

CREATE TABLE `pub_business_domain` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务域';


-- zpaas_lowcode_revise.pub_business_system definition

CREATE TABLE `pub_business_system` (
  `id` varchar(32) NOT NULL COMMENT '标识',
  `name` varchar(90) NOT NULL COMMENT '名称',
  `servlet_context` varchar(90) NOT NULL COMMENT 'URL前缀',
  `description` varchar(900) DEFAULT NULL COMMENT '描述',
  `system_icon` varchar(90) DEFAULT NULL COMMENT '系统图标',
  `version` varchar(90) DEFAULT NULL COMMENT '版本号',
  `status` varchar(1) NOT NULL COMMENT '状态',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务系统';


-- zpaas_lowcode_revise.pub_deploy_instance_registry definition

CREATE TABLE `pub_deploy_instance_registry` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `address` varchar(90) NOT NULL COMMENT '实例地址',
  `port` int NOT NULL COMMENT '实例端口',
  `context_path` varchar(90) NOT NULL COMMENT '上下文地址',
  `is_https` varchar(1) DEFAULT NULL COMMENT '是否HTTPS',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `deploy_instance_address_port_unique_index` (`address`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部署实例注册表;';


-- zpaas_lowcode_revise.pub_dict definition

CREATE TABLE `pub_dict` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `dict_code` varchar(32) NOT NULL COMMENT '字典编码',
  `dict_name` varchar(90) DEFAULT NULL COMMENT '字典名称',
  `parent_dict_code` varchar(32) DEFAULT NULL COMMENT '上级字典编码',
  `parent_dict_value` varchar(900) DEFAULT NULL COMMENT '上级参数值',
  `dict_value` varchar(900) NOT NULL COMMENT '参数值',
  `dict_value_label` varchar(900) DEFAULT NULL COMMENT '参数值名称',
  `dict_value2` varchar(900) DEFAULT NULL COMMENT '参数值2',
  `dict_value3` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '参数值3',
  `display_order` int DEFAULT NULL COMMENT '显示顺序',
  `status` varchar(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典参数表;';


-- zpaas_lowcode_revise.pub_user_business_system definition

CREATE TABLE `pub_user_business_system` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户标识',
  `system_id` varchar(32) NOT NULL COMMENT '业务系统标识',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户业务系统授权表;';