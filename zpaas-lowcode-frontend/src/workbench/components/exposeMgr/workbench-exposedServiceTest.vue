<!--测试暴露服务 -->
<template>
<el-dialog v-model="showFlag" title="暴露服务测试" width="1200px" top="5vh">
    <template #header>
        <span class="title">暴露服务测试</span>
    </template>
    <el-form  :model="exposedOperation" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="发布标识">
                    <el-input v-model="exposedOperation.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">              
                <el-form-item label="发布状态">
                    <el-select v-model="exposedOperation.status" class="m-2" disabled placeholder="Select" size="small">
                           <el-option
                                  v-for="item in statusOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>  
            <el-col :span="8">
                <el-form-item label="HttpMethod">
                    <el-select v-model="exposedOperation.httpMethod" class="m-2" disabled placeholder="Select" size="small">
                        <el-option v-for="item in httpMethodOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>          
        </el-row>
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="服务编码">
                    <el-input v-model="exposedOperation.serviceCode" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">              
                <el-form-item label="方法编码">
                    <el-input v-model="exposedOperation.operationCode" readonly size="small"/>
                </el-form-item>
            </el-col>            
            <el-col :span="8">              
                <el-form-item label="请求URL">
                    <el-input v-model="exposedOperation.httpMapping" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>

        <el-row :gutter="4">
            <el-col :span="7">
                <el-form-item label="请求地址">
                    <el-select v-model="requestType" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in requestTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="17">              
                <el-form-item label=" " label-width="5px">
                    <el-input v-model="requestUrl" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        
        <el-row :gutter="4">
		    <el-col :span="24">
		        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
		        <div class="toolbar1">
		            <el-icon><histogram /></el-icon><span>测试信息</span>&nbsp;&nbsp;&nbsp;&nbsp;
		        </div>
		        </el-divider>
		    </el-col>
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="8">
                请求参数说明：
            </el-col>
            <el-col :span="8">
                请求参数：<el-link type="primary" @click="addPageParam">添加分页参数</el-link>
            </el-col>
            <el-col :span="8">
                返回参数：
            </el-col>
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="8">
                <el-input v-model="requestParamsExample" type="textarea" readonly rows="16" size="small"/>
            </el-col>
            <el-col :span="8">
                <el-input v-model="requestParams" type="textarea" rows="16" size="small"/>
            </el-col>
            <el-col :span="8">
                <el-input v-model="responseData" type="textarea" readonly rows="16" size="small"/>
            </el-col>
		</el-row>
        <el-row :gutter="4" v-if="requestType == 'platformFileUploadUrl'">
		    <el-col :span="24">
                &nbsp;
            </el-col>
		</el-row>
        <el-row :gutter="4" v-if="requestType == 'platformFileUploadUrl'">
		    <el-col :span="8">
                <el-upload v-model:file-list="fileList" :before-upload="beforeUploadFile">
                    <el-button type="primary">点击上传</el-button>
                </el-upload>
            </el-col>
            <el-col :span="8">文件名：{{ this.fileSelected.name }}</el-col>
            <el-col :span="8">文件类型：{{ this.fileSelected.type }}</el-col>
		</el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideExposedServiceTestPage()">取消</el-button>&nbsp;&nbsp;
            <el-button @click="testExposedService()" type="primary">测试</el-button>
        </span>
    </template>
