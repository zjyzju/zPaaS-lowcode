<!-- 业务流编辑页 -->

<template>
<el-dialog v-model="showFlag" v-if="showBusinessFlowEdit ==true" title="业务流编辑" width="98vw" top="2vh"  :close-on-press-escape="false" :close-on-click-modal="false" :show-close="false">
    <template #header>
        <span class="title">业务流编辑</span>
    </template>
    <template #default>
        <div>
            <el-form v-if="showBusinessFlowEdit && businessFlowInfo" :model="businessFlowInfo" label-width="100px" :rules="validateRules" ref="businessFlowInfoForm">
                <el-row :gutter="4">
                    <el-col :span="6">
                        <el-form-item label="业务流标识" required prop="businessFlow.id">
                            <el-input v-model="businessFlowInfo.businessFlow.id" readonly  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="业务流名称" required prop="businessFlow.name">
                            <el-input v-model="businessFlowInfo.businessFlow.name"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item label="加入事务" required prop="businessFlow.transactionRequired">
                            <el-select v-model="businessFlowInfo.businessFlow.transactionRequired" class="m-2" placeholder="Select" size="small">
                                <el-option
                                  v-for="item in yesOrNoOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                                />
                             </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item label="业务流类型" required prop="businessFlow.flowType">
                            <el-select v-model="businessFlowInfo.businessFlow.flowType" class="m-2" placeholder="Select" size="small">
                                <el-option
                                  v-for="item in flowTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                                />
                             </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item label="对应数据库">
                            <el-select v-model="businessFlowInfo.businessFlow.dbSchemaId" class="m-2" placeholder="Select" size="small">
                                <el-option
                                  v-for="item in dbSchemas"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                                />
                             </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"   border-style="dashed" style="margin-top: 12px;margin-bottom: 12px">
                        <div class="toolbar1">
                            业务流
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="17">
                        <div id="businessFlowCanvas" class="canvas" style="height:70vh;"></div>    
                    </el-col>
                    <el-col :span="7">
                    <el-scrollbar height="70vh" >
                        <el-row :gutter="4">
		                    <el-col :span="1">
		                        <el-divider direction="vertical"  border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" />
		                    </el-col>
		                    <el-col :span="23">
		                        <div v-if="businessFlowNodeInfo != null && businessFlowNodeInfo.postCfgJson != null">
		                            <el-row :gutter="4">
		                                <el-col :span="24">
		                                    <el-form-item label="节点说明">
		                                        <el-input type="hidden" v-model="businessFlowNodeInfo.bpmnNodeId" readonly size="small" />
		                                        <el-input v-model="businessFlowNodeInfo.name" size="small" />
		                                    </el-form-item>
		                                </el-col>
		                            </el-row>
		                            <el-row :gutter="4">
		                                <el-col :span="15">
                                            <el-form-item label="节点类型">
                                                <el-popover placement="bottom-start" title="节点类型选择" :width="550" trigger="hover" size="small">
                                                    <template #reference>
                                                        <el-select v-model="businessFlowNodeInfo.flowNodeId" class="m-2" disabled placeholder="Select" size="small">
                                                            <el-option v-for="item in flowNodes" :key="item.id" :label="item.name" :value="item.id"/>
                                                        </el-select>
                                                    </template>
                                                    <el-scrollbar  height="450px">
                                                        <el-row :gutter="4" v-for="type in flowNodeTree">
                                                            <el-col :span="24">
                                                                <el-row :gutter="4">
                                                                    <el-col :span="24" style="color:rgba(97, 116, 209, 1)">
                                                                        {{ type.label }}
                                                                    </el-col>
                                                                </el-row>
                                                                <el-row :gutter="4">
                                                                    <el-col :span="24">
                                                                        <el-tag :key="flowNode.id" @click="selectFlowNode(flowNode.id)" style='margin:5px;min-width: 120px;color: black; cursor: pointer;' size="small" type="info" effect="light" v-for="flowNode in type.children">
                                                                            {{ flowNode.name }}
                                                                        </el-tag>
                                                                    </el-col>
                                                                </el-row>
                                                            </el-col>
                                                        </el-row>
                                                    </el-scrollbar>
                                                </el-popover>
                                            </el-form-item>
		                                </el-col>
		                                <el-col :span="1"></el-col>
		                                <el-col :span="8">
		                                    <el-popover placement="left-start" title="节点可用数据查看" :width="700" trigger="hover" size="small" @show="onAvailableDataSuggestShow(businessFlowNodeInfo,false)" @hide="onAvailableDataSuggestHide()">
											    <template #reference>
											        <el-button class="m-2">查看可用数据</el-button>
											    </template>
											    <!-- 节点可用数据查看 -->
                                                <availableDataSuggest v-if="showAvailableDataSuggest == true" :operation="businessFlowInfo.operation" :params="businessFlowInfo.operationParams" :ownerAttributes="businessFlowInfo.ownerAttributes" :domainObjectValues="domainObjectValues" :inProcessDatas="inProcessDatas" :loginSessionKey="loginSessionKey"/>
											</el-popover>
		                                </el-col>
		                            </el-row>
		                            <el-row :gutter="4">
		                                <el-col :span="24">
		                                    <el-row :gutter="4"  v-if="showNodeCfgEdit != null">
		                                        <el-col :span="24">
		                                            <el-divider v-if="showNodeCfgEdit != null" style="margin-top: 10px;margin-bottom: 15px" ><span  style="color:#3B85C8">节点处理</span></el-divider>
		                                        </el-col>
		                                    </el-row>
		                                    <!-- ORM数据库存取节点配置信息 -->
		                                    <nodeCfgEditORMRepositoryNode v-if="showNodeCfgEdit == 'ORMRepositoryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- LocalInvokeNode配置信息 -->
		                                    <nodeCfgEditLocalInvokeNode v-if="showNodeCfgEdit == 'LocalInvokeNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- ReturnNode配置信息 -->
		                                    <nodeCfgEditReturnNode v-if="showNodeCfgEdit == 'ReturnNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- BreakNode配置信息 -->
		                                    <nodeCfgEditBreakNode v-if="showNodeCfgEdit == 'BreakNode'" @updateNodeCfgJson="updateNodeCfgJson" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- ForEachNode配置信息 -->
		                                    <nodeCfgEditForEachNode v-if="showNodeCfgEdit == 'ForEachNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- ConditionNode配置信息 -->
		                                    <nodeCfgEditConditionNode v-if="showNodeCfgEdit == 'ConditionNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- SpringElNode配置信息 -->
		                                    <nodeCfgEditSpringElNode v-if="showNodeCfgEdit == 'SpringElNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- NativeJavaInvokeNode配置信息 -->
		                                    <nodeCfgEditNativeJavaInvokeNode v-if="showNodeCfgEdit == 'NativeJavaInvokeNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- ValidateNode配置信息 -->
		                                    <nodeCfgEditValidateNode v-if="showNodeCfgEdit == 'ValidateNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId" :tenantId="businessFlowInfo.businessFlow.tenantId"/>
		                                    <!-- ExceptionNode配置信息 -->
		                                    <nodeCfgEditExceptionNode v-if="showNodeCfgEdit == 'ExceptionNode'" @updateNodeCfgJson="updateNodeCfgJson" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- DynamicGetterNode配置信息 -->
		                                    <nodeCfgEditDynamicGetterNode v-if="showNodeCfgEdit == 'DynamicGetterNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- FtpRepositoryNode配置信息 -->
		                                    <nodeCfgEditFtpRepositoryNode v-if="showNodeCfgEdit == 'FtpRepositoryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- ObjectSetterNode配置信息 -->
		                                    <nodeCfgEditObjectSetterNode v-if="showNodeCfgEdit == 'ObjectSetterNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- RestInvokeNode配置信息 -->
		                                    <nodeCfgEditRestInvokeNode v-if="showNodeCfgEdit == 'RestInvokeNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 参数转换节点配置信息 -->
		                                    <nodeCfgEditTransferNode v-if="showNodeCfgEdit == 'TransferNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- Sql语句执行节点配置信息 -->
		                                    <nodeCfgEditSqlQueryNode v-if="showNodeCfgEdit == 'SqlQueryNode'" @updateNodeCfgJson="updateNodeCfgJson" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId" :dbSchemaId="businessFlowInfo.businessFlow.dbSchemaId"/>
		                                    <!-- ES语句执行节点配置信息 -->
		                                    <nodeCfgEditESQueryNode v-if="showNodeCfgEdit == 'ESQueryNode'" @updateNodeCfgJson="updateNodeCfgJson" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- Excel文件导入节点配置信息 -->
		                                    <nodeCfgEditExcelImportNode v-if="showNodeCfgEdit == 'ExcelImportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- Excel文件导出节点配置信息 -->
		                                    <nodeCfgEditExcelExportNode v-if="showNodeCfgEdit == 'ExcelExportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 工作流流程操作节点配置信息 -->
		                                    <nodeCfgEditWorkflowProcessOperatorNode v-if="showNodeCfgEdit == 'WorkflowProcessOperatorNode'" @showAvailableDataSelectPage="showAvailableDataSelectPage" @updateNodeCfgJson="updateNodeCfgJson" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 工作流任务查询节点配置信息 -->
		                                    <nodeCfgEditWorkflowTaskQueryNode v-if="showNodeCfgEdit == 'WorkflowTaskQueryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 工作流任务操作节点配置信息 -->
		                                    <nodeCfgEditWorkflowTaskOperatorNode v-if="showNodeCfgEdit == 'WorkflowTaskOperatorNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 哈希能力工具节点配置信息 -->
		                                    <nodeCfgEditHashUtilsNode v-if="showNodeCfgEdit == 'HashUtilsNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- Redis存取节点配置信息 -->
		                                    <nodeCfgEditRedisRepositoryNode v-if="showNodeCfgEdit == 'RedisRepositoryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 本地缓存节点配置信息 -->
                                            <nodeCfgEditLocalCacheNode v-if="showNodeCfgEdit == 'LocalCacheNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- MinIO存取节点配置信息 -->
                                            <nodeCfgEditMinioRepositoryNode v-if="showNodeCfgEdit == 'MinioRepositoryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- Kafka消息发送节点配置信息 -->
                                            <nodeCfgEditKafkaMessageSendNode v-if="showNodeCfgEdit == 'KafkaMessageSendNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- JSON文件导入节点配置信息 -->
                                            <nodeCfgEditJSONImportNode v-if="showNodeCfgEdit == 'JSONImportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- JSON文件导出节点配置信息 -->
                                            <nodeCfgEditJSONExportNode v-if="showNodeCfgEdit == 'JSONExportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- Text文件导入节点配置信息 -->
                                            <nodeCfgEditTextImportNode v-if="showNodeCfgEdit == 'TextImportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- Text文件导出节点配置信息 -->
                                            <nodeCfgEditTextExportNode v-if="showNodeCfgEdit == 'TextExportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- 邮件发送节点配置信息 -->
                                            <nodeCfgEditMailSendNode v-if="showNodeCfgEdit == 'MailSendNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- 短信发送节点配置信息 -->
                                            <nodeCfgEditSmsSendNode v-if="showNodeCfgEdit == 'SmsSendNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- 类型转换节点配置信息 -->
                                            <nodeCfgEditTypeConvertNode v-if="showNodeCfgEdit == 'TypeConvertNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
                                            <!-- 动态Excel文件导出节点配置信息 -->
		                                    <nodeCfgEditDynamicExcelExportNode v-if="showNodeCfgEdit == 'DynamicExcelExportNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 动态参数校验节点配置信息 -->
		                                    <nodeCfgEditDynamicValidateNode v-if="showNodeCfgEdit == 'DynamicValidateNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId" :tenantId="businessFlowInfo.businessFlow.tenantId"/>
		                                    <!-- 动态参数转换节点配置信息 -->
		                                    <nodeCfgEditDynamicTransferNode v-if="showNodeCfgEdit == 'DynamicTransferNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 动态本地方法调用配置信息 -->
		                                    <nodeCfgEditDynamicLocalInvokeNode v-if="showNodeCfgEdit == 'DynamicLocalInvokeNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- Zip能力节点配置信息 -->
		                                    <nodeCfgEditZipUtilsNode v-if="showNodeCfgEdit == 'ZipUtilsNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 字典缓存读取节点配置信息 -->
		                                    <nodeCfgEditDictRepositoryNode v-if="showNodeCfgEdit == 'DictRepositoryNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 本地文件操作节点配置信息 -->
		                                    <nodeCfgEditLocalFileNode v-if="showNodeCfgEdit == 'LocalFileNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 加解密能力节点配置信息 -->
		                                    <nodeCfgEditCipherUtilsNode v-if="showNodeCfgEdit == 'CipherUtilsNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- 签名验签能力节点配置信息 -->
		                                    <nodeCfgEditSignatureUtilsNode v-if="showNodeCfgEdit == 'SignatureUtilsNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    <!-- aigc能力调用节点配置信息 -->
		                                    <nodeCfgEditAigcInvokeNode v-if="showNodeCfgEdit == 'AigcInvokeNode'" @updateNodeCfgJson="updateNodeCfgJson" @showAvailableDataSelectPage="showAvailableDataSelectPage" :nodeCfgJson="nodeCfgJson" :showNodeCfgEdit="showNodeCfgEdit" :systemId="businessFlowInfo.businessFlow.systemId"/>
		                                    
		                                </el-col>
		                            </el-row>
		                            
		                            <el-row :gutter="4">
		                                <el-col :span="24">
		                                   <el-row :gutter="4">
		                                        <el-col :span="24">
		                                            <el-divider style="margin-top: 10px;margin-bottom: 15px" ><span style="color:#3B85C8">节点预处理</span></el-divider>
		                                        </el-col>
		                                    </el-row>
		                                    <el-row :gutter="4">
		                                        <el-col :span="16">
		                                            <el-form-item label="源对象来源" label-width='100px'>
		                                                <el-select v-model="fromObjectType" clearable class="m-2" placeholder="Select" size="small">
		                                                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                                                </el-select>
		                                            </el-form-item>
		                                        </el-col>
		                                        <el-col :span="8">
		                                            <el-form-item label="" label-width="10px">
                                                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.fromObjectType=contextType;this.fromObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                                                    </el-form-item>
		                                        </el-col>
		                                    </el-row>
		                                    <el-row :gutter="4">
		                                        <el-col :span="16">
		                                            <el-form-item label="源对象Key" label-width='100px'>
		                                                <el-input v-model="fromObjectKey" size="small" />
		                                            </el-form-item>
		                                        </el-col>
		                                        <el-col :span="8">
		                                            &nbsp;&nbsp;<el-checkbox v-model="isListType" label="源对象是否列表" />
		                                        </el-col>
		                                    </el-row>
		                                    <el-row :gutter="4">
		                                        <el-col :span="24">
		                                            <el-form-item label="映射规则" label-width='100px'>
		                                                <el-input v-model="attrMappings" type="textarea" :rows="3" size="small" />
		                                            </el-form-item>
		                                        </el-col>
		                                        
		                                    </el-row>
		                                    <el-row :gutter="4">
		                                        <el-col :span="16">
		                                            
		                                        </el-col>
		                                        <el-col :span="8">
		                                            <div style="margin-bottom: 5px;margin-top: 1px;margin-left: 5px;">
		                                                <el-button type="default" size="small" @click="addRule()">添加</el-button>
		                                            </div>
		                                        </el-col>
		                                    </el-row>
		                                    <el-row :gutter="4">
		                                        <el-col :span="24">
		                                        <el-scrollbar  height="150px">
		                                            <el-table ref="paramRulesTable" :data="businessFlowNodeInfo.preCfgJson.paramsRule" stripe style="width: 100%">
		                                                <el-table-column fixed="left" label="操作" width="60">
		                                                    <template  #default="scope">
		                                                        <el-button link type="primary" size="small" @click.prevent="deleteRule(scope.$index)" >X</el-button>
		                                                    </template>
		                                                    
		                                                </el-table-column>
		                                                <el-table-column prop="fromObjectType" label="来源" width="90" >
		                                                    <template  #default="scope">
		                                                        <div style="display: flex; align-items: center">
		                                                            <span v-if="scope.row.fromObjectType == 'I'">输入参数</span>
		                                                            <span v-if="scope.row.fromObjectType == 'P'">过程数据</span>
		                                                            <span v-if="scope.row.fromObjectType == 'D'">领域对象</span>
		                                                            <span v-if="scope.row.fromObjectType == 'F'">固定值</span>
		                                                        </div>
		                                                    </template>
		                                                </el-table-column>
		                                                <el-table-column prop="fromObjectKey" label="源对象Key" width="160" />
		                                                <el-table-column prop="isListType" label="是否列表" width="80" >
		                                                    <template  #default="scope">
		                                                        <div style="display: flex; align-items: center">
		                                                            <span v-if="scope.row.isListType == true">是</span>
		                                                            <span v-if="scope.row.isListType != true">否</span>
		                                                        </div>
		                                                    </template>
		                                                </el-table-column>
		                                                <el-table-column prop="attrMappings" label="映射规则" width="580" />
		                                            </el-table>
		                                        </el-scrollbar>
		                                        </el-col>
		                                    </el-row>
		                                </el-col>
		                            </el-row>
		                            
		                            <el-row :gutter="4">
		                                <el-col :span="24">
		                                    <el-row :gutter="4">
		                                        <el-col :span="24">
		                                            <el-divider style="margin-top: 15px;margin-bottom: 15px" ><span style="color:#3B85C8">节点后处理</span></el-divider>
		                                        </el-col>
		                                    </el-row>
		                                    <!-- 作为过程数据输出 -->
		                                    <el-row :gutter="4">
		                                        <el-col :span="2">
		                                            &nbsp;
		                                        </el-col>
		                                        <el-col :span="22">
		                                            <el-checkbox v-model="businessFlowNodeInfo.postCfgJson.asInProcessData" label="作为过程数据输出" />
		                                        </el-col>
		                                    </el-row>
		                                    <div v-if="businessFlowNodeInfo.postCfgJson.asInProcessData == true">
		                                    <el-row :gutter="4">
		                                        <el-col :span="16">
		                                            <el-form-item label="存储关键字">
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.inProcessDataKey" size="small" />
		                                            </el-form-item>
		                                        </el-col>
                                                <el-col :span="8"></el-col>
		                                    </el-row>
		                                    <el-row :gutter="4" >
		                                        <el-col :span="16">
		                                            <el-form-item label="转换规则">
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData" type="hidden" readonly size="small" />
		                                                <el-input v-model="dataMappingName_inProcessData" readonly size="small" />                                                
		                                            </el-form-item>
		                                        </el-col>
		                                        <el-col :span="8">
		                                            <el-form-item label="" label-width="10px">
		                                                <el-link  type="primary" @click="selectDataMapping('I')" ><label>设置</label></el-link>&nbsp;
		                                                <el-link  type="primary" @click="clearDataMapping('I')" ><label>清空</label></el-link>                                                
		                                            </el-form-item>
		                                        </el-col>
		                                    </el-row>
		                                    </div>
		                                    <!-- 作为领域对象输出 -->
		                                    <el-row :gutter="4">
		                                        <el-col :span="2">
		                                            &nbsp;
		                                        </el-col>
		                                        <el-col :span="22">
		                                            <el-checkbox v-model="businessFlowNodeInfo.postCfgJson.asDomainObjectValue" label="作为领域对象输出" />
		                                        </el-col>
		                                    </el-row>
		                                    <div v-if="businessFlowNodeInfo.postCfgJson.asDomainObjectValue == true">
		                                    <el-row :gutter="4">
		                                        <el-col :span="16">
		                                            <el-form-item label="存储关键字">
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.domainObjectValueKey" size="small" />
		                                            </el-form-item>
		                                        </el-col>
                                                <el-col :span="8"></el-col>
		                                    </el-row>
		                                   <el-row :gutter="4" >
		                                        <el-col :span="16">
		                                            <el-form-item label="转换规则">
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue" type="hidden" readonly size="small" />
		                                                <el-input v-model="dataMappingName_domainObjectValue" readonly size="small" /> 
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.domainObjectId" type="hidden" readonly size="small" />                                               
		                                            </el-form-item>
		                                        </el-col>
		                                        <el-col :span="8">
		                                            <el-form-item label="" label-width="10px">
		                                                <el-link  type="primary" @click="selectDataMapping('D')" ><label>设置</label></el-link>&nbsp;
		                                                <el-link  type="primary" @click="clearDataMapping('D')" ><label>清空</label></el-link>                                                
		                                            </el-form-item>
		                                        </el-col>
		                                    </el-row>
		                                    </div>
		                                    <!-- 作为业务流结果输出 -->
		                                    <el-row :gutter="4">
		                                        <el-col :span="2">
		                                            &nbsp;
		                                        </el-col>
		                                        <el-col :span="22">
		                                            <el-checkbox v-model="businessFlowNodeInfo.postCfgJson.asBusinessProcessResult" label="作为业务流结果输出" />
		                                        </el-col>
		                                    </el-row>
		                                    <div v-if="businessFlowNodeInfo.postCfgJson.asBusinessProcessResult == true">
		                                    <el-row :gutter="4" >
		                                        <el-col :span="16">
		                                            <el-form-item label="转换规则">
		                                                <el-input v-model="businessFlowNodeInfo.postCfgJson.dataMappingId_Result" type="hidden" readonly size="small" />  
		                                                <el-input v-model="dataMappingName_Result" readonly size="small" />                                                
		                                            </el-form-item>
		                                        </el-col>
		                                        <el-col :span="8">
		                                            <el-form-item label="" label-width="10px">
		                                                <el-link  type="primary" @click="selectDataMapping('R')" ><label>设置</label></el-link>&nbsp;
		                                                <el-link  type="primary" @click="clearDataMapping('R')" ><label>清空</label></el-link>                                                
		                                            </el-form-item>
		                                        </el-col>
		                                    </el-row>
		                                    </div>
		                                </el-col>
		                            </el-row>
		                       </div>  
		                    </el-col>
                        </el-row>
                    </el-scrollbar>
                    </el-col>
                </el-row>
            </el-form>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="hideBusinessFlowEditPage()">取消</el-button>
        <el-button type="primary" @click="saveBusinessFlow()">保存</el-button>
      </div>
    </template>
