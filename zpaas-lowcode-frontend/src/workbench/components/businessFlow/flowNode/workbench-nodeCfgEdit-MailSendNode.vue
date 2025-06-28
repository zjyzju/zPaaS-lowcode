<!-- 邮件发送节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'MailSendNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="SMTP服务">
                <el-select v-model="nodeCfgJson.smtpResourceId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in smtpResources" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="发件人来源">
                <el-select v-model="nodeCfgJson.fromSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.fromSource=contextType;this.nodeCfgJson.fromKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="发件人Key">
                <el-input v-model="nodeCfgJson.fromKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="发件人属性">
                <el-input v-model="nodeCfgJson.fromAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="收件人来源">
                <el-select v-model="nodeCfgJson.toSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.toSource=contextType;this.nodeCfgJson.toKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="收件人Key">
                <el-input v-model="nodeCfgJson.toKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="收件人属性">
                <el-input v-model="nodeCfgJson.toAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="抄送人来源">
                <el-select v-model="nodeCfgJson.ccSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.ccSource=contextType;this.nodeCfgJson.ccKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="抄送人Key">
                <el-input v-model="nodeCfgJson.ccKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="抄送人属性">
                <el-input v-model="nodeCfgJson.ccAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="密送人来源">
                <el-select v-model="nodeCfgJson.bccSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.bccSource=contextType;this.nodeCfgJson.bccKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="密送人Key">
                <el-input v-model="nodeCfgJson.bccKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="密送人属性">
                <el-input v-model="nodeCfgJson.bccAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="邮件主题来源">
                <el-select v-model="nodeCfgJson.subjectSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.subjectSource=contextType;this.nodeCfgJson.subjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="邮件主题Key">
                <el-input v-model="nodeCfgJson.subjectKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="邮件主题属性">
                <el-input v-model="nodeCfgJson.subjectAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="内容类型">
                <el-select v-model="nodeCfgJson.contentType" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in contentTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="邮件内容来源">
                <el-select v-model="nodeCfgJson.contentSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.contentSource=contextType;this.nodeCfgJson.contentKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="邮件内容Key">
                <el-input type="textarea" v-model="nodeCfgJson.contentKey" rows="6" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="邮件内容属性">
                <el-input v-model="nodeCfgJson.contentAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
	    <el-col :span="1">
	        <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
	    </el-col>
	    <el-col :span="22">
	        <el-row :gutter="4">
	            <el-col :span="24">
	                <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置邮件内容参数</el-divider>
	            </el-col>
	        </el-row>
	        <el-row :gutter="4">
	            <el-col :span="16">
	                <el-form-item label="参数序号" label-width='100px'>
	                    <el-input v-model="paramId" size="small" />
	                </el-form-item>
	            </el-col>
	            <el-col :span="8">
                    <div style="margin-bottom: 1px;margin-top: 4px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addParam()">添加</el-button>
                    </div>
                </el-col>
	        </el-row>
	        <el-row :gutter="4">
                <el-col :span="16">
                    <el-form-item label="参数值来源">
		                <el-select v-model="paramSource" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.paramSource=contextType;this.paramKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="参数值Key" label-width='100px'>
                        <el-input v-model="paramKey" size="small" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="参数值属性" label-width='100px'>
                        <el-input v-model="paramAttr" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
            
	        <el-row :gutter="4">
	            <el-col :span="24">
	            <el-scrollbar  height="200px">
	                <el-table ref="contentParamsTable" :data="nodeCfgJson.contentParams" stripe style="width: 100%">
	                    <el-table-column fixed="left" label="删除" width="60">
	                        <template  #default="scope">
	                            <el-button link type="primary" size="small" @click.prevent="deleteParam(scope.$index)" >X</el-button>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="paramId" label="参数序号" width="80" />
	                    <el-table-column prop="paramSource" label="参数值来源" width="150" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.paramSource == 'I'">输入参数</span>
		                            <span v-if="scope.row.paramSource == 'P'">过程数据</span>
		                            <span v-if="scope.row.paramSource == 'D'">领域对象</span>
		                            <span v-if="scope.row.paramSource == 'N'">预处理对象</span>
                                    <span v-if="scope.row.paramSource == 'F'">固定值</span>
		                        </div>
		                    </template>
		                </el-table-column>
	                    <el-table-column prop="paramKey" label="参数值Key" width="150" />
	                    <el-table-column prop="paramAttr" label="参数值属性" width="150" />
	                </el-table>
	            </el-scrollbar>
	            </el-col>
	        </el-row>
	        <el-row :gutter="4">
	            <el-col :span="24">
	                <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 1px" ></el-divider>
	            </el-col>
	        </el-row>
	    </el-col>
	    <el-col :span="1">
	        <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
	    </el-col>
	</el-row>
	<el-row :gutter="4">
	   <el-col :span="24">
	       &nbsp;
	   </el-col>
	</el-row>
	<el-row :gutter="4">
        <el-col :span="1">
            <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
        </el-col>
        <el-col :span="22">
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置邮件附件</el-divider>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="16">
                    <el-form-item label="附件名" label-width='100px'>
                        <el-input v-model="attachmentName" size="small" />
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <div style="margin-bottom: 1px;margin-top: 4px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addAttachment()">添加</el-button>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="附件类型" label-width='100px'>
                        <el-input v-model="attachmentType" size="small" />
                    </el-form-item>
                </el-col>
                 <el-col :span="12">
                    <el-form-item label="附件标识" label-width='100px'>
                        <el-input v-model="attachmentId" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="文件来源" label-width='100px'>
                        <el-select v-model="attachmentFileSource" clearable class="m-2" placeholder="Select" size="small">
                            <el-option v-for="item in fileSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    &nbsp;
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="16">
                    <el-form-item label="文件名来源">
                        <el-select v-model="attachmentFileNameSource" clearable class="m-2" placeholder="Select" size="small">
                            <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.attachmentFileNameSource=contextType;this.attachmentFileNameKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="文件名Key" label-width='100px'>
                        <el-input v-model="attachmentFileNameKey" size="small" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="文件名属性" label-width='100px'>
                        <el-input v-model="attachmentFileNameAttr" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
            
            <el-row :gutter="4">
                <el-col :span="24">
                <el-scrollbar  height="200px">
                    <el-table ref="attachmentsTable" :data="nodeCfgJson.attachments" stripe style="width: 100%">
                        <el-table-column fixed="left" label="删除" width="60">
                            <template  #default="scope">
                                <el-button link type="primary" size="small" @click.prevent="deleteAttachment(scope.$index)" >X</el-button>
                            </template>
                            
                        </el-table-column>
                        <el-table-column prop="attachmentName" label="附件名" width="80" />
                        <el-table-column prop="attachmentType" label="附件类型" width="150" />
                        <el-table-column prop="attachmentId" label="附件标识" width="150" />
                        <el-table-column prop="attachmentFileSource" label="文件来源" width="150" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.attachmentFileSource == 'U'">上传文件</span>
		                            <span v-if="scope.row.attachmentFileSource == 'P'">过程数据</span>
		                        </div>
		                    </template>
		                </el-table-column>
                        <el-table-column prop="attachmentFileNameSource" label="文件名来源" width="150" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.attachmentFileNameSource == 'I'">输入参数</span>
		                            <span v-if="scope.row.attachmentFileNameSource == 'P'">过程数据</span>
		                            <span v-if="scope.row.attachmentFileNameSource == 'D'">领域对象</span>
		                            <span v-if="scope.row.attachmentFileNameSource == 'N'">预处理对象</span>
                                    <span v-if="scope.row.attachmentFileNameSource == 'F'">固定值</span>
		                        </div>
		                    </template>
		                </el-table-column>
                        <el-table-column prop="attachmentFileNameKey" label="文件名Key" width="150" />
                        <el-table-column prop="attachmentFileNameAttr" label="文件名属性" width="150" />
                    </el-table>
                </el-scrollbar>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 1px" ></el-divider>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="1">
            <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="执行结果类型">
                <el-select v-model="nodeCfgJson.nodeResultType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in nodeResultTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;&nbsp;<el-checkbox v-model="nodeCfgJson.isListResult" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="执行结果对象">
                <el-input v-model="nodeCfgJson.nodeResultClass" v-if="nodeCfgJson.nodeResultType=='J'" size="small" />
                <el-input type="hidden" v-model="nodeCfgJson.nodeResultClass" readonly v-if="nodeCfgJson.nodeResultType!='J'" size="small" />
                <el-input v-model="nodeResultCode" readonly v-if="nodeCfgJson.nodeResultType!='J'" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8" v-if="nodeCfgJson.nodeResultType!='J'">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectNodeResultClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearNodeResultClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    
    
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
import valueObjectSelect from '../../domainModel/workbench-valueObjectSelect.vue'

