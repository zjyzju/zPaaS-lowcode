<!--文件下载类展示组件-->
<template>
    <el-upload  v-if="attrAssign.displayType == 'F'" size="small"
	    v-model:file-list="regionData[attrAssign.attribute.code + '_JSON']"
	    class="upload-demo"
	    :before-upload="uploadFile"
	    :multiple="(attrAssign.displayCfgJSON==null||attrAssign.displayCfgJSON.multiple==null)?false:attrAssign.displayCfgJSON.multiple"
	    :accept="(attrAssign.displayCfgJSON==null||attrAssign.displayCfgJSON.accept==null)?'':attrAssign.displayCfgJSON.accept"
	    :limit="(attrAssign.displayCfgJSON==null||attrAssign.displayCfgJSON.fileLimit==null)?1:attrAssign.displayCfgJSON.fileLimit"
	    :list-type="(attrAssign.displayCfgJSON!=null&&attrAssign.displayCfgJSON.listType!=null&&attrAssign.displayCfgJSON.listType=='P')?'picture-card':'text'"
	    :on-exceed="handleExceed"
	    >
	    <el-button type="primary" size="small" v-if="(attrAssign.displayCfgJSON==null || attrAssign.displayCfgJSON.listType == null || attrAssign.displayCfgJSON.listType != 'P') && disableFlag!=true && attrAssign.readonly!='Y'">点击上传</el-button>
	    <el-icon v-if="attrAssign.displayCfgJSON!=null && attrAssign.displayCfgJSON.listType != null && attrAssign.displayCfgJSON.listType == 'P' && disableFlag!=true && attrAssign.readonly!='Y'"><plus /></el-icon>
	    <template #file="{ file }" >
		    <div v-if="attrAssign.displayCfgJSON!=null && attrAssign.displayCfgJSON.listType != null && attrAssign.displayCfgJSON.listType == 'P'">
			    <img class="el-upload-list__item-thumbnail" :src="file.fileUrl" alt="" />
			    <span class="el-upload-list__item-actions">
			        <span class="el-upload-list__item-preview" @click="handlePreview(file)">
			            <el-icon><ZoomIn /></el-icon>
			        </span>
			        <span class="el-upload-list__item-delete" @click="handleDownload(file)">
			            <el-icon><Download /></el-icon>
			        </span>
			        <span v-if="disableFlag!=true && attrAssign.readonly!='Y'" class="el-upload-list__item-delete" @click="handleRemove(file)">
			            <el-icon><Delete /></el-icon>
			        </span>
			    </span>
		    </div>
		    <div v-if="attrAssign.displayCfgJSON==null || attrAssign.displayCfgJSON.listType == null || attrAssign.displayCfgJSON.listType != 'P'">
				<div class="el-upload-list__item-info">
			        <a class="el-upload-list__item-name" @click="handleDownload(file)">
			            <i class="el-icon el-icon--document"><el-icon><document /></el-icon></i>
			            <span class="el-upload-list__item-file-name" :title="'下载文件'+file.name"> {{file.name}} </span>
			        </a>
			    </div>
			    
			    <label class="el-upload-list__item-status-label">
			        <i class="el-icon el-icon--upload-success el-icon--circle-check"><el-icon><Check /></el-icon></i>
			    </label>
			    <i v-if="disableFlag!=true && attrAssign.readonly!='Y'" class="el-icon el-icon--close" @click="handleRemove(file)"><el-icon><Close /></el-icon></i>
			</div>
	    </template>
	    <template #tip>
	      <div class="el-upload__tip">
	        {{(attrAssign.displayCfgJSON==null||attrAssign.displayCfgJSON.listType==null)?'':attrAssign.displayCfgJSON.uploadTip}} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	      </div>
	    </template>
    </el-upload>
    <el-dialog v-model="showPreviewDialog" width="1040px">
        <img w-full :src="previewImageUrl" alt="预览" width="1000"/>
    </el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ElMessageBox, ElMessage } from 'element-plus'
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
    	uploadFile(file) {
    		var formData = new FormData();
    		if(this.attrAssign.displayCfgJSON != null && this.attrAssign.displayCfgJSON.maxFileSize != null) {
    			if(file.size > this.attrAssign.displayCfgJSON.maxFileSize) {
    				ElMessage.error(`超过允许的最大文件大小！最大文件大小为` + this.attrAssign.displayCfgJSON.maxFileSize + `字节`);
    				return false;
    			}
    		}
    		formData.append(file.name, file);
    		formData.append("reqData","{'fileName':'" + file.name + "','contentType':'" + file.type + "'}");
    		var url = (this.attrAssign.displayCfgJSON==null||this.attrAssign.displayCfgJSON.uploadService==null)?'':('/lcdp-proxy/lowcode/' + this.funcDefine.platformFileUploadUrl + '/' + this.funcDefine.systemUrl + this.attrAssign.displayCfgJSON.uploadService);
    		axiosClient.post(url, formData).then((response) => {
                var data = response.data;
                console.log(data);
                if(data == null || data.status == null || data.status != "200" || data.data == null) {
                	ElMessage.error(`文件上传失败！` + (data==null?``:data.message));
                    return false;
                }else {
                	ElMessage(`文件上传成功！`);
                	var downloadParams = "{'reqData':{'fileKey':'" + data.data.fileKey + "'}}";
                	var fileUrl = (this.attrAssign.displayCfgJSON==null||this.attrAssign.displayCfgJSON.downloadService==null)?'':('/lcdp-proxy/lowcode/' + this.funcDefine.platformFileDownloadUrl + '/' + this.funcDefine.systemUrl + this.attrAssign.displayCfgJSON.downloadService + '?params=' + encodeURI(downloadParams));
                    data.data.fileUrl = fileUrl;
                	this.regionData[this.attrAssign.attribute.code+'_JSON'].push(data.data);
                	//element-plus会自动增加一些其他属性，避免存储这些属性到库中
                	var transferArray = [];
                	this.regionData[this.attrAssign.attribute.code+'_JSON'].forEach((row)=> {
                		var obj = {};
                		obj.name = row.name;
                		obj.fileKey = row.fileKey;
                		obj.fileUrl = row.fileUrl;
                		transferArray.push(obj);
                	});
                	this.regionData[this.attrAssign.attribute.code] = JSON.stringify(transferArray);
                    return false;
                }
            }).catch((e)=>{
                ElMessage.error(`文件上传失败！` + e);
                return false;
            });
    		return false;
    	},
    	handleExceed(files, uploadFiles) {
    		ElMessage.warning(`超出最大上传文件数(${(this.attrAssign.displayCfgJSON==null||this.attrAssign.displayCfgJSON.fileLimit==null)?1:this.attrAssign.displayCfgJSON.fileLimit})！`);
    	},
    	handleRemove(file) {
    		console.log(file);
    		return ElMessageBox.confirm(`是否确定要删除文件：${file.name} ？`).then(
    			() => {
    				var url = (this.attrAssign.displayCfgJSON==null||this.attrAssign.displayCfgJSON.uploadService==null)?'':('/lcdp-proxy/lowcode/' + this.funcDefine.platformUrl + '/' + this.funcDefine.systemUrl + '/' + this.attrAssign.displayCfgJSON.deleteService);
    	            var params = {};
    	            params.reqData = {};
    	            var fileKey = '';
    	            if(file.fileKey != null) {
    	            	fileKey = file.fileKey;
    	            }
    	            params.reqData['fileKey'] = fileKey;
    				axiosClient.post(url, params).then((response) => {
    	                var data = response.data;
    	                console.log(data);
    	                if(data == null || data.status == null || data.status != "200") {
    	                    ElMessage.error(`文件删除失败！` + (data==null?``:data.message));
    	                    return false;
    	                }else {
    	                	this.regionData[this.attrAssign.attribute.code+'_JSON'].forEach((item, index)=> {
    	                        if(item == file) {
    	                            this.regionData[this.attrAssign.attribute.code+'_JSON'].splice(index);
    	                        }
    	                    });
    	                	//element-plus会自动增加一些其他属性，避免存储这些属性到库中
    	                    var transferArray = [];
    	                    this.regionData[this.attrAssign.attribute.code+'_JSON'].forEach((row)=> {
    	                        var obj = {};
    	                        obj.name = row.name;
    	                        obj.fileKey = row.fileKey;
    	                        obj.fileUrl = row.fileUrl;
    	                        transferArray.push(obj);
    	                    });
    	                    this.regionData[this.attrAssign.attribute.code] = JSON.stringify(transferArray);
    	                    ElMessage(`文件删除成功！`);
    	                    return true;
    	                }
    	            }).catch((e)=>{
    	                ElMessage.error(`文件删除失败！` + e);
    	                return false;
    	            });
    				
    			},
    			() => false
    		)
    	},
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