</el-dialog>

<!-- 选择数据映射信息 -->
<dataMappingSelect v-if="showDataMappingSelect==true && dataMappingsForSelect != null" :dataMappingSelectType="dataMappingSelectType"  @dataMappingSelected="dataMappingSelected" @hideDataMappingSelectPage="hideDataMappingSelectPage" :showDataMappingSelect="showDataMappingSelect"  :dataMappingsForSelect="dataMappingsForSelect"/>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect==true && managedObjectsForSelect != null"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

<!-- 可用数据辅助选择 -->
<availableDataSelect v-if="showAvailableDataSelect == true" :operation="businessFlowInfo.operation" :params="businessFlowInfo.operationParams" :ownerAttributes="businessFlowInfo.ownerAttributes" :domainObjectValues="domainObjectValues" :inProcessDatas="inProcessDatas" :loginSessionKey="loginSessionKey" :showAvailableDataSelect="showAvailableDataSelect" @hideDataSelectPage="onAvailableDataSuggestHide" @selectAvailabeData="selectAvailabeData" :contextTypes="contextTypeOptions"/>
											   
</template>

<script >
//import $ from 'jquery';
import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';
//import 'bpmn-js-properties-panel/dist/assets/properties-panel.css'
import axiosClient from '/src/js/utils/axios.js';
import { ref, markRaw, defineAsyncComponent} from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import CustomModeler from '../../../bpmn/js/customModeler';