</el-dialog>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['exposedOperation', 'showExposedServiceTest'],
    
    emits: ['hideExposedServiceTestPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideExposedServiceTestPage= () => {
            emit('hideExposedServiceTestPage');
        }
        
        return {
        	hideExposedServiceTestPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showExposedServiceTest;
            },
            set(value) {
                this.hideExposedServiceTestPage();
            }
        }
    },
    data() {
        const httpMethodOptions = ref(null);
        
        const statusOptions = ref(null);
        const requestType = ref("platformUrl");
        const requestTypeOptions = ref(null);
        const requestUrl = ref('');
        const requestParams = ref(null);
        const responseData = ref(null);
        const requestParamsExample = ref(null);

        const systemCfg = ref(null);

        const fileSelected = ref({});
        const fileList = ref([]);
        
        return {
            httpMethodOptions,
            statusOptions,
            requestType,
            requestTypeOptions,
            requestUrl,
            requestParams,
            responseData,
            requestParamsExample,

            systemCfg,

            fileSelected,
            fileList
        }
    },
    watch: {
        'requestType': function(val, old) {
            if(this.systemCfg != null) {
                this.requestUrl = this.constructRequestUrl();
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_HTTP_METHOD','PUB_STATUS','PLATFORM_REQUEST_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.httpMethodOptions = data.data['PUB_HTTP_METHOD'];
                this.statusOptions = data.data['PUB_STATUS'];
                this.requestTypeOptions = data.data['PLATFORM_REQUEST_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        var reqData = {};
        var reqDataExample = {};
        if(this.exposedOperation != null && this.exposedOperation.parameters != null && this.exposedOperation.parameters.length > 0) {
            for(var i in this.exposedOperation.parameters) {
                var param = this.exposedOperation.parameters[i];
                if("N" == param.isInParam) {
                    continue;
                }
                if('Y' == param.isInParam) {
                    var paramValue = null;
                    var paramValueExample = null;
                    if('Y' == param.isListParam) {
                        var array = [];
                        if('J' == param.paramType) {
                            array.push('');
                        }else {
                            var subObj = this.generateRequestData(param.paramClassObject);
                            if(subObj != null) {
                                array.push(subObj);
                            }
                        }
                        paramValue = array;
                    }else {
                        if('J' == param.paramType) {
                            paramValue = '';
                        }else {
                            paramValue = this.generateRequestData(param.paramClassObject);
                        }
                    }

                    if('Y' == param.isListParam) {
                        var array = [];
                        if('J' == param.paramType) {
                            array.push(param.paramClass);
                        }else {
                            var subObj = this.generateRequestObject(param.paramClassObject);
                            if(subObj != null) {
                                array.push(subObj);
                            }
                        }
                        paramValueExample = array;
                    }else {
                        if('J' == param.paramType) {
                            paramValueExample = param.paramClass;
                        }else {
                            paramValueExample = this.generateRequestObject(param.paramClassObject);
                        }
                    }
                }
                reqData[param.code] = paramValue;
                reqDataExample[param.code] = paramValueExample;
            }
        }
        var reqParam = {};
        reqParam['reqData'] = reqData;
        this.requestParams = JSON.stringify(reqParam, null, '    ');
        var reqParamExample = {};
        reqParamExample['reqData'] = reqDataExample;
        this.requestParamsExample = JSON.stringify(reqParamExample, null, '    ');
        var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/loadBusinessSystemTestInfo/" + this.exposedOperation.systemId;
        axiosClient.get(url).then((response) => {
            var data = response.data;
            if(data != null && data.status == "200" && data.data != null) {
                this.systemCfg = data.data; 
                if(this.systemCfg != null) {
                    this.requestUrl = this.constructRequestUrl();
                }
            }
        });
    },
    methods: {
    	constructRequestUrl() {
            if(this.systemCfg != null && this.exposedOperation != null){
                return ((this.systemCfg[this.requestType] != null && this.systemCfg[this.requestType].startsWith('/')) ? this.systemCfg[this.requestType] : '/' + this.systemCfg[this.requestType]) + 
                        (this.systemCfg[this.requestType].endsWith('/') ? (this.systemCfg.systemUrl.startsWith('/') ? this.systemCfg.systemUrl.substr(1) : this.systemCfg.systemUrl) : (this.systemCfg.systemUrl.startsWith('/') ? this.systemCfg.systemUrl : '/' + this.systemCfg.systemUrl)) + 
                        (this.systemCfg.systemUrl.endsWith('/') ? (this.exposedOperation.httpMapping.startsWith('/') ? this.exposedOperation.httpMapping.substr(1) : this.exposedOperation.httpMapping) : (this.exposedOperation.httpMapping.startsWith('/') ? this.exposedOperation.httpMapping : '/' + exposedOperation.httpMapping));
            }
        },
        generateRequestObject(objectClass) {
            if(objectClass == null || objectClass.attributes == null || objectClass.attributes.length == 0) {
                return null;
            }
            var obj = {};
            for(var i in objectClass.attributes) {
                var attr = objectClass.attributes[i];
                var attrValue = null;
                if('Y' ==  attr.isListAttr) {
                    var array = [];
                    if('J' == attr.attrType) {
                        array.push(attr.attrClass);
                    }else {
                        var subObj = this.generateRequestObject(attr.attrClassObject);
                        if(subObj != null) {
                            array.push(subObj);
                        }
                    }
                    attrValue = array;
                }else {
                    if('J' == attr.attrType) {
                        attrValue = attr.attrClass;
                    }else {
                        attrValue = this.generateRequestObject(attr.attrClassObject);
                    }
                }
                obj[attr.code] = attrValue;
            }
            return obj;
        },
        generateRequestData(objectClass) {
            if(objectClass == null || objectClass.attributes == null || objectClass.attributes.length == 0) {
                return null;
            }
            var obj = {};
            for(var i in objectClass.attributes) {
                var attr = objectClass.attributes[i];
                var attrValue = null;
                if('Y' ==  attr.isListAttr) {
                    var array = [];
                    if('J' == attr.attrType) {
                        array.push('');
                    }else {
                        var subObj = this.generateRequestData(attr.attrClassObject);
                        if(subObj != null) {
                            array.push(subObj);
                        }
                    }
                    attrValue = array;
                }else {
                    if('J' == attr.attrType) {
                        attrValue = '';
                    }else {
                        attrValue = this.generateRequestData(attr.attrClassObject);
                    }
                }
                obj[attr.code] = attrValue;
            }
            return obj;
        },
        testExposedService() {
            var url = "/lcdp-proxy/lowcode" + this.requestUrl;
            if('platformFileUploadUrl' == this.requestType) {//上传文件
                this.uploadFile(url);
                return;
            }else if('platformStreamUrl' == this.requestType) {//流式调用
                url = url + '?' + encodeURI('params=' + this.requestParams);
                const eventSource = new EventSource(url);
                var returnMessage = "";
                eventSource.onmessage = (event) => {
                    returnMessage += event.data;
                    this.responseData = returnMessage;
                };
                eventSource.onerror = (event) => {
                    eventSource.close();
                };
                eventSource.addEventListener("close", function (e) {
                    eventSource.close();
                });
                return;
            }
            if('platformFileDownloadUrl' == this.requestType) {
                if('GET' == this.exposedOperation.httpMethod) {
                    url = url + '?' + encodeURI('params=' + this.requestParams);
                    axiosClient.get(url, {"responseType":"blob"}).then((response) => {
                        if(response != null && response.status == "200") {
                            const blob = new Blob([response.data], { "type": response.headers["content-type"]});
                            const disposition = decodeURI(response.headers['content-disposition'])
                            // 从响应头中获取文件名称
                            const fileName = disposition.substring(disposition.indexOf('filename=') + 10, disposition.length-1)
                            const a = document.createElement("a"); 
                            const url = window.URL.createObjectURL(blob); // 创建媒体流 url
                            a.download = fileName;
                            a.href = url;
                            a.style.display = "none";
                            document.body.appendChild(a);
                            a.click();
                            a.parentNode.removeChild(a);
                            window.URL.revokeObjectURL(url); // 删除创建的媒体流 url 对象
                            this.responseData = JSON.stringify(response.data, null, '    ');
                        }
                    }).catch(ex => {
                        this.responseData = JSON.stringify(ex, null, '    ');
                    });
                }else {
                    axiosClient.post(url, JSON.parse(this.requestParams), {"responseType":"blob"}).then((response) => {
                        if(response != null && response.status == "200") {
                            const blob = new Blob([response.data], { "type": response.headers["content-type"]});
                            const disposition = decodeURI(response.headers['content-disposition'])
                            // 从响应头中获取文件名称
                            const fileName = disposition.substring(disposition.indexOf('filename=') + 10, disposition.length-1)
                            const a = document.createElement("a"); 
                            const url = window.URL.createObjectURL(blob); // 创建媒体流 url
                            a.download = fileName;
                            a.href = url;
                            a.style.display = "none";
                            document.body.appendChild(a);
                            a.click();
                            a.parentNode.removeChild(a);
                            window.URL.revokeObjectURL(url); // 删除创建的媒体流 url 对象
                            this.responseData = JSON.stringify(response.data, null, '    ');
                        }
                    }).catch(ex => {
                        this.responseData = JSON.stringify(ex, null, '    ');
                    });
                }
            }else {
                if('GET' == this.exposedOperation.httpMethod) {
                    url = url + '?' + encodeURI('params=' + this.requestParams);
                    axiosClient.get(url).then((response) => {
                        if(response != null && response.status == "200") {
                            this.responseData = JSON.stringify(response.data, null, '    ');
                        }
                    }).catch(ex => {
                        this.responseData = JSON.stringify(ex, null, '    ');
                    });
                }else {
                    axiosClient.post(url, JSON.parse(this.requestParams)).then((response) => {
                        if(response != null && response.status == "200") {
                            this.responseData = JSON.stringify(response.data, null, '    ');
                        }
                    }).catch(ex => {
                        this.responseData = JSON.stringify(ex, null, '    ');
                    });
                }
            }
            
        },
        beforeUploadFile(file) {
            this.fileSelected = file;
            var file = {
                'name' : file.name,
                'url' : ''
            }
            this.fileList = [];
            this.fileList.push(file);
        },
        uploadFile(url) {
            if(this.fileSelected == null) {
                ElMessage(`请先选择要上传的文件！`);
                return;
            }
            var formData = new FormData();
            formData.append(this.fileSelected.name, this.fileSelected);
            formData.append("reqData",JSON.stringify(JSON.parse(this.requestParams).reqData));
            axiosClient.post(url, formData).then((response) => {
                this.responseData = JSON.stringify(response.data, null, '    ');
            }).catch((ex)=>{
                this.responseData = JSON.stringify(ex, null, '    ');
            });
        },
        addPageParam() {
            var params = JSON.parse(this.requestParams);
            params['reqData']['pageParam'] = {
                'pageNo': 1,
                'pageRows': 10,
                'total': null,
                'queryTotal': true,
                'sortName': null,
                'sortOrder': null
            }
            this.requestParams = JSON.stringify(params, null, '    ');
        }
    }
}

</script>

<style scoped>
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>