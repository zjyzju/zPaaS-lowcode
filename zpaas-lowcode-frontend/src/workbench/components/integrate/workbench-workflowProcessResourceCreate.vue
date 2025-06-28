<!-- 新建工作流流程资源 -->
<template>
<el-dialog v-model="showFlag" title="新建工作流流程资源" width="1200px">
    <template #header>
        <span class="title">新建工作流流程资源</span>
    </template>
	<el-row :gutter="4"> 
	   <el-col :span="12"> 
	       <el-form-item label="流程资源名称" required prop="name"> 
	           <el-input v-model="newWorkflowProcessResource.name" size="small" /> 
	       </el-form-item> 
	   </el-col> 
	   <el-col :span="12"> 
	       <el-form-item label="描述"> 
	           <el-input v-model="newWorkflowProcessResource.description" size="small" /> 
	       </el-form-item> 
	   </el-col> 
    </el-row> 
    <el-row :gutter="4"> 
        <el-col :span="18">
	        <div ref="bpmnCanvasNew" id="bpmnCanvasNew" style="width: 98%; height: 55vh" />
	    </el-col> 
		<el-col :span="6">
		    <div id="bpmnPropertiesNew" class="panel" style="height: 55vh"/>
		</el-col> 
	</el-row>
	<template #footer>
		<span class="dialog-footer"> 
		    <el-button @click="hideWorkflowProcessResourceCreatePage()">取消</el-button> 
		    <el-button type="primary" @click="workflowProcessResourceCreateBack()">上一步</el-button>
			<el-button type="primary" @click="createWorkflowProcessResource()">确定</el-button>
		</span>
	</template>
</el-dialog>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import BpmnModeler from 'camunda-bpmn-js/lib/camunda-platform/Modeler';
import 'camunda-bpmn-js/dist/assets/camunda-platform-modeler.css';

export default {
    props: ['newWorkflowProcessResource','showWorkflowProcessResourceCreate'],
    
    emits: ['addNewWorkflowProcessResourceToList', 'hideWorkflowProcessResourceCreatePage', 'workflowProcessResourceCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewWorkflowProcessResourceToList = (newWorkflowProcessResource)=> {
            emit('addNewWorkflowProcessResourceToList', newWorkflowProcessResource);
        };
        
        const hideWorkflowProcessResourceCreatePage = () => {
            emit('hideWorkflowProcessResourceCreatePage');	
        };
        
        const workflowProcessResourceCreateBack = () => {
            emit('workflowProcessResourceCreateBack'); 
        };
        
        return {
        	addNewWorkflowProcessResourceToList,
        	hideWorkflowProcessResourceCreatePage,
        	workflowProcessResourceCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showWorkflowProcessResourceCreate;
            },
            set(value) {
                this.hideWorkflowProcessResourceCreatePage();
            }
        }
    },
    components: {
    	
    },
    data() {            
        const bpmnModeler = ref(null);
        
        return {
        	bpmnModeler
        }
    },
    watch: {
    	
    },
    mounted() {
    	setTimeout(()=>{
    		this.init();
        },100);
    },
    methods: {
    	init() {
    	    const canvas = this.$refs.bpmnCanvasNew;
    	    this.bpmnModeler = new BpmnModeler({
                container: canvas,
                
                propertiesPanel: {
                	  parent: '#bpmnPropertiesNew'
                }
            });
            /*
            const Logo = canvas.querySelector(".bjs-powered-by");
            while (Logo.firstChild) {
                Logo.removeChild(Logo.firstChild);
            }*/
            this.createNewDiagram();
        },
        createNewDiagram() {
            this.bpmnModeler.createDiagram().then(() => {
                this.bpmnModeler.get('canvas').zoom("fit-viewport", "auto");
            }).catch((err)=> {
                console.log(err);
            });
        },
        createWorkflowProcessResource() {
    		if(this.newWorkflowProcessResource.name == null || this.newWorkflowProcessResource.name.length ==0) {
    			ElMessage.error(`请设置流程资源名称！`);
    			return;
    		}
    		this.bpmnModeler.saveXML({ format: true }).then((data)=> {
                //设置bpmn 2.0 xml字符串到对象中
                var bpmnXml = data.xml;
                var url = "/lcdp-proxy/lowcode/platform/be/api/workflowProcessResource/add"; 
                this.newWorkflowProcessResource.bpmnXml = bpmnXml;
                //提交后台保存数据
                axiosClient.post(url, this.newWorkflowProcessResource).then((response) => {
                	var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        ElMessage(`新增流程资源成功！`);
                        this.addNewWorkflowProcessResourceToList(data.data);
                    }
                }).catch((error)=> {
                    ElMessage.error(`新增流程资源失败:` + error);
                });
            });
    	}
    }
}
</script>
