<!-- 编辑流程资源 -->
<template>
<el-form v-if="workflowProcessResource != null" :model="workflowProcessResource" label-width="120px" :rules="validateRules" ref="workflowProcessResourceForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">流程资源</span></el-breadcrumb-item>
               <el-breadcrumb-item>流程定义</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{workflowProcessResource.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="workflowProcessResource.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="workflowProcessResource.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="状态" required prop="status">
                <el-select v-model="workflowProcessResource.status" class="m-2" placeholder="Select" disabled size="small">
                    <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
	        <el-form-item label="流程定义标识">
	            <el-input v-model="workflowProcessResource.processDefId" readonly size="small"/>
	        </el-form-item>
        </el-col>
        <el-col :span="15">
            <el-form-item label="描述">
                <el-input v-model="workflowProcessResource.description" type="textarea" rows="2" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>
</el-form>
    <el-row :gutter="4"> 
        <el-col :span="17">
            <div ref="bpmnCanvasEdit" id="bpmnCanvasEdit" style="width: 99%; height: 55vh"/>
        </el-col> 
        <el-col :span="6">
            <div id="bpmnPropertiesEdit" class="panel" style="height: 55vh"></div>
        </el-col> 
        <el-col :span="1">&nbsp;</el-col>
    </el-row>
    <br>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveWorkflowProcessResource()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteWorkflowProcessResource()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import BpmnModeler from 'camunda-bpmn-js/lib/camunda-platform/Modeler';
import 'camunda-bpmn-js/dist/assets/camunda-platform-modeler.css';

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateWorkflowProcessResourceToList = (workflowProcessResource)=> {
            emit('updateToList', 'workflowProcessResource', workflowProcessResource);
        };
        
        const deleteWorkflowProcessResourceFromList = (workflowProcessResource)=> {
            emit('deleteFromList', 'workflowProcessResource', workflowProcessResource);
        };
        
        return {
        	updateWorkflowProcessResourceToList,
        	deleteWorkflowProcessResourceFromList
        };
    },
    components: {
    	
    },
    data() {   
        const workflowProcessResource = ref(null);
        const router = useRoute();

    	const statusOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "status": [
                { required: true, message: '状态不能为空！', trigger: 'blur' }
            ]
        });
    	
    	return {
            workflowProcessResource,
            router,
        	statusOptions,
            validateRules
        }
    },
    watch: {
    	'workflowProcessResource.bpmnXml': function(val, old){
            if(val == null || (val == null && old == null)) {
                return;
            }
            this.loadProcessBpmnXml();
        },
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadWorkflowProcessResource();
                },100);
            }else if(val == null || val.objectId == null) {
                this.workflowProcessResource = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['WORKFLOW_PROCESS_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.statusOptions = data.data['WORKFLOW_PROCESS_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.init();
        setTimeout(()=>{
            this.loadWorkflowProcessResource();
        },100);
    },
    methods: {
        loadWorkflowProcessResource() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/workflowProcessResource/load/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.workflowProcessResource = data.data;
                }
            });
        },
    	init() {
            const canvas = this.$refs.bpmnCanvasEdit;
            this.bpmnModeler = new BpmnModeler({
                container: canvas,
                
                propertiesPanel: {
                      parent: '#bpmnPropertiesEdit'
                }
            });
            /*
            const Logo = canvas.querySelector(".bjs-powered-by");
            while (Logo.firstChild) {
                Logo.removeChild(Logo.firstChild);
            }*/
            
        },
        loadProcessBpmnXml() {
            this.bpmnModeler.importXML(this.workflowProcessResource.bpmnXml).then(() => {
                this.bpmnModeler.get('canvas').zoom("fit-viewport", "auto");
            }).catch((err)=> {
                console.log(err);
            });
        },
    	saveWorkflowProcessResource() {
        	this.$refs.workflowProcessResourceForm.validate((valid, fields)=> {
                if(valid) {
                    this.bpmnModeler.saveXML({ format: true }).then((data)=> {
                        //设置bpmn 2.0 xml字符串到对象中
                        var bpmnXml = data.xml;
                        var url = "/lcdp-proxy/lowcode/platform/be/api/workflowProcessResource/save"; 
                        this.workflowProcessResource.bpmnXml = bpmnXml;
                        //提交后台保存数据
                        axiosClient.post(url, this.workflowProcessResource).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                                ElMessage(`更新流程资源成功！`);
                                this.updateWorkflowProcessResourceToList(data.data);
                            }
                        }).catch((error)=> {
                            ElMessage.error(`更新流程资源失败:` + error);
                        });
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteWorkflowProcessResource() {
            ElMessageBox.confirm(
                '该流程资源(' + this.workflowProcessResource.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/workflowProcessResource/delete/" + this.workflowProcessResource.systemId + "/" + this.workflowProcessResource.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteWorkflowProcessResourceFromList(this.workflowProcessResource);
                         
                         ElMessage(`删除流程资源(`+ this.workflowProcessResource.name +`)成功。`);
                     }
                 }).catch((ex)=>{
                     ElMessage.error(`删除流程资源(`+ this.workflowProcessResource.name +`)失败！` + ex);
                 });
             }).catch(()=>{});
        }
    }
}
</script>

<style scoped>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
} 
/* 输入框或下拉选框禁用时：加粗显示提示语 */
:deep(.el-input.is-disabled .el-input__inner){
    color: black ;
}
</style>