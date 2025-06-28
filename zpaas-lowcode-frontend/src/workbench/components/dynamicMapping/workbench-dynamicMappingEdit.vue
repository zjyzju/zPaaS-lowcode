<!-- 编辑动态映射 -->
<template>
<el-form  v-if="dynamicMapping != null && dynamicMapping.id != null" :model="dynamicMapping" label-width="120px" :rules="validateRules" ref="dynamicMappingForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">动态映射</span></el-breadcrumb-item>
               <el-breadcrumb-item>动态映射</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dynamicMapping.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dynamicMapping.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dynamicMapping.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述">
                <el-input v-model="dynamicMapping.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="映射类型" required prop="mappingType">
                <el-select v-model="dynamicMapping.mappingType" class="m-2" disabled placeholder="Select" size="small">
                       <el-option
                              v-for="item in mappingTypeOptions"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                        />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="业务类型">
                <el-input v-model="dynamicMapping.businessType" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="子业务类型">
                <el-input v-model="dynamicMapping.subBusinessType" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="关键属性">
                <el-input v-model="dynamicMapping.keyAttribute"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="状态" required prop="status">
                <el-select v-model="dynamicMapping.status" class="m-2" placeholder="Select" disabled size="small">
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
            <el-form-item label="变更时间">
                <el-input v-model="dynamicMapping.updateTime" readonly size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="创建时间">
                <el-input v-model="dynamicMapping.createTime" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
        <el-col :span="8"></el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveDynamicMapping()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDynamicMapping()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>

    <!-- 映射详情 -->
    <br/>
    <el-row :gutter="4">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div class="toolbar1">
                <span class="title">映射详情</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showDynamicMappingDetailCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="dynamicMapping.dynamicMappingDetails" stripe style="width: 100%">
                <el-table-column  label="操作" width="100">
                    <template #default="scope">
                        <el-link @click="showDynamicMappingDetailEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteDynamicMappingDetail(scope.row.id, scope.row.description)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="id" label="标识" width="251" />
                    <el-table-column prop="keyAttrValue" label="关键属性值" width="180" />
                    <el-table-column prop="mappingObjectId" label="映射对象标识" width="251" />
                    <el-table-column prop="description" label="映射描述"  width="260" />
                    <el-table-column prop="createTime" label="创建时间" width="180" />
                    <el-table-column prop="updateTime" label="更新时间" width="180" />
                </el-table>
        </el-col>
    </el-row>
</el-form>

