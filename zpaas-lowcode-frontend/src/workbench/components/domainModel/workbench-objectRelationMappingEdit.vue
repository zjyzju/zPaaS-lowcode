<!-- 对象关系映射信息编辑页 -->
 <template>
<el-drawer v-model="showFlag" direction="rtl" size="60%">
    <template #header>
        <span class="title">对象关系映射信息编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
            <div><el-link  type="primary" @click="showObjectRelationMappingCreatePage()">新建对象关系映射</el-link></div>
            <el-form v-if="showObjectRelationMappingEdit && objectRelationMappings" v-for="objectRelationMapping in objectRelationMappings" :model="objectRelationMapping" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
		                <el-divider content-position="left"  >
		                    <div class="toolbar1">
		                        <span class="title">映射：{{objectRelationMapping.name}}&nbsp;&nbsp;&nbsp;&nbsp;
		                        <el-link  type="primary" @click="showObjectRelationMappingModifyPage(objectRelationMapping.id)">编辑</el-link></span>
		                    </div>
		                </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识">
                            <el-input v-model="objectRelationMapping.id" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="名称">
                            <el-input v-model="objectRelationMapping.name" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="领域对象">
                            <el-input v-model="objectRelationMapping.relDomainObject.code" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="数据库表">
                            <el-input v-model="objectRelationMapping.relDbTable.name" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="创建时间">
                            <el-input v-model="objectRelationMapping.createTime" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="更新时间">
                            <el-input v-model="objectRelationMapping.updateTime" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
               
                <el-row :gutter="4">
                    <el-col :span="24">
                        &nbsp;
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="objectRelationMapping.columnMappings">
                    <el-col :span="1"/>
                    <el-col :span="23">
                        <el-table :data="objectRelationMapping.columnMappings" stripe style="width: 100%">
                            <el-table-column prop="relAttribute.code" label="关联属性编码" width="180" />
                            <el-table-column prop="relAttribute.name" label="关联属性名称" width="180" />
                            <el-table-column prop="relDbColumn.name" label="关联字段名称" width="180" />
                            <el-table-column prop="relDbColumn.description" label="关联字段描述"/>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelEditObjectRelationMapping()">取消</el-button>
      </div>
    </template>
</el-drawer>

<!-- 新建对象关系映射信息 -->
<createObjectRelationMapping v-if="showObjectRelationMappingCreate==true && newObjectRelationMapping != null"  @hideObjectRelationMappingCreatePage="hideObjectRelationMappingCreatePage" @addNewObjectRelationMappingToList="addNewObjectRelationMappingToList" :showObjectRelationMappingCreate="showObjectRelationMappingCreate"  :newObjectRelationMapping="newObjectRelationMapping" :orType="orType"/>

<!-- 修改对象关系映射信息 -->
<modifyObjectRelationMapping v-if="showObjectRelationMappingModify==true && modifyObjectRelationMapping != null"  @hideObjectRelationMappingModifyPage="hideObjectRelationMappingModifyPage" @updateObjectRelationMappingToList="updateObjectRelationMappingToList" :showObjectRelationMappingModify="showObjectRelationMappingModify"  :modifyObjectRelationMapping="modifyObjectRelationMapping" :orType="orType"/>

</template>


<script >
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import createObjectRelationMapping from './workbench-objectRelationMappingCreate.vue'
import modifyObjectRelationMapping from './workbench-objectRelationMappingModify.vue'

export default {
	props: ['objectRelationMappings','showObjectRelationMappingEdit', 'orType', 'orId', 'tenantId', 'systemId'],
	
	emits: ['hideEditObjectRelationMappingPage'],
	
	components: {
		createObjectRelationMapping,
		modifyObjectRelationMapping
	},
	
	setup (props, {attrs, emit, slots}) {
		const cancelEditObjectRelationMapping = ()=> {
	    	emit('hideEditObjectRelationMappingPage');
	    };
	    
	    return {
	    	cancelEditObjectRelationMapping
	    };
	},
	computed: {
        showFlag: {
            get() {
                return this.showObjectRelationMappingEdit;
            },
            set(value) {
                this.cancelEditObjectRelationMapping();
            }
        }
    },
    data() {    
		const showObjectRelationMappingCreate = ref(false);
		const newObjectRelationMapping = ref({});
		
		const showObjectRelationMappingModify = ref(false);
        const modifyObjectRelationMapping = ref({});
		
        return {
        	showObjectRelationMappingCreate,
        	newObjectRelationMapping,
        	showObjectRelationMappingModify,
            modifyObjectRelationMapping
        }
    },
    methods: {
    	showObjectRelationMappingCreatePage() {
    		this.newObjectRelationMapping = {
    			id : "",
    			name : "",
    			domainObjectId : "",
    			tableId : "",
    			status : "",
    			tenantId : this.tenantId,
    			systemId : this.systemId,
    			createTime : "",
    			updateTime : "",
    			columnMappings : [],
    			relDomainObject : {},
    			relDbTable : {}
    		}
    		if(this.orType == "O") {
    			this.newObjectRelationMapping.domainObjectId = this.orId;
    		}else if(this.orType == "R") {
    			this.newObjectRelationMapping.tableId = this.orId;
    		}else {
    			ElMessage.error("非法的对象/关系类型！")
    			return;
    		}
    		this.showObjectRelationMappingCreate = true;
    	},
    	
    	addNewObjectRelationMappingToList(newObjectRelationMapping) {
    		if(this.objectRelationMappings == null) {
    			this.objectRelationMappings = [];
    		}
    		this.objectRelationMappings.push(newObjectRelationMapping);
    		this.showObjectRelationMappingCreate = false;
    	},
        hideObjectRelationMappingCreatePage() {
    		this.showObjectRelationMappingCreate = false;
    	},
    	showObjectRelationMappingModifyPage(id) {
            if(this.objectRelationMappings != null && this.objectRelationMappings.length > 0) {
                this.objectRelationMappings.forEach((orm)=>{
                    if(orm.id == id) {
                        this.modifyObjectRelationMapping = orm;
                        return;
                    }
                });
            }
            if(this.modifyObjectRelationMapping == null) {
                return;
            }
            
            this.showObjectRelationMappingModify = true;
        },
        updateObjectRelationMappingToList(objectRelationMapping) {
            if(this.objectRelationMappings == null) {
                this.objectRelationMappings = [];
            }
            for(var i in this.objectRelationMappings){
            	if(this.objectRelationMappings[i].id == objectRelationMapping.id) {
            		this.objectRelationMappings.splice(i,1);
            	}
            }
            this.objectRelationMappings.push(objectRelationMapping);
            this.showObjectRelationMappingModify = false;
        },
        hideObjectRelationMappingModifyPage() {
            this.showObjectRelationMappingModify = false;
        }
    }
}

</script>