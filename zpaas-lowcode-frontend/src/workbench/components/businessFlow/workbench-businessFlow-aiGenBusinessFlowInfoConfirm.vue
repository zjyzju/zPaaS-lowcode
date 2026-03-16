<!-- AI生成业务流信息确认 -->
<template>
<el-dialog v-model="showFlag" title="AI生成业务流信息确认" width="700px">
    <template #header>
        <span class="title">AI生成业务流信息确认</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="" label-width="0px">
                将根据以下AI生成的信息生成业务流，请确认以下AI生成的信息是合法的JSON字符串：
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="" label-width="0px">
                <el-input type="textarea" v-model="businessFlowNodesConfirm" :rows="20" size="small"/>
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
			        <el-button @click="hideAiGenInfoConfirmPage()">取消</el-button>
                    <el-button type="primary" @click="confirmAiGenInfo()">生成</el-button>
			    </el-col>
			</el-row>
            
        </span>
    </template>
</el-dialog>
</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['businessFlowNodes', 'showAiGenInfoConfirm'],
    
    emits: [ 'hideAiGenInfoConfirmPage', 'genBusinessFlowInfoWithAi'],
    
    setup(props, {attrs, emit, slots}) {
        const hideAiGenInfoConfirmPage = () => {
            emit('hideAiGenInfoConfirmPage');
        };
        
        const genBusinessFlowInfoWithAi = (businessFlowNodesConfirm) => {
        	emit('genBusinessFlowInfoWithAi', businessFlowNodesConfirm);
        };
        
        return {
        	hideAiGenInfoConfirmPage,
        	genBusinessFlowInfoWithAi
        }
    },
    
    components: {
    	
    },
    
    computed: {
        showFlag: {
            get() {
                return this.showAiGenInfoConfirm;
            },
            set(value) {
                this.hideAiGenInfoConfirmPage();
            }
        }
    },

    data() {
    	const businessFlowNodesConfirm = ref(null);
        
        return {
        	businessFlowNodesConfirm
        }
    },
    mounted() {
        this.businessFlowNodesConfirm = this.businessFlowNodes;
    },
    methods: {
    	confirmAiGenInfo() {
            if(this.businessFlowNodesConfirm == null || this.businessFlowNodesConfirm.trim().length == 0) {
                ElMessage.info(`未获得AI生成的业务流信息!`);
                return;
            }
            var businessFlowNodesJson = null;
            try {
                businessFlowNodesJson = JSON.parse(this.businessFlowNodesConfirm);
            } catch(error) {
                ElMessage.error(`非法的JSON字符串!\n` + error);
                return;
            }
            this.genBusinessFlowInfoWithAi(businessFlowNodesJson);
        }
    }
}

</script>