<!-- 新建映射详情 -->
<dynamicMappingDetailCreate v-if="showDynamicMappingDetailCreate" :dynamicMapping="dynamicMapping" :newDynamicMappingDetail="newDynamicMappingDetail" @hideDynamicMappingDetailCreatePage="hideDynamicMappingDetailCreatePage" :showDynamicMappingDetailCreate="showDynamicMappingDetailCreate"  @addNewDynamicMappingDetailToList="addNewDynamicMappingDetailToList"/>
<!-- 编辑映射详情 -->
<dynamicMappingDetailEdit v-if="showDynamicMappingDetailEdit" :dynamicMapping="dynamicMapping" :dynamicMappingDetail="dynamicMappingDetail" @hideDynamicMappingDetailEditPage="hideDynamicMappingDetailEditPage" :showDynamicMappingDetailEdit="showDynamicMappingDetailEdit"  @updateDynamicMappingDetailToList="updateDynamicMappingDetailToList"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import dynamicMappingDetailCreate from './workbench-dynamicMappingDetailCreate.vue'
import dynamicMappingDetailEdit from './workbench-dynamicMappingDetailEdit.vue'

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    components: {
        dynamicMappingDetailCreate,
        dynamicMappingDetailEdit
    },

    setup (props, {attrs, emit, slots}) {
        const updateDynamicMappingToList = (dynamicMapping)=> {
            emit('updateToList', 'dynamicMapping', dynamicMapping);
        };
        
        const deleteDynamicMappingFromList = (dynamicMapping)=> {
            emit('deleteFromList', 'dynamicMapping', dynamicMapping);
        };
        
        return {
        	updateDynamicMappingToList,
        	deleteDynamicMappingFromList
        };
    },
    
    data() {        
        const dynamicMapping = ref(null);
        const router = useRoute();

        const mappingTypeOptions = ref(null);

        const statusOptions = ref(null);

        const showDynamicMappingDetailCreate = ref(false);
        const newDynamicMappingDetail = ref(null);


        const showDynamicMappingDetailEdit = ref(false);
        const dynamicMappingDetail = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "mappingType": [
                { required: true, message: '映射类型不能为空！', trigger: 'blur' }
            ],
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "status": [
                { required: true, message: '状态不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            dynamicMapping,
            router,
        	mappingTypeOptions,
            statusOptions,
            showDynamicMappingDetailCreate,
            newDynamicMappingDetail,
            showDynamicMappingDetailEdit,
            dynamicMappingDetail,
            validateRules
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDynamicMapping();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dynamicMapping = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DYNAMIC_MAPPING_TYPE','PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.mappingTypeOptions = data.data['DYNAMIC_MAPPING_TYPE'];
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.loadDynamicMapping();
    },
    methods: {
        loadDynamicMapping() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dynamicMapping = data.data;
                }
            });
        },
    	saveDynamicMapping() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/save";
            this.$refs.dynamicMappingForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dynamicMapping).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            this.updateDynamicMappingToList(data.data);
                            ElMessage(`更新动态映射信息成功`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteDynamicMapping() {
            ElMessageBox.confirm(
                '该动态映射(' + this.dynamicMapping.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/delete/" + this.dynamicMapping.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteDynamicMappingFromList(this.dynamicMapping);
                         
                         ElMessage(`删除动态映射(`+ this.dynamicMapping.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除动态映射(`+ this.dynamicMapping.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        showDynamicMappingDetailCreatePage() {
            this.newDynamicMappingDetail = {
                id:"",
                dynamicMappingId: this.dynamicMapping.id,
                keyAttrValue:"",
                mappingObjectId:"",
                description:"",
                systemId: this.dynamicMapping.systemId,
                tenantId: this.dynamicMapping.tenantId
            };
            this.showDynamicMappingDetailCreate = true;
        },
        addNewDynamicMappingDetailToList(detail) {
            var list = this.dynamicMapping.dynamicMappingDetails;
            if(list == null) {
                list = [];
                this.dynamicMapping.dynamicMappingDetails = list;
            }
            list.push(detail);
            this.dynamicMapping.dynamicMappingDetails.sort((a,b)=>{return a.keyAttrValue > b.keyAttrValue});
            this.showDynamicMappingDetailCreate = false;
        },
        hideDynamicMappingDetailCreatePage() {
            this.showDynamicMappingDetailCreate = false;
        },
        showDynamicMappingDetailEditPage(detailId) {
             this.dynamicMapping.dynamicMappingDetails.forEach((item)=> {
                if(item.id == detailId) {
                    this.dynamicMappingDetail = {
                            id:item.id,
                            dynamicMappingId: item.dynamicMappingId,
                            keyAttrValue:item.keyAttrValue,
                            mappingObjectId:item.mappingObjectId,
                            description:item.description,
                            systemId: item.systemId,
                            tenantId: item.tenantId,
                            createTime: item.createTime,
                            updateTime: item.updateTime
                        };
                    this.showDynamicMappingDetailEdit = true;
                    return;
                }
            });       
        },
        hideDynamicMappingDetailEditPage() {
            this.showDynamicMappingDetailEdit = false;
        },
        updateDynamicMappingDetailToList(updateDetail) {
            this.dynamicMapping.dynamicMappingDetails.forEach((item)=> {
                if(item.id == updateDetail.id) {
                   item.dynamicMappingId = updateDetail.dynamicMappingId;
                   item.keyAttrValue = updateDetail.keyAttrValue;
                   item.mappingObjectId = updateDetail.mappingObjectId;
                   item.description = updateDetail.description;
                   item.createTime = updateDetail.createTime;
                   item.updateTime = updateDetail.updateTime;
                   item.systemId = updateDetail.systemId;
                   item.tenantId = updateDetail.tenantId;
                   this.showDynamicMappingDetailEdit = false;
                   this.dynamicMapping.dynamicMappingDetails.sort((a,b)=>{return a.keyAttrValue > b.keyAttrValue});
                   return;
                }
            });
            
        },
        deleteDynamicMappingDetail(detailId, description) {
            ElMessageBox.confirm(
                    '该动态映射明细(' + description + ')将被删除. 是否继续?',
                    '警告',
                    {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      type: 'warning',
                    }
                  ).then(() => {
                      var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMappingDetail/delete/" + detailId;
                      axiosClient.post(url).then((response) => {
                          var data = response.data; 
                          if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                              if(this.dynamicMapping.dynamicMappingDetails != null && this.dynamicMapping.dynamicMappingDetails.length > 0) {
                                  for(var i in  this.dynamicMapping.dynamicMappingDetails) {
                                      if( this.dynamicMapping.dynamicMappingDetails[i].id == detailId) {
                                          this.dynamicMapping.dynamicMappingDetails.splice(i,1);
                                          break;
                                      }
                                  }
                                  
                              }
                              ElMessage(`删除动态映射明细(`+description+`)成功。`);
                          }
                      }).catch(()=>{
                          ElMessage.error(`删除动态映射明细(`+description+`)失败！`);
                      });
                  }).catch(()=>{
                  });
            
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
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>