//import dataMappingSelect from '../domainModel/workbench-dataMappingSelect.vue'
const dataMappingSelect = defineAsyncComponent(() => import('../domainModel/workbench-dataMappingSelect.vue'));
//import domainObjectSelect from '../domainModel/workbench-domainObjectSelect.vue'
const domainObjectSelect = defineAsyncComponent(() => import('../domainModel/workbench-domainObjectSelect.vue'));
//import availableDataSuggest from './workbench-businessFlow-availableDataSuggest.vue';
const availableDataSuggest = defineAsyncComponent(() => import('./workbench-businessFlow-availableDataSuggest.vue'));

const availableDataSelect = defineAsyncComponent(() => import('./workbench-businessFlow-dataSelect.vue'));

//import nodeCfgEditORMRepositoryNode from './flowNode/workbench-nodeCfgEdit-ORMRepositoryNode.vue'
const nodeCfgEditORMRepositoryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ORMRepositoryNode.vue'));
//import nodeCfgEditSqlQueryNode from './flowNode/workbench-nodeCfgEdit-SqlQueryNode.vue'
const nodeCfgEditSqlQueryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-SqlQueryNode.vue'));
//import nodeCfgEditESQueryNode from './flowNode/workbench-nodeCfgEdit-ESQueryNode.vue'
const nodeCfgEditESQueryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ESQueryNode.vue'));
//import nodeCfgEditTransferNode from './flowNode/workbench-nodeCfgEdit-TransferNode.vue'
const nodeCfgEditTransferNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-TransferNode.vue'));
//import nodeCfgEditLocalInvokeNode from './flowNode/workbench-nodeCfgEdit-LocalInvokeNode.vue'
const nodeCfgEditLocalInvokeNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-LocalInvokeNode.vue'));
//import nodeCfgEditRestInvokeNode from './flowNode/workbench-nodeCfgEdit-RestInvokeNode.vue'
const nodeCfgEditRestInvokeNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-RestInvokeNode.vue'));
//import nodeCfgEditReturnNode from './flowNode/workbench-nodeCfgEdit-ReturnNode.vue'
const nodeCfgEditReturnNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ReturnNode.vue'));
//import nodeCfgEditBreakNode from './flowNode/workbench-nodeCfgEdit-BreakNode.vue'
const nodeCfgEditBreakNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-BreakNode.vue'));
//import nodeCfgEditForEachNode from './flowNode/workbench-nodeCfgEdit-ForEachNode.vue'
const nodeCfgEditForEachNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ForEachNode.vue'));
//import nodeCfgEditConditionNode from './flowNode/workbench-nodeCfgEdit-ConditionNode.vue'
const nodeCfgEditConditionNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ConditionNode.vue'));
//import nodeCfgEditSpringElNode from './flowNode/workbench-nodeCfgEdit-SpringElNode.vue'
const nodeCfgEditSpringElNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-SpringElNode.vue'));
//import nodeCfgEditNativeJavaInvokeNode from './flowNode/workbench-nodeCfgEdit-NativeJavaInvokeNode.vue'
const nodeCfgEditNativeJavaInvokeNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-NativeJavaInvokeNode.vue'));
//import nodeCfgEditObjectSetterNode from './flowNode/workbench-nodeCfgEdit-ObjectSetterNode.vue'
const nodeCfgEditObjectSetterNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ObjectSetterNode.vue'));
//import nodeCfgEditValidateNode from './flowNode/workbench-nodeCfgEdit-ValidateNode.vue'
const nodeCfgEditValidateNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ValidateNode.vue'));
//import nodeCfgEditExceptionNode from './flowNode/workbench-nodeCfgEdit-ExceptionNode.vue'
const nodeCfgEditExceptionNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ExceptionNode.vue'));
//import nodeCfgEditDynamicGetterNode from './flowNode/workbench-nodeCfgEdit-DynamicGetterNode.vue'
const nodeCfgEditDynamicGetterNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DynamicGetterNode.vue'));
//import nodeCfgEditFtpRepositoryNode from './flowNode/workbench-nodeCfgEdit-FtpRepositoryNode.vue'
const nodeCfgEditFtpRepositoryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-FtpRepositoryNode.vue'));
//import nodeCfgEditExcelImportNode from './flowNode/workbench-nodeCfgEdit-ExcelImportNode.vue'
const nodeCfgEditExcelImportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ExcelImportNode.vue'));
//import nodeCfgEditExcelExportNode from './flowNode/workbench-nodeCfgEdit-ExcelExportNode.vue'
const nodeCfgEditExcelExportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ExcelExportNode.vue'));
//import nodeCfgEditWorkflowProcessOperatorNode from './flowNode/workbench-nodeCfgEdit-WorkflowProcessOperatorNode.vue'
const nodeCfgEditWorkflowProcessOperatorNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-WorkflowProcessOperatorNode.vue'));
//import nodeCfgEditWorkflowTaskQueryNode from './flowNode/workbench-nodeCfgEdit-WorkflowTaskQueryNode.vue'
const nodeCfgEditWorkflowTaskQueryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-WorkflowTaskQueryNode.vue'));
//import nodeCfgEditWorkflowTaskOperatorNode from './flowNode/workbench-nodeCfgEdit-WorkflowTaskOperatorNode.vue'
const nodeCfgEditWorkflowTaskOperatorNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-WorkflowTaskOperatorNode.vue'));
//import nodeCfgEditHashUtilsNode from './flowNode/workbench-nodeCfgEdit-HashUtilsNode.vue'
const nodeCfgEditHashUtilsNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-HashUtilsNode.vue'));
//import nodeCfgEditRedisRepositoryNode from './flowNode/workbench-nodeCfgEdit-RedisRepositoryNode.vue'
const nodeCfgEditRedisRepositoryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-RedisRepositoryNode.vue'));
//import nodeCfgEditLocalCacheNode from './flowNode/workbench-nodeCfgEdit-LocalCacheNode.vue'
const nodeCfgEditLocalCacheNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-LocalCacheNode.vue'));
//import nodeCfgEditMinioRepositoryNode from './flowNode/workbench-nodeCfgEdit-MinioRepositoryNode.vue'
const nodeCfgEditMinioRepositoryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-MinioRepositoryNode.vue'));
//import nodeCfgEditKafkaMessageSendNode from './flowNode/workbench-nodeCfgEdit-KafkaMessageSendNode.vue'
const nodeCfgEditKafkaMessageSendNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-KafkaMessageSendNode.vue'));
//import nodeCfgEditJSONImportNode from './flowNode/workbench-nodeCfgEdit-JSONImportNode.vue'
const nodeCfgEditJSONImportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-JSONImportNode.vue'));
//import nodeCfgEditJSONExportNode from './flowNode/workbench-nodeCfgEdit-JSONExportNode.vue'
const nodeCfgEditJSONExportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-JSONExportNode.vue'));
//import nodeCfgEditTextImportNode from './flowNode/workbench-nodeCfgEdit-TextImportNode.vue'
const nodeCfgEditTextImportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-TextImportNode.vue'));
//import nodeCfgEditTextExportNode from './flowNode/workbench-nodeCfgEdit-TextExportNode.vue'
const nodeCfgEditTextExportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-TextExportNode.vue'));
//import nodeCfgEditMailSendNode from './flowNode/workbench-nodeCfgEdit-MailSendNode.vue'
const nodeCfgEditMailSendNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-MailSendNode.vue'));
//import nodeCfgEditSmsSendNode from './flowNode/workbench-nodeCfgEdit-SmsSendNode.vue'
const nodeCfgEditSmsSendNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-SmsSendNode.vue'));
//import nodeCfgEditTypeConvertNode from './flowNode/workbench-nodeCfgEdit-TypeConvertNode.vue'
const nodeCfgEditTypeConvertNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-TypeConvertNode.vue'));
//import nodeCfgEditDynamicExcelExportNode from './flowNode/workbench-nodeCfgEdit-DynamicExcelExportNode.vue'
const nodeCfgEditDynamicExcelExportNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DynamicExcelExportNode.vue'));
//import nodeCfgEditDynamicValidateNode from './flowNode/workbench-nodeCfgEdit-DynamicValidateNode.vue'
const nodeCfgEditDynamicValidateNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DynamicValidateNode.vue'));
//import nodeCfgEditDynamicTransferNode from './flowNode/workbench-nodeCfgEdit-DynamicTransferNode.vue'
const nodeCfgEditDynamicTransferNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DynamicTransferNode.vue'));
//import nodeCfgEditDynamicLocalInvokeNode from './flowNode/workbench-nodeCfgEdit-DynamicLocalInvokeNode.vue'
const nodeCfgEditDynamicLocalInvokeNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DynamicLocalInvokeNode.vue'));
//import nodeCfgEditZipUtilsNode from './flowNode/workbench-nodeCfgEdit-ZipUtilsNode.vue'
const nodeCfgEditZipUtilsNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-ZipUtilsNode.vue'));
//import nodeCfgEditDictRepositoryNode from './flowNode/workbench-nodeCfgEdit-DictRepositoryNode.vue'
const nodeCfgEditDictRepositoryNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-DictRepositoryNode.vue'));
//import nodeCfgEditLocalFileNode from './flowNode/workbench-nodeCfgEdit-LocalFileNode.vue'
const nodeCfgEditLocalFileNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-LocalFileNode.vue'));

const nodeCfgEditCipherUtilsNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-CipherUtilsNode.vue'));

const nodeCfgEditSignatureUtilsNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-SignatureUtilsNode.vue'));

const nodeCfgEditAigcInvokeNode = defineAsyncComponent(() => import('./flowNode/workbench-nodeCfgEdit-AigcInvokeNode.vue'));

