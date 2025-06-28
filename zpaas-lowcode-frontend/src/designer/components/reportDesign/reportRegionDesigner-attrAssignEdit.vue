<template> 
    <el-form  :model="funcRegionAttrAssign" label-width="70px" :rules="validateRules" ref="funcRegionAttrAssignForm">
        <el-row :gutter="4" v-if="assignObject != null">
            <el-col :span="24">
                <el-form-item label="属性" required prop="attributeId">
                    <el-select v-model="funcRegionAttrAssign.attributeId" class="m-2" disabled placeholder="Select" size="small" >
                           <el-option v-for="item in assignObject.biDataSet.details" :key="item.id" :label="item.content.name" :value="item.id" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="displayName">
                    <el-input  v-model="funcRegionAttrAssign.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="displayType">
                      <el-select v-model="funcRegionAttrAssign.displayType" disabled class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in displayTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="是否维度" prop="isDimension">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.isDimension" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="合并" prop="isMerge">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.isMerge" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="行数">
                    <el-input  v-model="funcRegionAttrAssign.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="列数">
                    <el-input  v-model="funcRegionAttrAssign.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="宽度">
                    <el-input  v-model="funcRegionAttrAssign.displayWidth"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'Q'">
            <el-col :span="24">
                <el-form-item label="累加列" prop="sumCol">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.sumCol" class="m-2" clearable placeholder="Select" size="small" >
                           <el-option v-for="item in leafAttrs" :key="(item.dataSetDetail.detailContentAlias != null && item.dataSetDetail.detailContentAlias.length > 0) ? ( item.dataSetDetail.content.code + '::' + item.dataSetDetail.detailContentAlias) : item.dataSetDetail.content.code" :label="item.displayName" :value="(item.dataSetDetail.detailContentAlias != null && item.dataSetDetail.detailContentAlias.length > 0) ? ( item.dataSetDetail.content.code + '::' + item.dataSetDetail.detailContentAlias) : item.dataSetDetail.content.code" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'Q'">
            <el-col :span="24">
                <el-form-item label="维度列" prop="sumByCol">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.sumByCol" class="m-2" clearable placeholder="Select" size="small" >
                           <el-option v-for="item in leafAttrs" :key="(item.dataSetDetail.detailContentAlias != null && item.dataSetDetail.detailContentAlias.length > 0) ? ( item.dataSetDetail.content.code + '::' + item.dataSetDetail.detailContentAlias) : item.dataSetDetail.content.code" :label="item.displayName" :value="(item.dataSetDetail.detailContentAlias != null && item.dataSetDetail.detailContentAlias.length > 0) ? ( item.dataSetDetail.content.code + '::' + item.dataSetDetail.detailContentAlias) : item.dataSetDetail.content.code" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'Q' || funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="显示格式" prop="displayFormat">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.displayFormat" class="m-2" clearable placeholder="Select" size="small" >
                           <el-option v-for="item in displayFormatOptions" :key="item.value" :label="item.label" :value="item.value" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayCfgJSON.displayFormat == 'P' || funcRegionAttrAssign.displayCfgJSON.displayFormat == 'N' || funcRegionAttrAssign.displayCfgJSON.displayFormat == 'D'">
            <el-col :span="24">
                <el-form-item label="格式配置" prop="formatCfg">
                    <el-input  v-model="funcRegionAttrAssign.displayCfgJSON.formatCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="样式">
                    <el-input type="textarea" rows="5" v-model="funcRegionAttrAssign.displayCfg" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

export default {
    props: ['funcRegionAttrAssign','objectAssigns', 'assignObject', 'reportCfgData'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
            
        }
    },
    computed: {
        leafAttrs: {
            get(){
                if(this.reportCfgData == null || this.reportCfgData.reportAttrs == null) {
                    return null;
                }
                var leafAttrs = [];
                this.findLeafAttrs(this.reportCfgData.reportAttrs, leafAttrs);
                return leafAttrs;
            }
        }
    },
    components: {
        
    },
    
    data() {   
        const yesOrNoOptions = ref(null);

        const displayTypeOptions = ref(null);

        const displayFormatOptions = ref(null);

        const validateRules = ref({
            "objectId": [
                { required: true, message: '归属对象不能为空！', trigger: 'blur' }
            ],
            "attributeId": [
                { required: true, message: '绑定属性不能为空！', trigger: 'blur' }
            ],
            "displayName": [
                { required: true, message: '显示名称不能为空！', trigger: 'blur' }
            ],
            "displayType": [
                { required: true, message: '显示类型不能为空！', trigger: 'blur' }
            ],
        });
        
        return {
            yesOrNoOptions,
            displayTypeOptions,
            displayFormatOptions,
            validateRules
        }
    },
    watch: {
        'funcRegionAttrAssign.displayCfgJSON': {
            handler: function(value, old) {
                if(value == null) {
                    this.funcRegionAttrAssign.displayCfgJSON = {};
                }
                this.funcRegionAttrAssign.displayCfg = JSON.stringify(this.funcRegionAttrAssign.displayCfgJSON);
            },
            deep: true
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','BI_DISPLAY_TYPE','BI_DATA_DISPLAY_FORMAT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.displayTypeOptions = data.data['BI_DISPLAY_TYPE'];
                this.displayFormatOptions = data.data['BI_DATA_DISPLAY_FORMAT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
        findLeafAttrs(rootAttrs, leafAttrs) {
            if(rootAttrs == null || rootAttrs.length == 0 || leafAttrs == null) {
                return null;
            }
            for(var i in rootAttrs) {
                var attr = rootAttrs[i];
                if(attr.attrAssigns == null || attr.attrAssigns.length == 0) {
                    leafAttrs.push(attr);
                }else {
                    this.findLeafAttrs(attr.attrAssigns, leafAttrs);
                }
            }
        }
    }
 }
</script>