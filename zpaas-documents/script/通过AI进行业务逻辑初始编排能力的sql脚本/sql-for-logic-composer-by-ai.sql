1、修改表结构
ALTER TABLE zpaas_lowcode_revise.pub_dict MODIFY COLUMN dict_value3 varchar(9000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '参数值3';
ALTER TABLE zpaas_lowcode_revise.be_flow_node MODIFY COLUMN cfg_requirement varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置要求';

2、执行pub_dict_202603112037.sql文件中语句

3、更新be_flow_node表中的数据
（1）delete from be_flow_node;
（2）执行be_flow_node_202603112012.sql文件中的语句