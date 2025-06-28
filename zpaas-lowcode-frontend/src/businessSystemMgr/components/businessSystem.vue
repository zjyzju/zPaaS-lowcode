<template>
    <el-row :gutter="4">
        <el-col :span="1">&nbsp;</el-col>
        <el-col :span="23">
            <el-button type="primary" size="small" @click="showBusinessSystemCreatePage()"><el-icon><plus/></el-icon>&nbsp;新建业务系统</el-button>
        </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="1">&nbsp;</el-col>
        <el-col :span="22">
            <el-row :gutter="10" v-for="n in rowCount" v-if="rowCount > 0">
                <el-col :span="6" v-for="m in rowDisplayCount">
                    <el-card style="max-width: 260px; margin-bottom: 10px; margin-top: 10px;" shadow="hover" v-if="tableData[(n-1)*rowDisplayCount + m - 1 ] != null">
                        <template #header>
                            <div class="toolbar1" :style="'background-color: ' + tableData[(n-1)*rowDisplayCount + m - 1].systemColor + '; width: 100%; height: 30px; padding: 20px; margin: -20px; vertical-align:bottom;'">
                                <el-col :span="16">
                                    <span style="font-size: 26px;">
                                        <el-icon>
                                            <setting v-if="tableData[(n-1)*rowDisplayCount + m - 1].systemIcon == '1'" color="white"/>
                                            <PieChart v-else-if="tableData[(n-1)*rowDisplayCount + m - 1].systemIcon == '2'" color="white"/>
                                            <View v-else-if="tableData[(n-1)*rowDisplayCount + m - 1].systemIcon == '3'" color="white"/>
                                            <Discount v-else-if="tableData[(n-1)*rowDisplayCount + m - 1].systemIcon == '4'" color="white"/>
                                            <More v-else color="white"/>
                                        </el-icon>
                                    </span>
                                </el-col>
                                <el-col :span="5">
                                    <el-link @click="showBusinessSystemEditPage(tableData[(n-1)*rowDisplayCount + m - 1].id)"  type="primary">
                                        <span style="font-size: 16px;"><el-icon><EditPen color="white"/></el-icon></span>
                                    </el-link>
                                </el-col>
                                <el-col :span="3">
                                    <el-link @click="deleteBusinessSystem(tableData[(n-1)*rowDisplayCount + m - 1].id, tableData[(n-1)*rowDisplayCount + m - 1].name)"  type="primary">
                                        <span style="font-size: 16px;"><el-icon><Delete color="white"/></el-icon></span>
                                    </el-link>
                                </el-col>
                            </div>
                        </template>
                            <el-row :gutter="4">
                                <el-col :span="24">
                                    <el-link target="_blank" rel = "noopener noreferrer" :href="tableData[(n-1)*rowDisplayCount + m - 1].editUrl"  type="primary">
                                        <span style="font-size: 16px; white-space: nowrap;">
                                            {{ tableData[(n-1)*rowDisplayCount + m - 1].name }} :: {{ tableData[(n-1)*rowDisplayCount + m - 1].description }}
                                        </span>
                                    </el-link>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
                            <el-row :gutter="4">
                                <el-col :span="3">
                                    <span style="color:grey;">url:&nbsp;&nbsp;</span>
                                </el-col>
                                <el-col :span="12">
                                    <span style="color:grey;font-weight: bold;">{{ tableData[(n-1)*rowDisplayCount + m - 1].servletContext }}</span>
                                </el-col>
                                <el-col :span="6">
                                    <span style="color:grey;">version:&nbsp;&nbsp;</span>
                                </el-col>
                                <el-col :span="3">
                                    <span style="color:grey;font-weight: bold;">{{ tableData[(n-1)*rowDisplayCount + m - 1].version }}</span>
                                </el-col>
                            </el-row>
                        <template #footer>
                            <span style="color:grey; font-size: 12px">{{ tableData[(n-1)*rowDisplayCount + m - 1].createTime }}</span>
                        </template>
                    </el-card>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>

    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>

<!-- 新建业务系统 -->
<createBusinessSystem v-if="showBusinessSystemCreate" @addBusinessSystemToList="addBusinessSystemToList" @hideBusinessSystemCreatePage="hideBusinessSystemCreatePage"  :showBusinessSystemCreate="showBusinessSystemCreate"  :businessSystemNew="businessSystemNew"></createBusinessSystem>

<!-- 编辑业务系统 -->
<editBusinessSystem v-if="showBusinessSystemEdit" @updateBusinessSystemToInfo="updateBusinessSystemToInfo" @hideBusinessSystemEditPage="hideBusinessSystemEditPage"  :showBusinessSystemEdit="showBusinessSystemEdit"  :businessSystem="businessSystem"></editBusinessSystem>


</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import editBusinessSystem from '../../workbench/components/domainModel/workbench-businessSystemEdit.vue'
import createBusinessSystem from './businessSystem-create.vue'

