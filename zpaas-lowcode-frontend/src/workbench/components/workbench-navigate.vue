<!-- 工作台导航页 -->
<template>

<div >
<el-row>
    <el-col :span="24">
        <div style="margin-bottom: 1px;margin-top: 10px;margin-left: 5px;" v-if="!isCollapse">
            <el-button type="default" size="small" @click="collapse()"><el-icon><Fold /></el-icon></el-button>
            <el-button type="primary" size="small" @click="manageObjectCreateStep1()"><el-icon><plus/></el-icon></el-button>
            <el-button type="default" size="small" @click="reloadBusinessSystem()"><el-icon><RefreshRight /></el-icon></el-button>&nbsp;
            <el-input v-model="filterString" style="width: 100px;" size="small" clearable/>
        </div>
        <div style="margin-bottom: 1px;margin-top: 10px;margin-left: 1px;margin-right: 1px;" v-if="isCollapse">
            <el-button type="default" size="small" @click="collapse()" style="margin-left: 1px;margin-right: 1px;"><el-icon><Expand /></el-icon></el-button>
            <el-button type="primary" size="small" @click="manageObjectCreateStep1()" style="margin-left: 1px;margin-right: 1px;"><el-icon><plus/></el-icon></el-button>
        </div>
        <br/>
        <el-scrollbar  height="80vh">
        <el-menu default-active="2" class="el-menu-vertical-demo" :collapse="isCollapse">
            <el-sub-menu v-if="businessSystemInfoFiltered.businessDomains" v-for="(domain) in businessSystemInfoFiltered.businessDomains" :index="domain.businessDomain.id">
                <template #title>
                    <el-icon><Folder /></el-icon>
                    <span>{{domain.businessDomain.name}}</span>
                </template>
                <el-sub-menu v-if="domain.serviceObjects != null && domain.serviceObjects.length > 0" :index="'SO_' + domain.businessDomain.id">
                    <template #title>
                        <el-icon><Service /></el-icon>
                        <span >服务对象</span>
                    </template>
                    <el-menu-item v-if="domain.serviceObjects" v-for="serviceObject in domain.serviceObjects" @click="handleClick('D', 'S', serviceObject.id)" :index="'D:S:'+serviceObject.id">
                        {{serviceObject.code}}
                    </el-menu-item>
                </el-sub-menu>
                
                <el-sub-menu v-if="domain.domainObjects != null && domain.domainObjects.length > 0" :index="'D_' + domain.businessDomain.id">
                    <template #title>
                        <el-icon><Notification /></el-icon>
                        <span >领域对象</span>
                    </template>
                    <el-menu-item v-if="domain.domainObjects" v-for="domainObject in domain.domainObjects" @click="handleClick('D', 'D', domainObject.id)" :index="'D:D:'+domainObject.id">
                        {{domainObject.code}}
                    </el-menu-item>
                </el-sub-menu>
                
                <el-sub-menu v-if="domain.valueObjects != null && domain.valueObjects.length > 0"  :index="'V_' + domain.businessDomain.id">
                    <template #title>
                        <el-icon><Guide /></el-icon>
                        <span>值传递对象</span>
                    </template>
                    <el-menu-item v-if="domain.valueObjects" v-for="vo in domain.valueObjects" @click="handleClick('D', 'V', vo.id)" :index="'D:V:'+vo.id">
                        {{vo.code}}
                    </el-menu-item>
                </el-sub-menu>

                <el-sub-menu  :index="'BM_' + domain.businessDomain.id"  v-if="domain.dataModels != null && domain.dataModels.length >0">
                    <template #title>
                        <el-icon><DataBoard /></el-icon>
                        <span>BI模型对象</span>
                    </template>
                    <el-menu-item v-for="dataModel in domain.dataModels" @click="handleClick('B', 'M', dataModel.id)" :index="'B:M:'+dataModel.id">
                       {{dataModel.name}}
                    </el-menu-item>
                </el-sub-menu>
                <el-sub-menu  :index="'BS_' + domain.businessDomain.id"  v-if="domain.dataSets != null && domain.dataSets.length >0">
                    <template #title>
                        <el-icon><DataAnalysis /></el-icon>
                        <span>BI数据集</span>
                    </template>
                    <el-menu-item v-for="dataSet in domain.dataSets" @click="handleClick('B', 'S', dataSet.id)" :index="'B:S:'+dataSet.id">
                       {{dataSet.name}}
                    </el-menu-item>
                </el-sub-menu>

                <el-sub-menu  :index="'C_' + domain.businessDomain.id"  v-if="domain.comboFuncDefines != null && domain.comboFuncDefines.length >0">
                    <template #title>
                        <el-icon><DocumentCopy /></el-icon>
                        <span>组合功能</span>
                    </template>
                    <el-menu-item v-for="comboFuncDefine in domain.comboFuncDefines" @click="handleClick('F', 'C', comboFuncDefine.id)" :index="'F:C:'+comboFuncDefine.id">
                       {{comboFuncDefine.name}}
                    </el-menu-item>
                </el-sub-menu>
                <el-sub-menu  :index="'F_' + domain.businessDomain.id"  v-if="domain.funcDefines != null && domain.funcDefines.length >0">
                    <template #title>
                        <el-icon><document /></el-icon>
                        <span>系统功能</span>
                    </template>
                    <el-menu-item v-for="funcDefine in domain.funcDefines" @click="handleClick('F', 'F', funcDefine.id)" :index="'F:F:'+funcDefine.id">
                       {{funcDefine.name}}
                    </el-menu-item>
                </el-sub-menu>
                <el-sub-menu  :index="'S_' + domain.businessDomain.id"  v-if="domain.subFuncDefines != null && domain.subFuncDefines.length >0">
                    <template #title>
                        <el-icon><Postcard /></el-icon>
                        <span>子功能</span>
                    </template>
                    <el-menu-item v-for="funcDefine in domain.subFuncDefines" @click="handleClick('F', 'S', funcDefine.id)" :index="'F:S:'+funcDefine.id">
                       {{funcDefine.name}}
                    </el-menu-item>
                </el-sub-menu>
            </el-sub-menu>
            
            <!-- 数据库对象 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.dbSchemas != null && businessSystemInfoFiltered.dbSchemas.length >0" index="O:D">
                <template #title>
                    <el-icon><coin /></el-icon>
                    <span>数据库资源</span>
                </template>
                <el-sub-menu  index="D">
                    <template #title>
                        <el-icon><coin /></el-icon>
                        <span>数据库</span>
                    </template>
                    <el-menu-item v-for="dbSchema in businessSystemInfoFiltered.dbSchemas" @click="handleClick('O', 'D', dbSchema.id)" :index="'O:D:'+dbSchema.id">
                       {{dbSchema.name}}
                    </el-menu-item>
                </el-sub-menu>
                <template  v-for="dbSchema in businessSystemInfoFiltered.dbSchemas">
                    <el-sub-menu v-if="dbSchema.dbTables != null && dbSchema.dbTables.length > 0"  :index="'D:'+dbSchema.id">
                        <template #title>
                            <el-icon><Wallet /></el-icon>
                            <span>数据库表（{{dbSchema.name}}）</span>
                        </template>
                        <el-menu-item v-if="dbSchema.dbTables" v-for="dbTable in dbSchema.dbTables" @click="handleClick('O', 'T', dbTable.id)" :index="'O:T:'+dbTable.id">
                            {{dbTable.name}}
                        </el-menu-item>
                    </el-sub-menu>
                </template>
            </el-sub-menu>
            
            <!-- 流程资源 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.workflowProcessResources != null && businessSystemInfoFiltered.workflowProcessResources.length >0" index="W:P">
                <template #title>
                    <el-icon><setting /></el-icon>
                    <span>流程资源</span>
                </template>
                <el-menu-item v-for="workflowProcessResource in businessSystemInfoFiltered.workflowProcessResources" @click="handleClick('W', 'P', workflowProcessResource.id)" :index="'W:P:'+workflowProcessResource.id">
                   {{workflowProcessResource.name}}
                </el-menu-item>
            </el-sub-menu>
            
            <!-- 异步消息处理 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.messageConsumers != null && businessSystemInfoFiltered.messageConsumers.length >0" index="M:K">
                <template #title>
                    <el-icon><ChatDotRound /></el-icon>
                    <span>异步消息处理</span>
                </template>
                <el-menu-item v-for="messageConsumer in businessSystemInfoFiltered.messageConsumers" @click="handleClick('M', 'K', messageConsumer.id)" :index="'M:K:'+messageConsumer.id">
                   {{messageConsumer.name}}
                </el-menu-item>
            </el-sub-menu>

            <!-- 定时任务 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.cronJobs != null && businessSystemInfoFiltered.cronJobs.length >0" index="O:J">
                <template #title>
                    <el-icon><Timer /></el-icon>
                    <span>定时任务</span>
                </template>
                <el-menu-item v-for="cronJob in businessSystemInfoFiltered.cronJobs" @click="handleClick('O', 'J', cronJob.id)" :index="'O:J:'+cronJob.id">
                   {{cronJob.name}}
                </el-menu-item>
            </el-sub-menu>

            <!-- 动态映射 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.dynamicMappings != null && businessSystemInfoFiltered.dynamicMappings.length >0" index="O:Y">
                <template #title>
                    <el-icon><Connection /></el-icon>
                    <span>动态映射</span>
                </template>
                <el-menu-item v-for="dynamicMapping in businessSystemInfoFiltered.dynamicMappings" @click="handleClick('O', 'Y', dynamicMapping.id)" :index="'O:Y:'+dynamicMapping.id">
                   {{dynamicMapping.name}}
                </el-menu-item>
            </el-sub-menu>
            
            <!-- 技术服务资源对象 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.serverResources != null && businessSystemInfoFiltered.serverResources.length >0" index="O:R">
                <template #title>
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>技术服务</span>
                </template>
                <el-menu-item v-for="serverResource in businessSystemInfoFiltered.serverResources" @click="handleClick('O', 'R', serverResource.id)" :index="'O:R:'+serverResource.id">
                   {{serverResource.name}}
                </el-menu-item>
            </el-sub-menu>

            <!-- 字典资源对象 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.dictResources != null && businessSystemInfoFiltered.dictResources.length >0" index="O:P">
                <template #title>
                    <el-icon><Collection /></el-icon>
                    <span>字典资源</span>
                </template>
                <el-menu-item v-for="dictResource in businessSystemInfoFiltered.dictResources" @click="handleClick('O', 'P', dictResource.id)" :index="'O:P:'+dictResource.id">
                   {{dictResource.name}}
                </el-menu-item>
            </el-sub-menu>
            <!-- 安全密钥资源对象 -->
            <el-sub-menu v-if="businessSystemInfoFiltered.securityKeyResources != null && businessSystemInfoFiltered.securityKeyResources.length >0" index="O:S">
                <template #title>
                    <el-icon><Lock /></el-icon>
                    <span>安全密钥</span>
                </template>
                <el-menu-item v-for="securityKeyResource in businessSystemInfoFiltered.securityKeyResources" @click="handleClick('O', 'S', securityKeyResource.id)" :index="'O:S:'+securityKeyResource.id">
                   {{securityKeyResource.name}}
                </el-menu-item>
            </el-sub-menu>
        </el-menu>
        </el-scrollbar>
    </el-col>