export default {
    props: ['showNodeCfgEdit','nodeCfgJson','systemId'],
    
    emits: ['updateNodeCfgJson','showAvailableDataSelectPage'],
        
    setup (props, {attrs, emit, slots}) {
        const updateNodeCfgJson = (json)=> {
            emit('updateNodeCfgJson', json);
        };

        const showAvailableDataSelectPage = (contextTypes, dataSelectedHandler)=> {
            emit('showAvailableDataSelectPage', contextTypes, dataSelectedHandler);
        };
        
        return {
        	updateNodeCfgJson,
            showAvailableDataSelectPage
        };
    },
    components: {
    	domainObjectSelect,
        valueObjectSelect
    },
    data() {     
    	const fileSourceOptions = ref(null);
    	const objectAttrSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
       
       const contentTypeOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const paramId = ref(null);
        const paramSource = ref(null);
        const paramKey = ref(null);
        const paramAttr = ref(null);
        
        const attachmentName = ref(null);
        const attachmentType = ref(null);
        const attachmentId = ref(null);
        const attachmentFileSource = ref(null);
        const attachmentFileNameSource = ref(null);
        const attachmentFileNameKey = ref(null);
        const attachmentFileNameAttr = ref(null);
        
        const nodeResultCode = ref("");
        
        const smtpResources = ref([]);
        
        return {
        	fileSourceOptions,
        	objectAttrSourceOptions,
        	contentTypeOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            paramId,
            paramSource,
            paramKey,
            paramAttr,
            
            attachmentName,
            attachmentType,
            attachmentId,
            attachmentFileSource,
            attachmentFileNameSource,
            attachmentFileNameKey,
            attachmentFileNameAttr,
            
            nodeResultCode,
            smtpResources
        }
    },
    watch: {    	
    	'nodeCfgJson': {
    		handler: function(val, old){
    			this.updateNodeCfgJson(val);
    		},
    		deep:true // 深度监听的参数
    	},
        'nodeCfgJson.nodeResultType': function(val, old){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','FILE_SOURCE_TYPE','OBJECT_TYPE','MAIL_CONTENT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectAttrSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.fileSourceOptions = data.data['FILE_SOURCE_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
                this.contentTypeOptions = data.data['MAIL_CONTENT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + this.systemId + "/P";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.smtpResources = data.data;
                if(this.smtpResources != null && this.smtpResources.length > 0 && (this.nodeCfgJson.smtpResourceId == null || this.nodeCfgJson.smtpResourceId == "") ) {
                    this.nodeCfgJson.smtpResourceId = this.smtpResources[0].id;
                }
            }
        });
        
    	if((this.nodeCfgJson.nodeResultType == "D" || this.nodeCfgJson.nodeResultType == "V")  && this.nodeCfgJson.nodeResultClass != null && this.nodeCfgJson.nodeResultClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.nodeResultType + "/" + this.nodeCfgJson.nodeResultClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.nodeResultCode = data.data;
                }
            });
        }
    },     
    methods: {
    	selectNodeResultClass() {
            if("D" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearNodeResultClass(){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
        },
        managedObjectSelected(obj) {
            this.nodeCfgJson.nodeResultClass = obj.id;
            this.nodeResultCode = obj.code;
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideValueObjectSelectPage(){
            this.managedObjectsForSelect = null;
            this.showValueObjectSelect = false;
       },
       addParam() {
           var param = {
        	    paramId : this.paramId,
        		paramSource : this.paramSource,
                paramKey : this.paramKey,
                paramAttr : this.paramAttr,
               
           };
           this.nodeCfgJson.contentParams.push(param);
       },
       deleteParam(row) {
           this.nodeCfgJson.contentParams.splice(row,1);
       },
       addAttachment() {
           var attachment = {
               attachmentName : this.attachmentName,
        	   attachmentType : this.attachmentType,
        	   attachmentId : this.attachmentId,
        	   attachmentFileSource : this.attachmentFileSource,
        	   attachmentFileNameSource : this.attachmentFileNameSource,
        	   attachmentFileNameKey : this.attachmentFileNameKey,
        	   attachmentFileNameAttr : this.attachmentFileNameAttr,
           };
           this.nodeCfgJson.attachments.push(attachment);
       },
       deleteAttachment(row) {
           this.nodeCfgJson.attachments.splice(row,1);
       }
    }
}
</script>

