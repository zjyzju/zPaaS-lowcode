<!--文件上传类展示组件-->
<template>
    <el-upload  v-if="attrAssign.displayType == 'G'" size="small"
	    v-model:file-list="regionData[attrAssign.attribute.code + '_JSON']"
	    class="hidden-Btn"
	    :list-type="(attrAssign.displayCfgJSON!=null&&attrAssign.displayCfgJSON.listType!=null&&attrAssign.displayCfgJSON.listType=='P')?'picture-card':'text'"
	    >
	    <template #file="{ file }" >
		    <div v-if="attrAssign.displayCfgJSON!=null && attrAssign.displayCfgJSON.listType != null && attrAssign.displayCfgJSON.listType == 'P'">
			    <img class="el-upload-list__item-thumbnail" :src="file.fileUrl" alt="" />
			    <span class="el-upload-list__item-actions">
			        <span class="el-upload-list__item-preview" @click="handlePreview(file)">
			            <el-icon><ZoomIn /></el-icon>
			        </span>
			        <span  class="el-upload-list__item-delete" @click="handleDownload(file)">
			            <el-icon><Download /></el-icon>
			        </span>
			    </span>
		    </div>
		    <div v-if="attrAssign.displayCfgJSON==null || attrAssign.displayCfgJSON.listType == null || attrAssign.displayCfgJSON.listType != 'P'">
				<div class="el-upload-list__item-info">
			        <a class="el-upload-list__item-name" @click="handleDownload(file)">
			            <i class="el-icon el-icon--document"><el-icon><document /></el-icon></i>
			            <span class="el-upload-list__item-file-name" :title="'下载文件'+file.name" style="width:160px;text-align:left"> {{file.name}} </span>
			        </a>
			    </div>
			</div>
	    </template>
	    
	</el-upload>
    <el-dialog v-model="showPreviewDialog" width="1040px">
        <img w-full :src="previewImageUrl" alt="预览" width="1000"/>
    </el-dialog>
</template>
<script>
import { ref } from 'vue';


export default {
    props: ['funcRegion','funcDefine','regionData', 'attrAssign', 'disableFlag'],
    
    components: {
    	
    },
    data() { 
    	const showPreviewDialog = ref(false);
    	const previewImageUrl = ref(null);
    	
    	return {
    		showPreviewDialog,
    		previewImageUrl
    	}
    },
    mounted() {
    	if(this.regionData[this.attrAssign.attribute.code] == null) {
    		this.regionData[this.attrAssign.attribute.code+'_JSON'] = [];
    	}else {
    		this.regionData[this.attrAssign.attribute.code+'_JSON'] = JSON.parse(this.regionData[this.attrAssign.attribute.code]);
    	}
    },
    methods: {
    	handlePreview(file) {
    		this.showPreviewDialog = true;
    		this.previewImageUrl = file.fileUrl;
    	},
    	handleDownload(file) {
    		let a = document.createElement('a');
    	    a.href =file.fileUrl;
    	    a.click();
    	}
    }
}
</script>
<style scoped>
	.hidden-Btn :deep() .el-upload--picture-card {
	    display: none;
	}
	
	.hidden-Btn :deep() .el-upload--text {
        display: none;
    }
</style>