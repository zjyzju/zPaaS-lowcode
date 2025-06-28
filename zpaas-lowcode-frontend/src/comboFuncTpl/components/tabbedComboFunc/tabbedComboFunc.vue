<template>
<div class="common-layout">
<el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane v-if="comboFuncDefine.comboFuncTabs != null" v-for="tab in comboFuncDefine.comboFuncTabs" :label="tab.name" :name="tab.id">
        <div style="height: 100vh">
            <iframe width="100%" height="100%" :src="tab.funcDefine.funcTemplate.url + '?funcId=' + tab.tabFuncId + '&params=' + tab.tabParams_e" style="border:0px"></iframe>
        </div>
    </el-tab-pane>
  </el-tabs>
</div>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    components: {
    	
    },
    
    data() {   
    	const comboFuncDefine = ref({});
    	const comboFuncId = ref(null);
    	
    	const activeName = ref("first");
    	
    	return {
    		comboFuncId,
    		comboFuncDefine,
    		
    		activeName
        }
    },
    mounted() {
    	var url = document.location;
    	var searchString = url.search.substring(1);
    	var params = searchString.split("&");
    	
    	params.forEach(item=>{
    	    var kv = item.split("=");
    	    if("comboFuncId" == kv[0]) {
    	        this.comboFuncId = kv[1];
    	        return;
    	    }
    	})
        this.reloadComboFuncDefineAll();
    },
    methods: {
    	reloadComboFuncDefineAll() {
    		axiosClient.get("/lcdp-proxy/lowcode/fe/api/comboFuncDefine/loadAll/" + this.comboFuncId).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                	this.comboFuncDefine = data.data;
                	if(this.comboFuncDefine != null && this.comboFuncDefine.comboFuncTabs != null && this.comboFuncDefine.comboFuncTabs.length > 0) {
                		this.activeName = this.comboFuncDefine.comboFuncTabs[0].id;
                		for(var index in this.comboFuncDefine.comboFuncTabs) {
                			this.comboFuncDefine.comboFuncTabs[index].tabParams_e = escape(this.comboFuncDefine.comboFuncTabs[index].tabParams);
                		}
                	}else {
                		this.activeName = null;
                	}
                	document.title = this.comboFuncDefine.name;//设置功能页面的名称                    
                }else {
                	ElMessage.error(`加载组合功能定义信息失败`);
                }
                
            });
    	},
    	handleClick(tab, event) {
    		console.log(tab, event);
    	}
    }
    
 }
</script>