export default {
    props: ['businessFlowInfo','showBusinessFlowEdit'],
    
    emits: ['hideBusinessFlowEditPage'],
        
    setup (props, {attrs, emit, slots}) {
        const hideBusinessFlowEditPage = ()=> {
            emit('hideBusinessFlowEditPage');
        };
        
        return {
        	hideBusinessFlowEditPage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showBusinessFlowEdit;
            },
            set(value) {
                this.hideBusinessFlowEditPage();
            }
        }
    },
    components: {
    	dataMappingSelect,
    	domainObjectSelect,
    	availableDataSuggest,
        availableDataSelect,
    	
    	nodeCfgEditORMRepositoryNode,
    	nodeCfgEditSqlQueryNode,
    	nodeCfgEditESQueryNode,
    	nodeCfgEditTransferNode,
    	nodeCfgEditLocalInvokeNode,
    	nodeCfgEditRestInvokeNode,
    	nodeCfgEditReturnNode,
    	nodeCfgEditBreakNode,
    	nodeCfgEditForEachNode,
    	nodeCfgEditConditionNode,
    	nodeCfgEditSpringElNode,
    	nodeCfgEditNativeJavaInvokeNode,
    	nodeCfgEditObjectSetterNode,
    	nodeCfgEditValidateNode,
    	nodeCfgEditExceptionNode,
    	nodeCfgEditDynamicGetterNode,
    	nodeCfgEditFtpRepositoryNode,
    	nodeCfgEditExcelImportNode,
    	nodeCfgEditExcelExportNode,
    	nodeCfgEditWorkflowProcessOperatorNode,
    	nodeCfgEditWorkflowTaskQueryNode,
    	nodeCfgEditWorkflowTaskOperatorNode,
    	nodeCfgEditHashUtilsNode,
    	nodeCfgEditRedisRepositoryNode,
    	nodeCfgEditLocalCacheNode,
    	nodeCfgEditMinioRepositoryNode,
    	nodeCfgEditKafkaMessageSendNode,
    	nodeCfgEditJSONImportNode,
    	nodeCfgEditJSONExportNode,
    	nodeCfgEditTextImportNode,
    	nodeCfgEditTextExportNode,
    	nodeCfgEditMailSendNode,
    	nodeCfgEditSmsSendNode,
    	nodeCfgEditTypeConvertNode,
        nodeCfgEditDynamicExcelExportNode,
        nodeCfgEditDynamicValidateNode,
        nodeCfgEditDynamicTransferNode,
        nodeCfgEditDynamicLocalInvokeNode,
        nodeCfgEditZipUtilsNode,
        nodeCfgEditDictRepositoryNode,
        nodeCfgEditLocalFileNode,
        nodeCfgEditCipherUtilsNode,
        nodeCfgEditSignatureUtilsNode,
        nodeCfgEditAigcInvokeNode
    },
   
    data() {            
    	const flowTypeOptions = ref(null);
        
        const yesOrNoOptions = ref(null);
        
        const toObjectTypeOptions = ref(null);
        const objectSourceOptions = ref(null);
        
        const businessFlowNodeInfo = ref({});
        const currentBpmnTaskNode = null;
        
        const showDataMappingSelect = ref(false);
        const dataMappingsForSelect = ref({});
        const dataMappingSelectType = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        
        const showNodeCfgEdit = ref(null);
        const nodeCfgJson = ref(null);       
        const nodeCfgEditInit = ref(false);
        
        const fromObjectType = ref('');
        const isListType = ref(false);
        const fromObjectKey = ref('');
        const attrMappings = ref('[{"fromAttrPath":"","toObjectAttr":""}]');
        
        const dataMappingName_Result = ref('');
        const dataMappingName_domainObjectValue = ref('');
        const dataMappingName_inProcessData = ref('');
        
        const showAvailableDataSuggest = ref(false);
        const showAvailableDataSelect = ref(false);
        const inProcessDatas = ref([]);
        const domainObjectValues = ref([]);
        const loginSessionKey = ref(null);
        const contextTypeOptions = ref(null);
        const dataSelectedHandler = null;

        const validateRules = ref({
            "businessFlow.id": [
                { required: true, message: '业务流标识不能为空！', trigger: 'blur' }
            ],
            "businessFlow.name": [
                { required: true, message: '业务流名称不能为空！', trigger: 'blur' }
            ],
            "businessFlow.transactionRequired": [
                { required: true, message: '加入事务不能为空！', trigger: 'blur' }
            ],
            "businessFlow.flowType": [
                { required: true, message: '业务流类型不能为空！', trigger: 'blur' }
            ]
        });

        const dbSchemas = ref(null);
        const flowNodes = ref(null);
        const flowNodeTypes = ref(null);
        const flowNodeTree = ref([]);
        
        return {
        	flowTypeOptions,
            yesOrNoOptions,
            toObjectTypeOptions,
            objectSourceOptions,
            
            businessFlowNodeInfo,
            currentBpmnTaskNode,
            bpmnContainer: null,
            modeler: null,
            eventBus:null,
            elementRegistry:null,
            modeling:null,
            showDataMappingSelect,
            dataMappingsForSelect,
            dataMappingSelectType,
            nodeCfgJson,
            showNodeCfgEdit,
            showDomainObjectSelect,
            managedObjectsForSelect,
            
            fromObjectType,
            isListType,
            fromObjectKey,
            attrMappings,
            
            dataMappingName_Result,
            dataMappingName_domainObjectValue,
            dataMappingName_inProcessData,
            
            showAvailableDataSuggest,
            showAvailableDataSelect,
            dataSelectedHandler,
            contextTypeOptions,
            inProcessDatas,
            domainObjectValues,
            loginSessionKey,
            validateRules,

            dbSchemas,
            flowNodes,
            flowNodeTypes,
            flowNodeTree,
            nodeCfgEditInit
        }
    },
    watch: {
    	'businessFlowNodeInfo.flowNodeId': function(val, old){
    		if(val == null || old == null) {
    			return;
    		}
    		if(val == old) {
    			return;
    		}
    		if(this.nodeCfgEditInit) {//忽略业务流节点信息编辑初始化后的第一次事件
    			this.nodeCfgEditInit = false;
    			return;
    		}
    		var clearFlag = false;
    		if(val != old) {
    			clearFlag = true;
    		}
    		this.updateBpmnNodeName();
    		
    		this.handleFlowNodeChange(clearFlag, "watch event");
        },
    	'businessFlowNodeInfo.name': function(val, old){
    		if(val == null || old == null) {
                return;
            }
    		this.updateBpmnNodeName();
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['BUSINESS_FLOW_TYPE','PUB_YES_OR_NO','SUPPORT_OBJECT_TYPE','OBJECT_SOURCE_TYPE','FLOW_NODE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.flowTypeOptions = data.data['BUSINESS_FLOW_TYPE'];
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.toObjectTypeOptions = data.data['SUPPORT_OBJECT_TYPE'];
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE'];
                this.flowNodeTypes = data.data['FLOW_NODE_TYPE'];
            }

            var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/list/" + this.businessFlowInfo.businessFlow.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dbSchemas = data.data;

                    var url1 = "/lcdp-proxy/lowcode/platform/be/api/flowNode/list/" + this.businessFlowInfo.businessFlow.systemId;
                    axiosClient.get(url1).then((response) => {
                        var data1 = response.data; 
                        if(data1 != null && data1.status == "200" && data1.data != null) {
                            this.flowNodes = data1.data;
                            var flowNodeMap = {};
                            if(this.flowNodes != null && this.flowNodes.length > 0) {
                                for(var i in this.flowNodes) {
                                    if(flowNodeMap[this.flowNodes[i].type] == null) {
                                        flowNodeMap[this.flowNodes[i].type] = [];
                                    }
                                    flowNodeMap[this.flowNodes[i].type].push(this.flowNodes[i]);
                                }
                            }
                            if(this.flowNodeTypes != null && this.flowNodeTypes.length > 0) {
                                for(var i in this.flowNodeTypes) {
                                    var flowNodeType = {};
                                    flowNodeType.value = this.flowNodeTypes[i].value;
                                    flowNodeType.label = this.flowNodeTypes[i].label;
                                    flowNodeType.children = flowNodeMap[flowNodeType.value];
                                    this.flowNodeTree.push(flowNodeType);
                                }
                            }
                            var nodeList = [];
                            var subFlows = {};
                            this.processBusinessNodesOfResult(this.businessFlowInfo, nodeList, subFlows);
                            this.businessFlowInfo.allBusinessFlowNode = nodeList;
                            this.businessFlowInfo.allSubBusinessFlow = subFlows; 
                            this.businessFlowNodeInfo = null;
                            this.currentBpmnTaskNode = null;
                            setTimeout(()=>{
                                this.showBpmnProcess();
                            },100);
                        }
                    });
                }
            });
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        
    },     
    methods: {
    	updateNodeCfgJson(json) {
    		this.businessFlowNodeInfo.nodeCfg = JSON.stringify(json);
    		this.businessFlowNodeInfo.nodeCfgJson = json;
    	},
    	clearDataMapping(type){
    		if('R' == type) {
    			this.businessFlowNodeInfo.postCfgJson.dataMappingId_Result = "";
    			this.dataMappingName_Result = "";
    		}else if('D' == type) {
    			this.businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue = "";
    			this.dataMappingName_domainObjectValue = "";
    			this.businessFlowNodeInfo.postCfgJson.subDataMappings_domainObjectValue = "";
    			this.businessFlowNodeInfo.postCfgJson.domainObjectId = "";
            }else if('I' == type) {
            	this.businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData = "";
            	this.dataMappingName_inProcessData = "";
            }
    		
    	},
    	selectDataMapping(type) {
    		if(this.businessFlowNodeInfo == null) {
    			return;
    		}
    		if(this.businessFlowNodeInfo.nodeCfgJson == null 
    				|| this.businessFlowNodeInfo.nodeCfgJson.nodeResultType == null
    				|| this.businessFlowNodeInfo.nodeCfgJson.nodeResultClass == null
    				|| this.businessFlowNodeInfo.nodeCfgJson.nodeResultClass.trim().length==0) {
    			ElMessage.error(`请先设置节点的输出结果类型！`);
                return;
            }
    		if(this.businessFlowNodeInfo.nodeCfgJson.nodeResultType != 'D' &&
                    this.businessFlowNodeInfo.nodeCfgJson.nodeResultType != 'V') {
                ElMessage.error(`请先设置节点的输出结果不是管理对象类型（领域对象或值传递对象），无法设置数据映射规则！`);
                return;
            }
    		var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/listByFromObject/" + this.businessFlowNodeInfo.nodeCfgJson.nodeResultType + "/" + this.businessFlowNodeInfo.nodeCfgJson.nodeResultClass;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataMappingsForSelect = data.data;
                    this.dataMappingSelectType = type;
                    this.showDataMappingSelect = true;
                }
            });
    	},
    	hideDataMappingSelectPage() {
    		this.showDataMappingSelect = false;
    		this.dataMappingsForSelect = null;
    		this.dataMappingSelectType = null;
    	},
    	dataMappingSelected(mappings, type) {
    		if('R' == type) {
                this.businessFlowNodeInfo.postCfgJson.dataMappingId_Result = mappings.id;
                this.dataMappingName_Result = mappings.name;
            }else if('D' == type) {
                this.businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue = mappings.id;
                this.dataMappingName_domainObjectValue = mappings.name;
                this.businessFlowNodeInfo.postCfgJson.domainObjectId = mappings.toObjectId;
            }else if('I' == type) {
                this.businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData = mappings.id;
                this.dataMappingName_inProcessData = mappings.name;
                this.businessFlowNodeInfo.postCfgJson.inProcessDataType = mappings.toObjectType;
                this.businessFlowNodeInfo.postCfgJson.inProcessDataClass = mappings.toObjectId;
            }
    		
    		this.showDataMappingSelect = false;
            this.dataMappingsForSelect = null;
            this.dataMappingSelectType = null;
    	},
    	showBpmnProcess() {
    		if(this.businessFlowInfo.bpmnXml != null) {//已有对应流程图
                this.initBpmnProcess();
                this.loadBpmnProcess();
            }else {
                this.initBpmnProcess();
                this.createBpmnProcess();
            }
    	},
    	handleHideBusinessFlowEdit() {
    		ElMessageBox.confirm(`Are you confirm to close ?`)
    		  .then(()=>{
    			  this.hideBusinessFlowEditPage();
    		  })
    		  .catch(()=>{
    				  
    		  });
    	},
    	saveBusinessFlow() {
    		//解析流程图
    		var mainFlow = this.analyzeBpmnProcess();
    		if(mainFlow == null) {
    			console.error("analyzeBpmnProcess failed!");
    			ElMessage.error(`解析BPMN流程信息失败！`);
    			return;
    		}
    		//根据解析后的流程图，处理提交后台的数据结构
    		var editBusinessFlowInfo = this.processBusinessFlowNodes(mainFlow);
    		//设置主业务流对象
    		editBusinessFlowInfo.businessFlow = this.businessFlowInfo.businessFlow;
    		try {
    			//从流程图中获取流程图对应的bpmn 2.0 xml字符串
                this.modeler.saveXML({ format: true }).then((data)=> {
                	//设置bpmn 2.0 xml字符串到对象中
                	editBusinessFlowInfo.bpmnXml = data.xml;
                	var url = "/lcdp-proxy/lowcode/platform/be/api/businessFlow/saveAll";            
                    //提交后台保存数据
                    this.$refs.businessFlowInfoForm.validate((valid, fields)=> {
                        if(valid) {
                            axiosClient.post(url,editBusinessFlowInfo).then((response) => {
                                var data = response.data; 
                                if(data != null && data.status == "200" && data.data != null && data.data.businessFlow != null && data.data.businessFlow.id != null) {
                                    ElMessage(`保存业务流信息成功`);
                                    this.businessFlowInfo.businessFlow = data.data.businessFlow;
                                    var nodeList = [];
                                    var subFlows = {};
                                    this.processBusinessNodesOfResult(data.data, nodeList, subFlows);
                                    this.businessFlowInfo.allBusinessFlowNode = nodeList;
                                    this.businessFlowInfo.allSubBusinessFlow = subFlows;
                                }
                            });
                        }else {
                            console.log("参数校验失败！", fields);
                        }
                    });
                    
                });
            } catch (err) {
                console.error('Error happened saving XML: ', err);
            }
        },
        initBpmnProcess() {
        	this.bpmnContainer = document.getElementById('businessFlowCanvas');
            this.modeler = markRaw(new CustomModeler({
                 container: this.bpmnContainer,
                 // 添加控制板
                 propertiesPanel: {
                     
                 }, 
                 // 右侧属性面板
                 additionalModules: [
                     
                 ],
                 moddleExtensions: {
                     
                 }
            }));
            this.eventBus = this.modeler.get('eventBus');
            this.elementRegistry = markRaw(this.modeler.get('elementRegistry'));
            this.modeling = markRaw(this.modeler.get('modeling'));
            /*
            const Logo = document.querySelector(".bjs-powered-by");
            while (Logo.firstChild) {
                Logo.removeChild(Logo.firstChild);
            }*/
        },
        loadBpmnProcess() {
            this.modeler.importXML(this.businessFlowInfo.bpmnXml).then(() => {
                this.modeler.get('canvas').zoom(0.7);
            }).catch((err)=> {
                console.log(err);
            });
            this.registeBpmnProcessEvent();
        },
        createBpmnProcess() {
        	this.modeler.createDiagram().then(() => {
            	this.modeler.get('canvas').zoom(0.7);
            	
            }).catch((err)=> {
                console.log(err);
            });
            this.registeBpmnProcessEvent();
        },
        registeBpmnProcessEvent() {
        	const that = this;
            var events = ['shape.removed','element.click','element.dblclick','shape.added'];
            this.eventBus.on(events, function(e) {
                const {element} = e;
                if(element.type == "bpmn:Task") {
                    that.currentBpmnTaskNode = element;  
                    that.editBusinessFlowNodeForBpmn(element.id);
                }else {
                    that.currentBpmnTaskNode = null;
                    that.businessFlowNodeInfo = {};
                }
            });
        },
        editBusinessFlowNodeForBpmn(bpmnNodeId) {
        	var exists = false;
            if(this.businessFlowInfo != null && this.businessFlowInfo.allBusinessFlowNode != null && this.businessFlowInfo.allBusinessFlowNode.length > 0) {
                for(var i in this.businessFlowInfo.allBusinessFlowNode) {
                	var item = this.businessFlowInfo.allBusinessFlowNode[i];
                	if(item.bpmnNodeId == bpmnNodeId) {
                        this.businessFlowNodeInfo = item;
                        if(this.businessFlowNodeInfo.preCfgJson != null && this.businessFlowNodeInfo.preCfgJson.paramsRule == null) {
                            this.businessFlowNodeInfo.preCfgJson.paramsRule = [];
                        }
                        exists = true;
                        break;
                    }
                }
            }
            if(!exists) {
            	this.businessFlowNodeInfo = {
                        id : "",
                        name : "",
                        flowNodeId : "",
                        businessFlowId : "",
                        nodeOrder : "",
                        nodePreCfg : "",
                        nodeCfg : "",
                        nodePostCfg : "",
                        bpmnNodeId : bpmnNodeId,
                        systemId: this.businessFlowInfo.businessFlow.systemId,
                        tenantId: this.businessFlowInfo.businessFlow.tenantId,
                        postCfgJson : {},
                        nodeCfgJson : {},
                        preCfgJson : {}
                }
            	this.businessFlowNodeInfo.postCfgJson.asDomainObjectValue = false;
            	this.businessFlowNodeInfo.postCfgJson.asInProcessData = false;
            	this.businessFlowNodeInfo.postCfgJson.asBusinessProcessResult = false;
            	this.businessFlowNodeInfo.nodeCfgJson.isListResult = false;
            	this.businessFlowNodeInfo.nodeCfgJson.isListType = false;
            	this.businessFlowNodeInfo.preCfgJson.paramsRule = [];
            	this.businessFlowInfo.allBusinessFlowNode.push(this.businessFlowNodeInfo);
            }
            this.nodeCfgEditInit = true;
            
            this.showNodeCfgEdit = null;
            this.nodeCfgJson = null;
            setTimeout(()=>{
            	this.handleFlowNodeChange(false,"editBusinessFlowNodeForBpmn");
            	setTimeout(()=>{
                    this.nodeCfgEditInit = false;
                },500);
            },100); 
            
        },
        handleFlowNodeChange(clearFlag, source) {//clearFlag:当节点类型发生变化是，清除数据的标志；source：触发源：editBusinessFlowNodeForBpmn
        	var nodeClass = "";
            if(this.businessFlowNodeInfo.flowNodeId != null && this.businessFlowNodeInfo.flowNodeId != "") {
                this.flowNodes.forEach((row)=>{
                    if(this.businessFlowNodeInfo.flowNodeId == row.id) {
                        nodeClass = row.nodeClass;
                        this.businessFlowNodeInfo.flowNode = row;
                        return;
                    }
                });
                
                if(source != null) {
                	var newNodeCfgJson = {};
                    if(clearFlag && this.nodeCfgJson != null) {                 
                        newNodeCfgJson.isListResult = this.nodeCfgJson.isListResult;
                        newNodeCfgJson.nodeResultType = this.nodeCfgJson.nodeResultType
                        newNodeCfgJson.nodeResultClass = this.nodeCfgJson.nodeResultClass;
                    }
                    
                    if(clearFlag) {  
                        this.nodeCfgJson = newNodeCfgJson;
                    }else {
                        this.nodeCfgJson = this.businessFlowNodeInfo.nodeCfgJson;
                    }
                }
                
                
                if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ORMRepositoryNode") {
                	this.showNodeCfgEdit = 'ORMRepositoryNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.SqlQueryNode") {
                	this.showNodeCfgEdit = 'SqlQueryNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ESQueryNode") {
                    this.showNodeCfgEdit = 'ESQueryNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.TransferNode") {
                    this.showNodeCfgEdit = 'TransferNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.LocalInvokeNode") {
                    this.showNodeCfgEdit = 'LocalInvokeNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.RestInvokeNode") {
                    this.showNodeCfgEdit = 'RestInvokeNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ReturnNode") {
                    this.showNodeCfgEdit = 'ReturnNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.BreakNode") {
                    this.showNodeCfgEdit = 'BreakNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ForEachNode") {
                    this.showNodeCfgEdit = 'ForEachNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ConditionNode") {
                    this.showNodeCfgEdit = 'ConditionNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.SpringELNode") {
                    this.showNodeCfgEdit = 'SpringElNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.NativeJavaInvokeNode") {
                    this.showNodeCfgEdit = 'NativeJavaInvokeNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ObjectSetterNode") {
                    this.showNodeCfgEdit = 'ObjectSetterNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ValidateNode") {
                    this.showNodeCfgEdit = 'ValidateNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ExceptionNode") {
                    this.showNodeCfgEdit = 'ExceptionNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DynamicGetterNode") {
                    this.showNodeCfgEdit = 'DynamicGetterNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.FtpRepositoryNode") {
                    this.showNodeCfgEdit = 'FtpRepositoryNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ExcelImportNode") {
                    this.showNodeCfgEdit = 'ExcelImportNode'; 
                    if(this.nodeCfgJson.columnMappings == null) {
                    	this.nodeCfgJson.columnMappings = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ExcelExportNode") {
                    this.showNodeCfgEdit = 'ExcelExportNode'; 
                    if(this.nodeCfgJson.columnMappings == null) {
                        this.nodeCfgJson.columnMappings = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.WorkflowProcessOperatorNode") {
                    this.showNodeCfgEdit = 'WorkflowProcessOperatorNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.WorkflowTaskQueryNode") {
                    this.showNodeCfgEdit = 'WorkflowTaskQueryNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.WorkflowTaskOperatorNode") {
                    this.showNodeCfgEdit = 'WorkflowTaskOperatorNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.HashUtilsNode") {
                    this.showNodeCfgEdit = 'HashUtilsNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.CipherUtilsNode") {
                    this.showNodeCfgEdit = 'CipherUtilsNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.SignatureUtilsNode") {
                    this.showNodeCfgEdit = 'SignatureUtilsNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.RedisRepositoryNode") {
                    this.showNodeCfgEdit = 'RedisRepositoryNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.LocalCacheNode") {
                    this.showNodeCfgEdit = 'LocalCacheNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.MinioRepositoryNode") {
                    this.showNodeCfgEdit = 'MinioRepositoryNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.KafkaMessageSendNode") {
                    this.showNodeCfgEdit = 'KafkaMessageSendNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.JSONImportNode") {
                    this.showNodeCfgEdit = 'JSONImportNode';
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.JSONExportNode") {
                    this.showNodeCfgEdit = 'JSONExportNode';
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.TextImportNode") {
                    this.showNodeCfgEdit = 'TextImportNode'; 
                    if(this.nodeCfgJson.columnMappings == null) {
                        this.nodeCfgJson.columnMappings = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.TextExportNode") {
                    this.showNodeCfgEdit = 'TextExportNode'; 
                    if(this.nodeCfgJson.columnMappings == null) {
                        this.nodeCfgJson.columnMappings = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.MailSendNode") {
                    this.showNodeCfgEdit = 'MailSendNode'; 
                    if(this.nodeCfgJson.contentParams == null) {
                        this.nodeCfgJson.contentParams = [];
                    } 
                    if(this.nodeCfgJson.attachments == null) {
                        this.nodeCfgJson.attachments = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.SmsSendNode") {
                    this.showNodeCfgEdit = 'SmsSendNode'; 
                    if(this.nodeCfgJson.contentParams == null) {
                        this.nodeCfgJson.contentParams = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.TypeConvertNode") {
                    this.showNodeCfgEdit = 'TypeConvertNode';
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DynamicExcelExportNode") {
                    this.showNodeCfgEdit = 'DynamicExcelExportNode'; 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DynamicValidateNode") {
                    this.showNodeCfgEdit = 'DynamicValidateNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DynamicTransferNode") {
                    this.showNodeCfgEdit = 'DynamicTransferNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DynamicLocalInvokeNode") {
                    this.showNodeCfgEdit = 'DynamicLocalInvokeNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.DictRepositoryNode") {
                    this.showNodeCfgEdit = 'DictRepositoryNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.AigcInvokeNode") {
                    this.showNodeCfgEdit = 'AigcInvokeNode';                 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.ZipUtilsNode") {
                    this.showNodeCfgEdit = 'ZipUtilsNode'; 
                    if(this.nodeCfgJson.srcFiles == null) {
                        this.nodeCfgJson.srcFiles = [];
                    } 
                }else if(nodeClass == "cn.zpaas.lowcode.be.engine.flow.node.LocalFileNode") {
                    this.showNodeCfgEdit = 'LocalFileNode';                 
                }else{
                    this.showNodeCfgEdit = null;
                }
                
                if(this.showNodeCfgEdit != null && this.businessFlowNodeInfo != null && this.businessFlowNodeInfo.postCfgJson != null) {
                	if(this.businessFlowNodeInfo.postCfgJson.dataMappingId_Result != null && this.businessFlowNodeInfo.postCfgJson.dataMappingId_Result != "") {
                		var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/queryName/" + this.businessFlowNodeInfo.postCfgJson.dataMappingId_Result;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.dataMappingName_Result = data.data;
                            }
                        });
                	}else {
                        this.dataMappingName_Result = "";
                    }
                	if(this.businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue != null && this.businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue != "") {
                        var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/queryName/" + this.businessFlowNodeInfo.postCfgJson.dataMappingId_domainObjectValue;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.dataMappingName_domainObjectValue = data.data;
                            }
                        });
                    }else {
                        this.dataMappingName_domainObjectValue = "";
                    }
                	if(this.businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData != null && this.businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData != "") {
                        var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/queryName/" + this.businessFlowNodeInfo.postCfgJson.dataMappingId_inProcessData;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.dataMappingName_inProcessData = data.data;
                            }
                        });
                    }else {
                        this.dataMappingName_inProcessData = "";
                    }
                }
                
            }else {
                this.showNodeCfgEdit = null;
                this.nodeCfgJson = null;
            }	
        },
        updateBpmnNodeName() {       	
        	if(this.currentBpmnTaskNode != null && this.businessFlowNodeInfo != null && this.businessFlowNodeInfo.bpmnNodeId != null) {
        		var flowNodeName = this.businessFlowNodeInfo.flowNodeId;
        		if(this.flowNodes != null && this.flowNodes.length > 0) {
        			for(var i in this.flowNodes) {
        				if(this.flowNodes[i].id == this.businessFlowNodeInfo.flowNodeId) {
        					flowNodeName = this.flowNodes[i].name;
        					break;
        				}
        			}
        		}
        		var node = this.elementRegistry.get(this.businessFlowNodeInfo.bpmnNodeId);
        		this.modeling.updateProperties(node, {name:this.businessFlowNodeInfo.name+'\n('+flowNodeName+')'});
        	}
        },
        analyzeBpmnProcess() {
        	//保存流程任务节点的Map，bpmn:Task，Key为节点的ID
        	var taskMap = {};
            //保存子流程节点的Map，bpmn:SubProcess，Key为节点的ID
            var subProcessMap = {};
            //保存结束节点的Map，bpmn:EndEvent，Key为节点的ID
            var endEventMap = {};
            //保存主流程的开始节点，作为主业务流解析处理的起点，bpmn:StartEvent
            var mainStartEvent;
            //保存子业务流的开始节点，作为子业务流解析处理的起点，bpmn:StartEvent，Key为对应子流程节点的ID
            var subStartEventMap;
            //保存连线的Map，bpmn:SequenceFlow，Key为连线源对象节点的ID，一个源对象节点可能会有多条连线
            var sequenceFlowMap = {};
            //主业务流的开始节点只能有一个，用于判断是否合法
            var mainStartEventCount = 0;
            //循环流程图中所有的图形
        	this.elementRegistry.forEach((shape, svgElement)=> {
        		if(shape.type == 'bpmn:Task') {//任务节点
        			taskMap[shape.id] = shape;
        		}else if(shape.type == 'bpmn:SequenceFlow') {//连线
        			if(sequenceFlowMap[shape.businessObject.sourceRef.id] == null) {
        				sequenceFlowMap[shape.businessObject.sourceRef.id] = [];
        			}
        			sequenceFlowMap[shape.businessObject.sourceRef.id].push(shape);
        		}else if(shape.type == 'bpmn:StartEvent') {//开始节点
        			if(shape.businessObject.$parent.$parent.$parent == null) {//主流程的开始节点
        				mainStartEvent = shape;
        				mainStartEventCount++;
                    }else {//子流程的开始节点
                    	if(subStartEventMap == null) {
                    		subStartEventMap = {};
                    	}
                    	if(subStartEventMap[shape.businessObject.$parent.id] != null) {//一个子流程节点只允许有一个开始节点
                    		console.error(shape.businessObject.$parent.id + "has more than one bpmn:StartEvent!");
                    		return null;
                    	}
        				subStartEventMap[shape.businessObject.$parent.id] = shape;
                    }       			
        		}else if(shape.type == 'bpmn:EndEvent') {//结束节点
        			endEventMap[shape.id] = shape;
        		}else if(shape.type == 'bpmn:SubProcess') {//子流程节点
        			subProcessMap[shape.id] = shape;
        		}else {//其他类型的图片目前不用处理
        			//do nothing
        		}        		
        	});
            
        	if(mainStartEventCount > 1) {//主流程只能有一个
                console.error("more than one main Flow found!");
                return;
            }
        	if(mainStartEvent == null) {//必须有主流程
        		console.error("no main Flow found!");
        		return;
        	}
        	//处理前面解析出来的各类节点，组装后台需要的数据格式
        	var mainFlow = this.processTaskList(mainStartEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap);        	
        	if(mainFlow == null) {
        		console.error('processTaskList failed!');
        	}
        	return mainFlow;
        },
        processTaskList(startEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap) {
        	var id = startEvent.id;//主业务流的开始节点作为处理的起点
        	var sequenceFlow =null;//连线变量
            var flowEnd = false;//流程结束标志
            
          //保存返回结果的变量，递归结构
          /* {
            	flowNodes:[{}],
            	subFlows:{
            		taskNodeId1:{
            			//递归结构
            		},
            		taskNodeId2:{
                        //递归结构
                    }
            	}
          } */
            var flow = {};
            flow.flowNodes = [];
            while(id != null) {//循环
                var sequenceFlows = sequenceFlowMap[id];//取出开始节点连接的连线
                if(sequenceFlows == null || sequenceFlows.length == 0) {//如果没有连线，直接报错
                    console.error("the shape(" + id + ") doesn't connect to any other shape");
                    ElMessage.error(`节点（` + id + `）未连接任何图形！`);
                    return null;
                }else {//有连线
                    var connectTasks = 0;//节点连接的bpmn:Task节点数
                    var connectSubProcesses = 0;//节点连接的bpmn:SubProcess节点数
                    for(var i=0; i<sequenceFlows.length; i++) {//循环所有的连线
                    	var item = sequenceFlows[i];
                    
                        if(taskMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:Task
                            connectTasks++;//计数加1
                            flow.flowNodes.push(taskMap[item.businessObject.targetRef.id]);//将连接的任务节点加入flow.flowNodes
                            id = item.businessObject.targetRef.id;//更新id的值为该任务节点的ID，触发下个循环，处理该任务节点
                        }else if(subProcessMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:SubProcess                      	
                            connectSubProcesses++;//计数加1
                            var subStartEvent = subStartEventMap[item.businessObject.targetRef.id];//取出该子流程节点对应的开始节点
                            if(subStartEvent == null) {//如果子流程对应的开始节点为空，直接报错
                            	console.error('cannot find StartEvent of SubProcess:'+item.businessObject.targetRef.id);
                            	ElMessage.error(`子流程节点（` + item.businessObject.targetRef.id + `）未配置开始节点！`);
                            	return null;
                            }
                            //递归调用，处理子业务流
                            var subFlow = this.processTaskList(subStartEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap);//递归处理子流程
                            if(subFlow == null) {//如果返回的子业务流对象为空，则表示子业务流处理失败，直接返回null
                            	console.error('process task list of sub process failed:' + item.businessObject.targetRef.id);
                            	return null;
                            }
                            if(flow.subFlows == null) {//初始化子业务流属性，key为子流程节点对应的任务节点标识（目前只有条件分支任务节点和循环任务节点，业务流节点类型）
                            	flow.subFlows = {};
                            }
                            //将处理好的子业务流信息设置到flow.subFlows中
                            flow.subFlows[item.businessObject.sourceRef.id] = subFlow;
                        }else if(endEventMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:EndEvent
                            flowEnd = true;//设置流程结束标志
                        }else {//connect to other shape
                            //do nothing
                        }
                    
                    }
                    if(flowEnd && connectTasks > 0) {//connect to both of bpmn:EndEvent and bpmn:Task
                        console.error("can't connect to both of bpmn:EndEvent and bpmn:Task!");
                        ElMessage.error(`节点（` + id + `）同时连接了任务节点和结束节点！`);
                        return null;
                    }
                    if(connectSubProcesses > 1) {//connect to more than one bpmn:SubProcess
                        console.error("can't connect to more than one bpmn:SubProcess!");
                        ElMessage.error(`节点（` + id + `）连接了超过1个子流程节点！`);
                        return null;
                    }
                    
                    if(flowEnd) {//main flow finished.
                        break;
                    }
                    if(connectTasks > 1) {//connect to more than one bpmn:Task
                        console.error("can't connect to more than one bpmn:Task!");
                        ElMessage.error(`节点（` + id + `）节点连接了超过1个任务节点！`);
                        return null;
                    } 
                    if(connectTasks < 1) {//not connect to any bpmn:Task or bpmn:EndEvent
                        console.error("not connect to any bpmn:Task or bpmn:EndEvent!");
                        ElMessage.error(`节点（` + id + `）节点未连接任何任务节点或结束结节！`);
                        return null;
                    }
                    //进入下一次循环，处理连接的下一个任务节点
                }
            }
            return flow;
        },
        processBusinessFlowNodes(mainFlow) {
        	//提交后台处理的最终数据格式
        	/* {
        		businessFlowNodes:[{}],
        		subBusinessFlows: {
        			parentNodeId1:{
        				//递归结构
        			},
        			parentNodeId2:{
                        //递归结构
                    }
        		}
        	} */
        	var editBusinessFlowInfo = {};
        	
        	//按流程图中任务节点的顺序将业务流节点加入到数组中，根据顺序设置nodeOrder
            if(mainFlow.flowNodes != null && mainFlow.flowNodes.length > 0) {
                editBusinessFlowInfo.businessFlowNodes = [];
                var nodeOrder = 0;
                mainFlow.flowNodes.forEach((flowNode) => {
                    nodeOrder++;
                    this.businessFlowInfo.allBusinessFlowNode.forEach((row) => {
                        if(row.bpmnNodeId == flowNode.id) {
                            row.nodeOrder = nodeOrder;//设置nodeOrder
                            row.nodePostCfg = JSON.stringify(row.postCfgJson);
                            
                            row.nodePreCfg = JSON.stringify(row.preCfgJson);
                            delete row.postCfgJson;
                            delete row.preCfgJson;
                            delete row.nodeCfgJson;
                            editBusinessFlowInfo.businessFlowNodes.push(row);
                            return;
                        }
                    });
                });
            }
            //按递归的方式将子业务流加入Map中
            if(mainFlow.subFlows != null) {
                editBusinessFlowInfo.subBusinessFlowMap = {};
                for(var parentNodeId in mainFlow.subFlows) {
                    if(mainFlow.subFlows[parentNodeId] !=null) {
                        editBusinessFlowInfo.subBusinessFlowMap[parentNodeId] = this.processBusinessFlowNodes(mainFlow.subFlows[parentNodeId]);
                        if(this.businessFlowInfo.allSubBusinessFlow[parentNodeId] != null) {
                        	editBusinessFlowInfo.subBusinessFlowMap[parentNodeId].businessFlow = this.businessFlowInfo.allSubBusinessFlow[parentNodeId];
                        }
                    }
                }
            }
            return editBusinessFlowInfo;
        },
        processBusinessNodesOfResult(result, nodeList, subFlows) {
        	//节点列表为空时，直接返回
        	if(result.businessFlowNodes == null || result.businessFlowNodes.length == 0) {
        		return;
        	}
        	var validateNodeId = null;
        	var transferNodeId = null;
        	this.flowNodes.forEach((row)=>{
                if("cn.zpaas.lowcode.be.engine.flow.node.ValidateNode" == row.nodeClass) {
                	validateNodeId = row.id;
                    return;
                }
                if("cn.zpaas.lowcode.be.engine.flow.node.TransferNode" == row.nodeClass) {
                	transferNodeId = row.id;
                    return;
                }
            });
        	//将业务流节点加入列表
        	for(var row in result.businessFlowNodes) {
                for(var i in this.flowNodes) {
                    if(this.flowNodes[i].id == result.businessFlowNodes[row].flowNodeId) {
                        result.businessFlowNodes[row].flowNode = this.flowNodes[i];
                        break;
                    }
                }
        		if(result.businessFlowNodes[row].nodePostCfg != null && result.businessFlowNodes[row].nodePostCfg != "") {
        			result.businessFlowNodes[row].postCfgJson = JSON.parse(result.businessFlowNodes[row].nodePostCfg);
        		}else {
        			result.businessFlowNodes[row].postCfgJson = {};
        		}
        		
        		if(result.businessFlowNodes[row].nodeCfg != null && result.businessFlowNodes[row].nodeCfg != "") {
        			result.businessFlowNodes[row].nodeCfgJson = JSON.parse(result.businessFlowNodes[row].nodeCfg);
        			if(validateNodeId != null && validateNodeId == result.businessFlowNodes[row].flowNodeId) {
        				if(result.businessFlowNodes[row].nodeCfgJson.subRuleGroups != null) {
        					result.businessFlowNodes[row].nodeCfgJson.subRuleGroups = JSON.stringify(result.businessFlowNodes[row].nodeCfgJson.subRuleGroups);
        				}else {
        					result.businessFlowNodes[row].nodeCfgJson.subRuleGroups = "{}";
        				}
        			}else if(transferNodeId != null && transferNodeId == result.businessFlowNodes[row].flowNodeId) {
                        if(result.businessFlowNodes[row].nodeCfgJson.subDataMappings != null) {
                            result.businessFlowNodes[row].nodeCfgJson.subDataMappings = JSON.stringify(result.businessFlowNodes[row].nodeCfgJson.subDataMappings);
                        }else {
                            result.businessFlowNodes[row].nodeCfgJson.subDataMappings = "{}";
                        }
                    }
        		}else {
                    result.businessFlowNodes[row].nodeCfgJson = {};
                }
        		
        		if(result.businessFlowNodes[row].nodePreCfg != null && result.businessFlowNodes[row].nodePreCfg != "") {
        			result.businessFlowNodes[row].preCfgJson = JSON.parse(result.businessFlowNodes[row].nodePreCfg);
        		}else {
                    result.businessFlowNodes[row].preCfgJson = {};
                }
        		
        		nodeList.push(result.businessFlowNodes[row]);
        	}
        	
        	//递归调用将子业务流的所有节点加入列表
        	if(result.subBusinessFlowMap != null) {
        		for(var bpmnNodeId in result.subBusinessFlowMap) {
        			this.processBusinessNodesOfResult(result.subBusinessFlowMap[bpmnNodeId], nodeList, subFlows);
        			//如果存在子业务流，则加入subFlows
                    if(result.subBusinessFlowMap[bpmnNodeId] != null) {
                        subFlows[bpmnNodeId] = result.subBusinessFlowMap[bpmnNodeId].businessFlow;
                    }
        		}
        	}
        },
        addRule() {
            var param = {
                    fromObjectType : this.fromObjectType,
                    fromObjectIsList : this.fromObjectIsList,
                    fromObjectKey : this.fromObjectKey,
                    attrMappings : this.attrMappings
            };
            this.businessFlowNodeInfo.preCfgJson.paramsRule.push(param);
        },
        deleteRule(row) {
        	this.businessFlowNodeInfo.preCfgJson.paramsRule.splice(row,1);
        },
        analyzeBpmnProcessTemporary() {//临时分析方法，忽略部分合法性校验
            //保存流程任务节点的Map，bpmn:Task，Key为节点的ID
            var taskMap = {};
            //保存子流程节点的Map，bpmn:SubProcess，Key为节点的ID
            var subProcessMap = {};
            //保存结束节点的Map，bpmn:EndEvent，Key为节点的ID
            var endEventMap = {};
            //保存主流程的开始节点，作为主业务流解析处理的起点，bpmn:StartEvent
            var mainStartEvent;
            //保存子业务流的开始节点，作为子业务流解析处理的起点，bpmn:StartEvent，Key为对应子流程节点的ID
            var subStartEventMap;
            //保存连线的Map，bpmn:SequenceFlow，Key为连线源对象节点的ID，一个源对象节点可能会有多条连线
            var sequenceFlowMap = {};
            //主业务流的开始节点只能有一个，用于判断是否合法
            var mainStartEventCount = 0;
            //循环流程图中所有的图形
            this.elementRegistry.forEach((shape, svgElement)=> {
                if(shape.type == 'bpmn:Task') {//任务节点
                    taskMap[shape.id] = shape;
                }else if(shape.type == 'bpmn:SequenceFlow') {//连线
                    if(sequenceFlowMap[shape.businessObject.sourceRef.id] == null) {
                        sequenceFlowMap[shape.businessObject.sourceRef.id] = [];
                    }
                    sequenceFlowMap[shape.businessObject.sourceRef.id].push(shape);
                }else if(shape.type == 'bpmn:StartEvent') {//开始节点
                    if(shape.businessObject.$parent.$parent.$parent == null) {//主流程的开始节点
                        mainStartEvent = shape;
                        mainStartEventCount++;
                    }else {//子流程的开始节点
                        if(subStartEventMap == null) {
                            subStartEventMap = {};
                        }
                        if(subStartEventMap[shape.businessObject.$parent.id] != null) {//一个子流程节点只允许有一个开始节点
                            console.error(shape.businessObject.$parent.id + "has more than one bpmn:StartEvent!");
                            return null;
                        }
                        subStartEventMap[shape.businessObject.$parent.id] = shape;
                    }                   
                }else if(shape.type == 'bpmn:EndEvent') {//结束节点
                    endEventMap[shape.id] = shape;
                }else if(shape.type == 'bpmn:SubProcess') {//子流程节点
                    subProcessMap[shape.id] = shape;
                }else {//其他类型的图片目前不用处理
                    //do nothing
                }               
            });
            
            if(mainStartEventCount > 1) {//主流程只能有一个
                console.error("more than one main Flow found!");
                return;
            }
            if(mainStartEvent == null) {//必须有主流程
                console.error("no main Flow found!");
                return;
            }
            //处理前面解析出来的各类节点，组装后台需要的数据格式
            var mainFlow = this.processTaskListTemporary(mainStartEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap);            
            if(mainFlow == null) {
                console.error('processTaskList failed!');
            }
            return mainFlow;
        },
        processTaskListTemporary(startEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap) {//临时分析方法，忽略部分合法性校验
            var id = startEvent.id;//主业务流的开始节点作为处理的起点
            var sequenceFlow =null;//连线变量
            var flowEnd = false;//流程结束标志
            
          //保存返回结果的变量，递归结构
          /* {
                flowNodes:[{}],
                subFlows:{
                    taskNodeId1:{
                        //递归结构
                    },
                    taskNodeId2:{
                        //递归结构
                    }
                }
          } */
            var flow = {};
            flow.flowNodes = [];
            while(id != null) {//循环
                var sequenceFlows = sequenceFlowMap[id];//取出开始节点连接的连线
                if(sequenceFlows == null || sequenceFlows.length == 0) {//如果没有连线，直接报错
                    console.error("the shape(" + id + ") doesn't connect to any other shape");
                    ElMessage.error(`节点（` + id + `）未连接任何图形！`);
                    return null;
                }else {//有连线
                    var connectTasks = 0;//节点连接的bpmn:Task节点数
                    var connectSubProcesses = 0;//节点连接的bpmn:SubProcess节点数
                    for(var i=0; i<sequenceFlows.length; i++) {//循环所有的连线
                        var item = sequenceFlows[i];
                    
                        if(taskMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:Task
                            connectTasks++;//计数加1
                            flow.flowNodes.push(taskMap[item.businessObject.targetRef.id]);//将连接的任务节点加入flow.flowNodes
                            id = item.businessObject.targetRef.id;//更新id的值为该任务节点的ID，触发下个循环，处理该任务节点
                        }else if(subProcessMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:SubProcess                         
                            connectSubProcesses++;//计数加1
                            var subStartEvent = subStartEventMap[item.businessObject.targetRef.id];//取出该子流程节点对应的开始节点
                            if(subStartEvent == null) {//如果子流程对应的开始节点为空，直接报错
                                console.error('cannot find StartEvent of SubProcess:'+item.businessObject.targetRef.id);
                                ElMessage.error(`子流程节点（` + item.businessObject.targetRef.id + `）未配置开始节点！`);
                                return null;
                            }
                            //递归调用，处理子业务流
                            var subFlow = this.processTaskListTemporary(subStartEvent, sequenceFlowMap, taskMap, subProcessMap, endEventMap, subStartEventMap);//递归处理子流程
                            if(subFlow == null) {//如果返回的子业务流对象为空，则表示子业务流处理失败，直接返回null
                                console.error('process task list of sub process failed:' + item.businessObject.targetRef.id);
                                return null;
                            }
                            if(flow.subFlows == null) {//初始化子业务流属性，key为子流程节点对应的任务节点标识（目前只有条件分支任务节点和循环任务节点，业务流节点类型）
                                flow.subFlows = {};
                            }
                            //将处理好的子业务流信息设置到flow.subFlows中
                            flow.subFlows[item.businessObject.sourceRef.id] = subFlow;
                        }else if(endEventMap[item.businessObject.targetRef.id] != null) {//connect to bpmn:EndEvent
                            flowEnd = true;//设置流程结束标志
                        }else {//connect to other shape
                            //do nothing
                        }
                    
                    }
                    if(flowEnd && connectTasks > 0) {//connect to both of bpmn:EndEvent and bpmn:Task
                        console.error("can't connect to both of bpmn:EndEvent and bpmn:Task!");
                        ElMessage.error(`节点（` + id + `）同时连接了任务节点和结束节点！`);
                        return null;
                    }
                    if(connectSubProcesses > 1) {//connect to more than one bpmn:SubProcess
                        console.error("can't connect to more than one bpmn:SubProcess!");
                        ElMessage.error(`节点（` + id + `）连接了超过1个子流程节点！`);
                        return null;
                    }
                    
                    if(flowEnd) {//main flow finished.
                        break;
                    }
                    if(connectTasks > 1) {//connect to more than one bpmn:Task
                        console.error("can't connect to more than one bpmn:Task!");
                        ElMessage.error(`节点（` + id + `）节点连接了超过1个任务节点！`);
                        return null;
                    } 
                    if(connectTasks < 1) {//not connect to any bpmn:Task or bpmn:EndEvent
                        console.error("not connect to any bpmn:Task or bpmn:EndEvent!");
                        ElMessage.error(`节点（` + id + `）节点未连接任何任务节点或结束结节！`);
                        return null;
                    }
                    //进入下一次循环，处理连接的下一个任务节点
                }
            }
            return flow;
        },
        processBusinessFlowNodesTemporary(mainFlow) {
            //提交后台处理的最终数据格式
            /* {
                businessFlowNodes:[{}],
                subBusinessFlows: {
                    parentNodeId1:{
                        //递归结构
                    },
                    parentNodeId2:{
                        //递归结构
                    }
                }
            } */
            var editBusinessFlowInfo = {};
            
            //按流程图中任务节点的顺序将业务流节点加入到数组中，根据顺序设置nodeOrder
            if(mainFlow.flowNodes != null && mainFlow.flowNodes.length > 0) {
                editBusinessFlowInfo.businessFlowNodes = [];
                var nodeOrder = 0;
                mainFlow.flowNodes.forEach((flowNode) => {
                    nodeOrder++;
                    this.businessFlowInfo.allBusinessFlowNode.forEach((row) => {
                        if(row.bpmnNodeId == flowNode.id) {
                            row.nodeOrder = nodeOrder;//设置nodeOrder
                            row.nodePostCfg = JSON.stringify(row.postCfgJson);
                            row.nodePreCfg = JSON.stringify(row.preCfgJson);
                            editBusinessFlowInfo.businessFlowNodes.push(row);
                            return;
                        }
                    });
                });
            }
            //按递归的方式将子业务流加入Map中
            if(mainFlow.subFlows != null) {
                editBusinessFlowInfo.subBusinessFlowMap = {};
                for(var parentNodeId in mainFlow.subFlows) {
                    if(mainFlow.subFlows[parentNodeId] !=null) {
                        editBusinessFlowInfo.subBusinessFlowMap[parentNodeId] = this.processBusinessFlowNodesTemporary(mainFlow.subFlows[parentNodeId]);
                        if(this.businessFlowInfo.allSubBusinessFlow[parentNodeId] != null) {
                            editBusinessFlowInfo.subBusinessFlowMap[parentNodeId].businessFlow = this.businessFlowInfo.allSubBusinessFlow[parentNodeId];
                        }
                    }
                }
            }
            return editBusinessFlowInfo;
        },
        showAvailableDataSelectPage(contextTypes, dataSelectedHandler) {
            this.onAvailableDataSuggestShow(this.businessFlowNodeInfo, true, contextTypes, dataSelectedHandler);
        },
        onAvailableDataSuggestShow(currentNodeInfo, isSelect, contextTypes, dataSelectedHandler) {
        	if(this.loginSessionKey == null) {
        		var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/businessSystemAuth/queryLoginSessionKey/" + this.businessFlowInfo.businessFlow.systemId;
                axiosClient.post(queryCodeUrl, objects).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.length > 0) {
                    	this.loginSessionKey = data.data;
                    }
                });
        	}
        	//解析流程图
            var mainFlow = this.analyzeBpmnProcessTemporary();
            if(mainFlow == null) {
                console.error("analyzeBpmnProcess failed!");
                ElMessage.error(`解析BPMN流程信息失败！`);
                return;
            }
            //根据解析后的流程图，处理提交后台的数据结构
            var editBusinessFlowInfo = this.processBusinessFlowNodesTemporary(mainFlow);
            var result = this.availableDataAnalyze(editBusinessFlowInfo, currentNodeInfo);
            if(result != null && result.find) {
            	var objects = {};
            	var hasObject = false;
            	if(result.inProcessDatas != null) {
                    for(var i in result.inProcessDatas) {
                    	if(result.inProcessDatas[i].objectType != null && result.inProcessDatas[i].objectType.length > 0 && result.inProcessDatas[i].objectType != 'J' && 
                    			result.inProcessDatas[i].objectClass != null && result.inProcessDatas[i].objectClass.length > 0) {
                    		objects[result.inProcessDatas[i].objectClass] = result.inProcessDatas[i].objectType;
                    		hasObject = true;
                    	}
                    }
                }
                if(result.domainObjectValues != null) {
                	for(var i in result.domainObjectValues) {
                        if(result.domainObjectValues[i].objectType != null && result.domainObjectValues[i].objectType.length > 0 && result.domainObjectValues[i].objectType != 'J' && 
                                result.domainObjectValues[i].objectClass != null && result.domainObjectValues[i].objectClass.length > 0) {
                            objects[result.domainObjectValues[i].objectClass] = result.domainObjectValues[i].objectType;
                            hasObject = true;
                        }
                    }
                }
                if(hasObject) {
                	var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCodes";
                    axiosClient.post(queryCodeUrl, objects).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                        	if(result.inProcessDatas != null) {
                                for(var i in result.inProcessDatas) {
                                    if(result.inProcessDatas[i].objectType != null && result.inProcessDatas[i].objectType.length > 0 &&
                                            result.inProcessDatas[i].objectClass != null && result.inProcessDatas[i].objectClass.length > 0) {
                                    	result.inProcessDatas[i].objectCode = data.data[result.inProcessDatas[i].objectClass];
                                    }
                                }
                            }
                            if(result.domainObjectValues != null) {
                                for(var i in result.domainObjectValues) {
                                    if(result.domainObjectValues[i].objectType != null && result.domainObjectValues[i].objectType.length > 0 &&
                                            result.domainObjectValues[i].objectClass != null && result.domainObjectValues[i].objectClass.length > 0) {
                                    	result.domainObjectValues[i].objectCode = data.data[result.domainObjectValues[i].objectClass];
                                    }
                                }
                            }
                        }
                        if(result.inProcessDatas != null) {
                            this.inProcessDatas = result.inProcessDatas;
                        }
                        if(result.domainObjectValues != null) {
                            this.domainObjectValues = result.domainObjectValues;
                        }
                        if(isSelect) {
                            this.showAvailableDataSelect = true;
                            this.contextTypeOptions = contextTypes;
                            this.dataSelectedHandler = dataSelectedHandler;
                        }else {
                            this.showAvailableDataSuggest = true;
                        }
                    });
                }else {
                	if(result.inProcessDatas != null) {
                        this.inProcessDatas = result.inProcessDatas;
                    }
                    if(result.domainObjectValues != null) {
                        this.domainObjectValues = result.domainObjectValues;
                    }
                    if(isSelect) {
                        this.showAvailableDataSelect = true;
                        this.contextTypeOptions = contextTypes;
                        this.dataSelectedHandler = dataSelectedHandler;
                    }else {
                        this.showAvailableDataSuggest = true;
                    }
                }
            }
           
        },
        onAvailableDataSuggestHide() {
        	this.showAvailableDataSuggest = false;
            this.showAvailableDataSelect = false;
        	this.inProcessDatas = [];
        },
        selectAvailabeData(contextType, contextKey) {
            if(this.dataSelectedHandler != null) {
                this.dataSelectedHandler(contextType, contextKey);
            }
            this.showAvailableDataSuggest = false;
            this.showAvailableDataSelect = false;
        	this.inProcessDatas = [];
        },
        availableDataAnalyze(allBusinessFlowInfo, currentNodeInfo) {
        	if(allBusinessFlowInfo == null || allBusinessFlowInfo.businessFlowNodes == null 
        			|| allBusinessFlowInfo.businessFlowNodes.length == 0 || currentNodeInfo == null) {
        		return;
        	}
        	var inProcessDatasTmp = [];
        	var domainObjectValuesTmp = [];
        	for(var i in allBusinessFlowInfo.businessFlowNodes) {
        		var nodeInfo = allBusinessFlowInfo.businessFlowNodes[i];
        		if(nodeInfo.bpmnNodeId == currentNodeInfo.bpmnNodeId) {//已经分析到当前节点
        			var resultMap = {};
                    resultMap.inProcessDatas = inProcessDatasTmp;
                    resultMap.domainObjectValues = domainObjectValuesTmp;
                    resultMap.find = true;
                    return resultMap;
        		}else {//不是当前节点
        			if(allBusinessFlowInfo.subBusinessFlowMap != null && allBusinessFlowInfo.subBusinessFlowMap[nodeInfo.bpmnNodeId] != null) {//存在子流程
        				var subMap = this.availableDataAnalyze(allBusinessFlowInfo.subBusinessFlowMap[nodeInfo.bpmnNodeId], currentNodeInfo);//循环处理子流程
        			    if(subMap != null && subMap.find) {//已经找到当前节点
        			    	if(subMap.inProcessDatas != null) {
        			    		inProcessDatasTmp = inProcessDatasTmp.concat(subMap.inProcessDatas);
        			    	}
        			    	if(subMap.domainObjectValues != null) {
        			    		domainObjectValuesTmp = domainObjectValuesTmp.concat(subMap.domainObjectValues);
                            }
        			    	var resultMap = {};
                            resultMap.inProcessDatas = inProcessDatasTmp;
                            resultMap.domainObjectValues = domainObjectValuesTmp;
                            resultMap.find = true;
                            return resultMap;
        			    }
        				
        			}
        			
        			if(nodeInfo.postCfgJson != null) {
        				if(nodeInfo.postCfgJson.asInProcessData && nodeInfo.postCfgJson.inProcessDataKey != null && 
        						nodeInfo.postCfgJson.inProcessDataKey.length > 0) {//往过程数据输出
        					var inProcessDataRow = {};
        					inProcessDataRow.key = nodeInfo.postCfgJson.inProcessDataKey;//保存在过程数据中的Key值
        					inProcessDataRow.objectType = nodeInfo.postCfgJson.inProcessDataType;//数据类型
        					inProcessDataRow.objectClass = nodeInfo.postCfgJson.inProcessDataClass;//数据类
        					if(inProcessDataRow.objectType == null || inProcessDataRow.objectType.length == 0 ||
        							inProcessDataRow.objectClass == null || inProcessDataRow.objectType.objectClass == 0) {//如果为空时，尝试从节点配置信息中获取
        						if(nodeInfo.nodeCfgJson != null && 
        								nodeInfo.nodeCfgJson.nodeResultType != null && nodeInfo.nodeCfgJson.nodeResultType.length > 0 &&
        								nodeInfo.nodeCfgJson.nodeResultClass != null && nodeInfo.nodeCfgJson.nodeResultClass.length > 0) {
        							inProcessDataRow.objectType = nodeInfo.nodeCfgJson.nodeResultType;
                                    inProcessDataRow.objectClass = nodeInfo.nodeCfgJson.nodeResultClass;
        						}
        					}
        					inProcessDataRow.nodeName = nodeInfo.name + "(" + nodeInfo.flowNode.name + ")";
        					inProcessDatasTmp.push(inProcessDataRow);//加入列表
        				}
        			    if(nodeInfo.postCfgJson.asDomainObjectValue) {//往领域对象数据输出
        			    	var domainObjectValueRow = {};
        			    	domainObjectValueRow.key = nodeInfo.postCfgJson.domainObjectValueKey;//保存在过程数据中的Key值
        			    	domainObjectValueRow.objectType = "D";//数据类型-默认领域对象
        			    	domainObjectValueRow.objectClass = nodeInfo.postCfgJson.domainObjectId;//数据类
                            if(domainObjectValueRow.objectType == null || domainObjectValueRow.objectType.length == 0 ||
                            		domainObjectValueRow.objectClass == null || domainObjectValueRow.objectType.objectClass == 0) {//如果为空时，尝试从节点配置信息中获取
                                if(nodeInfo.nodeCfgJson != null && 
                                        nodeInfo.nodeCfgJson.nodeResultType != null && nodeInfo.nodeCfgJson.nodeResultType.length > 0 &&
                                        nodeInfo.nodeCfgJson.nodeResultClass != null && nodeInfo.nodeCfgJson.nodeResultClass.length > 0) {
                                	domainObjectValueRow.objectType = nodeInfo.nodeCfgJson.nodeResultType;
                                	domainObjectValueRow.objectClass = nodeInfo.nodeCfgJson.nodeResultClass;
                                }
                            }
                            domainObjectValueRow.nodeName = nodeInfo.name + "(" + nodeInfo.flowNode.name + ")";
                            domainObjectValuesTmp.push(domainObjectValueRow);//加入列表
                        }
        			}
        		}
        	}
        },
        selectFlowNode(id) {
            this.businessFlowNodeInfo.flowNodeId = id;
        }
    }
}
</script>
<style scoped>
    /* 输入框或下拉选框禁用时：加粗显示提示语 */
    :deep(.el-select .is-disabled .el-select__selected-item){
        color: rgba(97, 116, 209, 1);
    }
</style>