</el-row>
</div>

</template>

<script >
import { ref } from 'vue';
import { Connection } from '@element-plus/icons-vue';

export default {
    props: ['businessSystemInfo'],
    
    emits: ['handleClick', 'manageObjectCreateStep1', 'reloadBusinessSystem', 'collapseAside'],
    
    setup (props, {attrs, emit, slots}) {
        const handleClick = (type, subType, objectId) => {
            emit('handleClick', type, subType, objectId);
        };
        
        const collapseAside = (flag) => {
            emit('collapseAside', flag);
        };
        
        const manageObjectCreateStep1 = () => {
            emit('manageObjectCreateStep1');
        };
        
        const reloadBusinessSystem = () => {
            emit('reloadBusinessSystem');
        };
        
        return {
        	handleClick,
        	reloadBusinessSystem,
        	manageObjectCreateStep1,
        	collapseAside
        };
    },
    components: {
        Connection
    },
    watch: {
        'filterString': function(newVal, oldVal){
            this.filterObject();
        },
        'businessSystemInfo': {
            handler: function(value, old) {
                this.filterObject();
            },
            deep: true
        }
    },
    data() {            
    	const isCollapse = ref(false);

        const filterString = ref("");
        const businessSystemInfoFiltered = this.businessSystemInfo;
    	
    	return {
    		isCollapse,
            filterString,
            businessSystemInfoFiltered
    	}
    },
    methods: {
    	collapse() {
    		this.isCollapse = !this.isCollapse;
    		this.collapseAside(this.isCollapse);
    	},
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.businessSystemInfoFiltered = this.businessSystemInfo;
            }else {
                if(this.businessSystemInfo != null) {
                    var tempBusinessSystemInfo = {};
                    tempBusinessSystemInfo.businessSystem = this.businessSystemInfo.businessSystem;

                    if(this.businessSystemInfo.businessDomains != null && this.businessSystemInfo.businessDomains.length > 0) {
                        var tempBusinessDomains = [];
                        for(var i in this.businessSystemInfo.businessDomains) {
                            var businessDomain = this.businessSystemInfo.businessDomains[i];
                            var tempDomain = {};
                            tempDomain.businessDomain = businessDomain.businessDomain;
                            var has = false;
                            //服务对象
                            var tempObjects = this.filterObjects(businessDomain.serviceObjects);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.serviceObjects = tempObjects;
                                has = true;
                            }
                            //领域对象
                            tempObjects = this.filterObjects(businessDomain.domainObjects);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.domainObjects = tempObjects;
                                has = true;
                            }
                            //值传递对象
                            tempObjects = this.filterObjects(businessDomain.valueObjects);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.valueObjects = tempObjects;
                                has = true;
                            }
                            //bi数据模型
                            tempObjects = this.filterObjects(businessDomain.dataModels);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.dataModels = tempObjects;
                                has = true;
                            }
                            //bi数据集
                            tempObjects = this.filterObjects(businessDomain.dataSets);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.dataSets = tempObjects;
                                has = true;
                            }
                            //组合功能
                            tempObjects = this.filterObjects(businessDomain.comboFuncDefines);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.comboFuncDefines = tempObjects;
                                has = true;
                            }
                            //功能
                            tempObjects = this.filterObjects(businessDomain.funcDefines);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.funcDefines = tempObjects;
                                has = true;
                            }
                            //子功能
                            tempObjects = this.filterObjects(businessDomain.subFuncDefines);
                            if(tempObjects != null && tempObjects.length > 0) {
                                tempDomain.subFuncDefines = tempObjects;
                                has = true;
                            }

                            if(has) {
                                tempBusinessDomains.push(tempDomain);
                            }
                        }
                        if(tempBusinessDomains.length > 0) {
                            tempBusinessSystemInfo.businessDomains = tempBusinessDomains;
                        }
                    }

                    if(this.businessSystemInfo.dbSchemas != null && this.businessSystemInfo.dbSchemas.length > 0) {
                        var tempDbSchemas = [];
                        for(var i in this.businessSystemInfo.dbSchemas) {
                            var dbSchema = this.businessSystemInfo.dbSchemas[i];
                            var tempDbSchema = {};
                            if(dbSchema.dbTables != null && dbSchema.dbTables.length > 0) {
                                var dbTable = dbSchema.dbTables[i];
                                var tempTables = [];
                                if(dbTable.name.indexOf(this.filterString) >= 0 || (dbTable.description != null && dbTable.description.indexOf(this.filterString) >= 0)) {
                                    tempTables.push(dbTable);
                                }
                                if(tempTables.length > 0) {
                                    tempDbSchema.dbTables = tempTables;
                                }
                            }
                            if(dbSchema.name.indexOf(this.filterString) >= 0 || (dbSchema.description != null && dbTable.description.indexOf(this.filterString) >= 0) || (tempDbSchema.dbTables != null && tempDbSchema.dbTables.length > 0)) {
                                tempDbSchema.id = dbSchema.id;
                                tempDbSchema.name = dbSchema.name;
                                tempDbSchema.description = dbSchema.description;
                                tempDbSchemas.push(tempDbSchema);
                            }
                        }
                        if(tempDbSchemas.length > 0) {
                            tempBusinessSystemInfo.dbSchemas = tempDbSchemas;
                        }
                    }

                    tempBusinessSystemInfo.cronJobs = this.filterObjects(this.businessSystemInfo.cronJobs);//定时任务
                    tempBusinessSystemInfo.dictResources = this.filterObjects(this.businessSystemInfo.dictResources)//数据字典
                    tempBusinessSystemInfo.dynamicMappings = this.filterObjects(this.businessSystemInfo.dynamicMappings)//动态映射
                    tempBusinessSystemInfo.messageConsumers = this.filterObjects(this.businessSystemInfo.messageConsumers)//动态映射
                    tempBusinessSystemInfo.securityKeyResources = this.filterObjects(this.businessSystemInfo.securityKeyResources)//安全密钥
                    tempBusinessSystemInfo.serverResources = this.filterObjects(this.businessSystemInfo.serverResources)//技术服务
                    tempBusinessSystemInfo.workflowProcessResources = this.filterObjects(this.businessSystemInfo.workflowProcessResources)//流程资源

                    this.businessSystemInfoFiltered = tempBusinessSystemInfo;
                }
            }
        },
        filterObjects(objects) {
            if(objects != null && objects.length > 0) {
                var tempObjects = [];
                for(var j in objects) {
                    var obj = objects[j];
                    if((obj.code != null && obj.code.indexOf(this.filterString) >= 0) || (obj.name != null && obj.name.indexOf(this.filterString) >= 0)) {
                        tempObjects.push(obj);
                    }
                }
                if(tempObjects.length > 0) {
                    return tempObjects;
                }
            }
            return null;
        }
    }
    
}
</script>