<!-- 根据数据库表生成 -->
<template>
<el-dialog v-model="showFlag" title="根据数据库表生成">
    <template #header>
        <span class="title">根据数据库表生成</span>
    </template>
    <el-form  :model="modelDrivenRequest" label-width="120px" :rules="validateRules" ref="modelDrivenRequestForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="生成内容">
                <el-scrollbar height="200px">
                    <li>数据库表DbTable</li>
                    <li>数据库表字段DbColumn</li>
                    <li>实体对象DomainObject</li>
                    <li>实体对象属性Attribute</li>
                    <li>实体对象基本方法Operation</li>
                    <li>方法出参及入参Parameter</li>
                    <li>方法业务流BusinessFlow</li>
                    <li>业务流节点BusinessFlowNode</li>
                    <li>对象与表映射ORMapping</li>
                    <li>字段映射ColumnMapping</li>
                </el-scrollbar>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="可选内容">
                    <el-checkbox v-model="modelDrivenRequest.generateValueObject" label="生成值传递对象" size="large" />
                    <li>值传递对象ValueObject</li>
                    <li>值传递对象属性Attribute</li>
                    <el-checkbox v-model="modelDrivenRequest.generateDataMapping" label="生成数据映射规则" size="large" />
                    <li>数据映射DataMapping</li>
                    <li>属性映射AttributeMapping</li>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
				<el-form-item label="归属业务域" required prop="businessDomainId">
				    <el-select v-model="modelDrivenRequest.businessDomainId"  v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
				        <el-option
				            v-for="item in businessDomains"
				            :key="item.businessDomain.id"
				            :label="item.businessDomain.name"
				            :value="item.businessDomain.id"
				            />
				    </el-select>
				</el-form-item>
            </el-col>
            <el-col :span="12">              
				<el-form-item label="对应数据库" required prop="dbSchemaId">
				    <el-select v-model="modelDrivenRequest.dbSchemaId" class="m-2" placeholder="Select" size="small">
				        <el-option
				          v-for="item in dbSchemas"
				          :key="item.id"
				          :label="item.name"
				          :value="item.id"
				        />
				     </el-select>
				</el-form-item>                
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="忽略的表名前缀">
                    <el-input v-model="modelDrivenRequest.ignorePrefixString" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="说明：">
                允许输入多个，以逗号分隔
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="请选择库表">
                    <el-link @click="selectAll()"  type="primary" >全选</el-link> &nbsp;&nbsp;
                    <el-link @click="cancelAll()"  type="primary" >取消</el-link>
                </el-form-item> 
            </el-col>   
            <el-col :span="12">
                <el-form-item label="过滤表">
                    <el-input v-model="filterString" size="small"/>
                </el-form-item>
            </el-col>         
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="">
                <el-scrollbar height="120px">
                    <el-checkbox-group v-model="modelDrivenRequest.tableArray" border>
                        <el-checkbox v-if="tables != null && tables.length > 0" v-for="row in tables" :label="row.tableName" />
                    </el-checkbox-group>
                </el-scrollbar>   
                </el-form-item>            
            </el-col>            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideModelDrivenGeneratePage()">取消</el-button>
            <el-button type="primary" @click="manageModelDrivenGenerateBack()">上一步</el-button>
            <el-button type="primary" @click="modelDrivenGenerate()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['modelDrivenRequest', 'showModelDrivenGenerate', 'dbSchemas', 'businessDomains'],
    
    emits: [ 'hideModelDrivenGeneratePage', 'manageModelDrivenGenerateBack'],
    
    setup(props, {attrs, emit, slots}) {
        const hideModelDrivenGeneratePage = () => {
            emit('hideModelDrivenGeneratePage');
        };
        
        const manageModelDrivenGenerateBack = () => {
        	emit('manageModelDrivenGenerateBack');
        };
        
        return {
        	hideModelDrivenGeneratePage,
        	manageModelDrivenGenerateBack
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showModelDrivenGenerate;
            },
            set(value) {
                this.hideModelDrivenGeneratePage();
            }
        }
    },
    data() {
        const tables = ref([]);
        const allTables = ref([]);
        const filterString = ref("");
        const validateRules = ref({
            "businessDomainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ],
            "dbSchemaId": [
                { required: true, message: '对应数据库不能为空！', trigger: 'blur' }
            ]
        });

        return {
            tables,
            allTables,
            filterString,
            validateRules
        }
    },
    
    mounted() {
    	this.queryTableList(this.modelDrivenRequest.dbSchemaId, this.modelDrivenRequest.systemId);
    },
    
    watch: {
    	'modelDrivenRequest.dbSchemaId' (newSchemaId, oldSchemaId){
    		this.queryTableList(newSchemaId, this.modelDrivenRequest.systemId);
    	},
    	'modelDrivenRequest.generateValueObject' (newVal, oldVal){
            if(newVal == false) {
            	this.modelDrivenRequest.generateDataMapping = false;
            }
        },
        'modelDrivenRequest.generateDataMapping' (newVal, oldVal){
            if(newVal == true) {
                this.modelDrivenRequest.generateValueObject = true;
            }
        },
        'filterString' (newVal, oldVal){
        	this.filterTables();
        }
    },
    
    methods: {
    	modelDrivenGenerate() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/generateAll";
            
            if(this.modelDrivenRequest.tableArray == null || this.modelDrivenRequest.tableArray.length == 0) {
                ElMessage.error(`请选择要生成配置数据的库表！`);
                return;
            }
            if(this.modelDrivenRequest.ignorePrefixString != null && this.modelDrivenRequest.ignorePrefixString != "") {
            	this.modelDrivenRequest.ignorePrefixes = this.modelDrivenRequest.ignorePrefixString.split(",");
            }else {
            	this.modelDrivenRequest.ignorePrefixes = [];
            }
            this.$refs.modelDrivenRequestForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.modelDrivenRequest).then((response) => {
                        var data = response.data; 
                        console.log(data);
                        if(data != null && data.status == "200" && data.data != null && data.data == true) {
                            ElMessage(`根据数据库表生成成功，请刷新工作台！！`);
                            this.hideModelDrivenGeneratePage();
                        }
                    }).catch((error)=>{
                        ElMessage.error(`根据数据库表生成失败`);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        queryTableList(dbSchemaId, systemId) {
        	if(systemId == null || systemId == "") {
        		ElMessage.error(`业务系统不能为空`);
        		return;
        	}
        	if(dbSchemaId == null || dbSchemaId == "") {
                ElMessage.error(`归属数据库不能为空`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryTableList/" + systemId + "/" + dbSchemaId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                this.tables = data;
                if(data != null && data.status == "200" && data.data != null && data.data.length > 0) {
                	this.tables = data.data;
                	this.tables.sort((a,b)=> {
                		if(a.tableName > b.tableName) {
                			return 1;
                		}else if(a.tableName = b.tableName) {
                			return 0;
                		}else {
                			return -1;
                		}
                	});
                	this.allTables = this.tables;
                }else {
                	this.tables = [];
                	this.allTables = this.tables;
                }
                this.filterTables();
            });
        },
        selectAll() {
        	this.modelDrivenRequest.tableArray = [];
        	if(this.tables != null && this.tables.length > 0) {
        		this.tables.forEach((row)=> {
        			this.modelDrivenRequest.tableArray.push(row.tableName);
        		});
        	}
        },
        cancelAll() {
            this.modelDrivenRequest.tableArray = [];
        },
        filterTables() {
        	if(this.filterString == null || this.filterString.trim().length == 0) {
        		this.tables = this.allTables;
        	}else {
        		if(this.allTables != null && this.allTables.length > 0) {
        			var tempTables = [];
        			this.allTables.forEach((row)=>{
        				if(row.tableName.indexOf(this.filterString) >= 0) {
        					tempTables.push(row);
        				} 
        			});
        			this.tables = tempTables;
        		}
        	}
        }
    }
}

</script>
