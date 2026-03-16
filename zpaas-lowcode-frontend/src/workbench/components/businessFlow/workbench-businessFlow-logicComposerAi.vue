<!-- 通过AI进行逻辑编排 -->
<template>
<el-dialog v-model="showFlag" title="通过AI进行逻辑编排" width="800px">
    <template #header>
        <span class="title">通过AI进行逻辑编排</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="" label-width="0px">
                <el-input type="textarea" v-model="chatMessages" :rows="20" readonly size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="22">
            <el-form-item label="" label-width="0px">
                <el-input type="textarea" v-model="message" :rows="3" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="2">
            <el-form-item label="" label-width="10px">
                <el-button @click="chatWithAi()" type="primary" :disabled="chatButtonDisabled" circle><el-icon><ArrowUpBold /></el-icon></el-button>
            </el-form-item>
        </el-col>
    </el-row>
    
    <template #footer>
        <span class="dialog-footer">
            <el-row :gutter="4" >
			    <el-col :span="8">
			        
			    </el-col>
                <el-col :span="8">
			    
			    </el-col>
                <el-col :span="8">
			        <el-button @click="hideLogicComposerAiPage()">取消</el-button>
                    <el-button type="primary" @click="confirmAiGenInfo()">生成</el-button>
			    </el-col>
			</el-row>
            
        </span>
    </template>
</el-dialog>

<!-- Ai生成信息确认 -->
<aiGenBusinessFlowInfoConfirm v-if="showAiGenInfoConfirm"  @genBusinessFlowInfoWithAi="genBusinessFlowInfoWithAi" @hideAiGenInfoConfirmPage="hideAiGenInfoConfirmPage" :showAiGenInfoConfirm="showAiGenInfoConfirm"  :businessFlowNodes="businessFlowNodes"/>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import axiosClient from '/src/js/utils/axios.js';

import aiGenBusinessFlowInfoConfirm from './workbench-businessFlow-aiGenBusinessFlowInfoConfirm.vue';

export default {
    props: ['businessFlow', 'showLogicComposerAi'],
    
    emits: [ 'hideLogicComposerAiPage', 'genBusinessFlowInfoByAi'],
    
    setup(props, {attrs, emit, slots}) {
        const hideLogicComposerAiPage = () => {
            emit('hideLogicComposerAiPage');
        };
        
        const genBusinessFlowInfoByAi = (businessFlowNodes) => {
        	emit('genBusinessFlowInfoByAi', businessFlowNodes);
        };
        
        return {
        	hideLogicComposerAiPage,
        	genBusinessFlowInfoByAi
        }
    },
    
    components: {
    	aiGenBusinessFlowInfoConfirm
    },
    
    computed: {
        showFlag: {
            get() {
                return this.showLogicComposerAi;
            },
            set(value) {
                this.hideLogicComposerAiPage();
            }
        }
    },

    data() {
    	const chatMessages = ref('');
        const message = ref(null);
        const businessFlowNodes = ref(null);
        const chatButtonDisabled = false;

        const showAiGenInfoConfirm = ref(false);
    	
        return {
        	chatMessages,
            message,
            businessFlowNodes,
            chatButtonDisabled,

            showAiGenInfoConfirm
        }
    },
    
    methods: {
        hideAiGenInfoConfirmPage() {
            this.showAiGenInfoConfirm = false;
        },
        confirmAiGenInfo() {
            if(this.businessFlowNodes == null || this.businessFlowNodes.trim().length == 0) {
                ElMessage.info(`未获得AI生成的业务流信息!`);
                //return;
            }
            this.showAiGenInfoConfirm = true;
        },
    	genBusinessFlowInfoWithAi(businessFlowNodesJson) {
            this.hideAiGenInfoConfirmPage();
            var aiGenBusinessFlowInfo = {
                "businessFlowNodes": businessFlowNodesJson,
                "businessFlowId": this.businessFlow.id,
                "systemId": this.businessFlow.systemId,
                "tenantId": this.businessFlow.tenantId
            };
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessFlow/genBusinessFlowInfoWithAi";  
            axiosClient.post(url, aiGenBusinessFlowInfo).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.genBusinessFlowInfoByAi(data.data);
                }
            }).catch(ex => {
                ElMessage.error(`访问AI服务失败!\n` + ex);
            });
        },
        chatWithAi() {
            if(this.message == null || this.message.trim().length == 0) {
                ElMessage.info(`请先输入信息!`);
                return;
            }
            var chatMessage = {
                "businessRequirement": this.message,
                "businessFlowId": this.businessFlow.id,
                "systemId": this.businessFlow.systemId,
                "tenantId": this.businessFlow.tenantId
            };
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessFlow/chatWithAi";  
            this.chatButtonDisabled = true;  
            var history = this.chatMessages + "我： " + this.message + "\n";
            history += "\nAI助手： "; 
            this.chatMessages = history;
            this.message = "";
            var response = "";
            url = url + '?' + encodeURI('param=' + JSON.stringify(chatMessage));
            const eventSource = new EventSource(url);
            eventSource.onmessage = (event) => {
                response += event.data;
                this.chatMessages = history + response; 
                this.businessFlowNodes = response;
            };
            eventSource.onerror = (event) => {
                eventSource.close();
                //ElMessage.error(`访问AI服务失败!\n` + event);
                this.chatButtonDisabled = false;
            };
            eventSource.oncomplete = (event) => {
                eventSource.close();
                //this.businessFlowNodes = response;
                this.chatButtonDisabled = false;
            };
            eventSource.addEventListener("close", function (e) {
                eventSource.close();
                //this.businessFlowNodes = response;
                this.chatButtonDisabled = false;
            });

            // axiosClient.post(url, chatMessage).then((response) => {
            //     var data = response.data; 
            //     if(data != null && data.status == "200" && data.data != null) {
            //         this.businessFlowNodes = data.data;
            //         this.chatMessages += "AI助手： " + data.data + "\n"; 
            //     }
            //     this.chatButtonDisabled = false;
            // }).catch(ex => {
            //     ElMessage.error(`访问AI服务失败!\n` + ex);
            //     this.chatButtonDisabled = false;
            // });
        }
    }
}

</script>
