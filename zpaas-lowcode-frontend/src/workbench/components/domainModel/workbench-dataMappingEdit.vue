<!-- 数据映射信息编辑页 -->
 <template>
<el-drawer v-model="showFlag" direction="rtl" size="60%">
    <template #header>
        <span class="title">数据映射信息编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
            <div>
                <el-row :gutter="4">
                    <el-col :span="6"><el-link  type="primary" @click="showDataMappingCreatePage()">新建数据映射信息</el-link></el-col>
                    <el-col :span="6">
                        <el-select v-model="asFromOrTo" class="m-2" placeholder="Select" size="small">
                            <el-option v-for="item in fromOrToOptions" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-col>
                </el-row>
            </div>
            <el-form v-if="showDataMappingEdit && dataMappings" v-for="dataMapping in dataMappings" :model="dataMapping" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
		                <el-divider content-position="left"  >
		                    <div class="toolbar1">
		                        <span class="title">数据映射：{{dataMapping.name}}&nbsp;&nbsp;&nbsp;&nbsp;
                                <el-link  type="primary" @click="showDataMappingModifyPage(dataMapping.id)">编辑</el-link></span>&nbsp;&nbsp;&nbsp;&nbsp;
		                    </div>
		                </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识">
                            <el-input v-model="dataMapping.id" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="名称">
                            <el-input v-model="dataMapping.name" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="源对象类型">
                            <el-select v-model="dataMapping.fromObjectType" class="m-2" placeholder="Select" disabled size="small">
			                    <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
			                </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="源对象">
                            <el-input v-model="dataMapping.fromObject.code" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="目标对象类型">
                            <el-select v-model="dataMapping.toObjectType" class="m-2" placeholder="Select" disabled size="small">
                                <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="目标对象">
                            <el-input v-model="dataMapping.toObject.code" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="子映射信息">
                            <el-input v-model="dataMapping.subDataMappings" type="textarea" rows="3" readonly placeholder='{"attributeCode1":"dataMappingId1", "attributeCode2":"dataMappingId2"}' size="small" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="创建时间">
                            <el-input v-model="dataMapping.createTime" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="更新时间">
                            <el-input v-model="dataMapping.updateTime" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
               
                <el-row :gutter="4">
                    <el-col :span="24">
                        &nbsp;
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="dataMapping.attributeMappings">
                    <el-col :span="1"/>
                    <el-col :span="23">
                        <el-table :data="dataMapping.attributeMappings" stripe style="width: 100%">
                            <el-table-column prop="fromAttribute.code" label="源属性编码" width="180" />
                            <el-table-column prop="fromAttribute.name" label="源属性名称" width="180" />
                            <el-table-column prop="toAttribute.code" label="目标属性编码" width="180" />
                            <el-table-column prop="toAttribute.name" label="目标属性名称" width="180"/>
                            <el-table-column prop="transferMethod" label="转换方法" width="180" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center"  v-for="rowMethod in transferMethods">
                                        <span v-if="scope.row.transferMethod == rowMethod.id">{{ rowMethod.name }}</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="transferCfg" label="转换配置" width="880"/>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelEditDataMapping()">取消</el-button>
      </div>
    </template>
</el-drawer>

<!-- 新建对象关系映射信息 -->
<createDataMapping v-if="showDataMappingCreate==true && newDataMapping != null"  @hideDataMappingCreatePage="hideDataMappingCreatePage" @addNewDataMappingToList="addNewDataMappingToList" :showDataMappingCreate="showDataMappingCreate"  :newDataMapping="newDataMapping" :asFromOrTo="asFromOrTo"/>
<!-- 修改对象关系映射信息 -->
<modifyDataMapping v-if="showDataMappingModify==true && modifyDataMapping != null"  @hideDataMappingModifyPage="hideDataMappingModifyPage" @updateDataMappingToList="updateDataMappingToList" :showDataMappingModify="showDataMappingModify"  :modifyDataMapping="modifyDataMapping" :asFromOrTo="asFromOrToForModify"/>

</template>


<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import createDataMapping from './workbench-dataMappingCreate.vue'
import modifyDataMapping from './workbench-dataMappingModify.vue'