export default {
	components: {
		editBusinessSystem,
		createBusinessSystem
	},
	
    data() {    
    	const showBusinessSystemEdit = ref(false);
    	const showBusinessSystemCreate = ref(false);
        const businessSystem = ref({});
        const businessSystemNew = ref({});
        const tableData = ref(null);
        const rowCount = ref(0);
        const businessSystemCount = ref(0);
        const rowDisplayCount = ref(4);
        const systemTypeDicts = ref(null);

        return {
            tableData,
            businessSystem,
            showBusinessSystemEdit,
            businessSystemNew,
            showBusinessSystemCreate,
            rowCount,
            businessSystemCount,
            rowDisplayCount,
            systemTypeDicts
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDicts", ['BUSINESS_SYSTEM_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.systemTypeDicts = data.data['BUSINESS_SYSTEM_TYPE'];
                this.loadBusinessSystems();
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
        
    },
    methods: {
        loadBusinessSystems() {
            axiosClient.get("/lcdp-proxy/lowcode/platform/be/api/businessSystem/listByUser").then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.tableData = [];
                    data.data.forEach(item=>{               
                        var businessSystem = {};
                        businessSystem.id = item.id;
                        businessSystem.name = item.name;
                        businessSystem.servletContext = item.servletContext;
                        businessSystem.description = item.description;
                        businessSystem.status = item.status;
                        businessSystem.version = item.version;
                        businessSystem.editUrl = "/workbench.html?businessSystemId=" + item.id;
                        businessSystem.tenantId = item.tenantId;
                        businessSystem.createTime = item.createTime;
                        businessSystem.updateTime = item.updateTime;
                        businessSystem.systemIcon = item.systemIcon;
                        businessSystem.systemColor = this.getSystemColor(businessSystem.systemIcon);
                        this.tableData.push(businessSystem);
                    });
                    this.tableData.sort((a,b)=> {
                        const dateA = new Date(a.createTime);
                        const dateB = new Date(b.createTime);
                        if(dateA == dateB) {
                            return 0;
                        }else if(dateA < dateB) {
                            return -1;
                        }else {
                            return 1;
                        }
                    });
                    this.rowCount = parseInt(this.tableData.length / this.rowDisplayCount);
                    if(this.tableData.length % this.rowDisplayCount > 0) {
                        this.rowCount ++;
                    }
                    this.businessSystemCount = this.tableData.length;
                }               
            });
        }, 
        getSystemColor(systemType) {
            if(this.systemTypeDicts == null || this.systemTypeDicts.length == 0) {
                return "rgba(141, 195, 209, 1)";
            }
            for(var i in this.systemTypeDicts) {
                var dict = this.systemTypeDicts[i];
                if(systemType == dict.dictValue) {
                    return dict.dictValue2;
                }
            }
            return "rgba(141, 195, 209, 1)";
        },
    	showBusinessSystemEditPage(id) {
            this.tableData.forEach((row)=>{    		   
    		   if(row.id == id) {
    			   this.businessSystem = {
    		               id : row.id,
    		               name : row.name,
    		               servletContext : row.servletContext,
    		               description : row.description,
    		               version : row.version,
                           systemIcon: row.systemIcon,
    		               status : row.status,
    		               tenantId : row.tenantId,
    		               createTime : row.createTime,
    		               updateTime : row.updateTime               
    		       } 
    			   this.showBusinessSystemEdit = true;
    			   return;
    		   }
    	   });             
           
        },
        hideBusinessSystemEditPage() {
            this.showBusinessSystemEdit = false;
        },
        updateBusinessSystemToInfo(system) {
                this.tableData.forEach(item=>{             
                if(item.id == system.id) {
                    item.id = system.id;
                    item.name = system.name;
                    item.servletContext = system.servletContext;
                    item.description = system.description;
                    item.status = system.status;
                    item.systemIcon = system.systemIcon;
                    item.version = system.version;
                    item.editUrl = "/workbench.html?businessSystemId=" + system.id;
                    item.tenantId = system.tenantId;
                    item.createTime = system.createTime;
                    item.updateTime = system.updateTime;
                    item.systemColor = this.getSystemColor(item.systemIcon);
                }
            });
            this.tableData.sort((a,b)=> {
                    const dateA = new Date(a.createTime);
                    const dateB = new Date(b.createTime);
                    if(dateA == dateB) {
                        return 0;
                    }else if(dateA < dateB) {
                        return -1;
                    }else {
                        return 1;
                    }
                });
            this.showBusinessSystemEdit = false;
        },
        showBusinessSystemCreatePage() {
            this.businessSystemNew = {
                    id : "",
                    name : "",
                    servletContext : "",
                    description : "",
                    version : "1.0",
                    systemIcon : null,
                    status : "",
                    tenantId : "",
                    createTime : "",
                    updateTime : ""               
            } 
            this.showBusinessSystemCreate = true;
        },
        hideBusinessSystemCreatePage() {
            this.showBusinessSystemCreate = false;
        },
        addBusinessSystemToList(system) {
            if(this.tableData == null) {
                this.tableData = [];
            }
            system.editUrl = "/workbench.html?businessSystemId=" + system.id;
            system.systemColor = this.getSystemColor(system.systemIcon);
            this.tableData.push(system);  
            this.tableData.sort((a,b)=> {
                    const dateA = new Date(a.createTime);
                    const dateB = new Date(b.createTime);
                    if(dateA == dateB) {
                        return 0;
                    }else if(dateA < dateB) {
                        return -1;
                    }else {
                        return 1;
                    }
                });        
            this.showBusinessSystemCreate = false;
            this.rowCount = parseInt(this.tableData.length / this.rowDisplayCount);
            if(this.tableData.length % this.rowDisplayCount > 0) {
                this.rowCount ++;
            }
            this.businessSystemCount = this.tableData.length;
        },
        deleteBusinessSystem(businessSystemId, name) {
            ElMessageBox.confirm('是否确定要删除业务系统: ' + name + ' (' + businessSystemId + ')？')
                .then(() => {
                    var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/delete/" + businessSystemId;
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data > 0) {
                            for(var i=0; i<this.tableData.length; i++) {
                                if(this.tableData[i].id == businessSystemId) {
                                    this.tableData.splice(i,1);
                                    break;
                                }
                            }
                            ElMessage(`删除业务系统: ` + name + ` (` + businessSystemId + `)成功`);
                        }else {
                            ElMessage.error(`删除业务系统失败`);
                        }
                    });   
                })
                .catch(() => {
                    
                })
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