export default {
	props: ['dataMappings','showDataMappingEdit', 'objectType', 'objectId', 'tenantId', 'systemId'],
	
	emits: ['hideEditDataMappingPage'],
	
	components: {
		createDataMapping,
		modifyDataMapping
	},
	
	setup (props, {attrs, emit, slots}) {
		const cancelEditDataMapping = ()=> {
	    	emit('hideEditDataMappingPage');
	    };
	    
	    return {
	    	cancelEditDataMapping
	    };
	},
	computed: {
        showFlag: {
            get() {
                return this.showDataMappingEdit;
            },
            set(value) {
                this.cancelEditDataMapping();
            }
        }
    },
    data() {    		
		const showDataMappingCreate = ref(false);
        const newDataMapping = ref({});
        const asFromOrTo = ref("F");
        const asFromOrToForModify = ref("F");
        
        const showDataMappingModify = ref(false);
        const modifyDataMapping = ref({});

        const transferMethods = ref(null);
        
        const fromOrToOptions = ref(null);
        
        const objectTypeOptions = ref(null);
        
        return {
            showDataMappingCreate,
            newDataMapping,
            asFromOrTo,
            showDataMappingModify,
            modifyDataMapping,
            fromOrToOptions,
            asFromOrToForModify,
            objectTypeOptions,
            transferMethods
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE','DATA_MAPPING_FROM_OR_TO']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
                this.fromOrToOptions = data.data['DATA_MAPPING_FROM_OR_TO'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        var url = "/lcdp-proxy/lowcode/platform/be/api/dataTransferMethod/list";
        axiosClient.post(url).then((response) => {
            var data = response.data;
            if(data != null && data.status == "200" && data.data != null) {
                this.transferMethods = data.data;                
            }
        });
    },
    methods: {
    	showDataMappingCreatePage() {
            this.newDataMapping = {
                id : "",
                name : "",
                fromObjectType : "",
                fromObjectId : "",
                toObjectType : "",
                toObjectId : "",
                status : "",
                tenantId : this.tenantId,
                systemId : this.systemId,
                createTime : "",
                updateTime : "",
                attributeMappings : [],
                fromObject : {},
                toObject : {}
            }
            if(this.asFromOrTo == "F") {
            	this.newDataMapping.fromObjectType = this.objectType;
                this.newDataMapping.fromObjectId = this.objectId;
            }else if(this.asFromOrTo == "T") {
            	this.newDataMapping.toObjectType = this.objectType;
                this.newDataMapping.toObjectId = this.objectId;
            }else {
                ElMessage.error("非法的源/目标类型！")
                return;
            }
            this.showDataMappingCreate = true;
        },
        
        addNewDataMappingToList(newDataMapping) {
            if(this.dataMappings == null) {
                this.dataMappings = [];
            }
            this.dataMappings.push(newDataMapping);
            this.showDataMappingCreate = false;
        },
        hideDataMappingCreatePage() {
            this.showDataMappingCreate = false;
        },
        showDataMappingModifyPage(id) {
            if(this.dataMappings != null && this.dataMappings.length > 0) {
                this.dataMappings.forEach((orm)=>{
                    if(orm.id == id) {
                        this.modifyDataMapping = orm;
                        return;
                    }
                });
            }
            if(this.modifyDataMapping == null) {
                return;
            }
            if(this.modifyDataMapping.fromObjectType == this.objectType && this.modifyDataMapping.fromObjectId == this.objectId) {
            	this.asFromOrToForModify = "F";
            }else if(this.modifyDataMapping.toObjectType == this.objectType && this.modifyDataMapping.toObjectId == this.objectId) {
                this.asFromOrToForModify = "T";
            }
            this.showDataMappingModify = true;
        },
        updateDataMappingToList(dataMapping) {
            if(this.dataMappings == null) {
                this.dataMappings = [];
            }
            for(var i in this.dataMappings){
                if(this.dataMappings[i].id == dataMapping.id) {
                    this.dataMappings.splice(i,1);
                }
            }
            this.dataMappings.push(dataMapping);
            this.showDataMappingModify = false;
        },
        hideDataMappingModifyPage() {
            this.showDataMappingModify = false;
        }
        
    }
}

</script>