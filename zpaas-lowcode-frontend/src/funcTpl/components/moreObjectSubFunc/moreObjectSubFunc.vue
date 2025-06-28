<template>
<div class="common-layout">
<el-container> 
    <el-main>
        <moreEditRegion v-if="params.viewType == 'E' && mainRegion != null" :funcRegion="mainRegion" :funcDefine="funcDefine" :regionData="data" @operationExecuted="operationExecuted"/>
        <moreDetailRegion v-if="params.viewType == 'D' && mainRegion != null" :funcRegion="mainRegion" :funcDefine="funcDefine" :regionData="data" @operationExecuted="operationExecuted"/>
        <moreCreateRegion v-if="params.viewType == 'C' && mainRegion != null" :funcRegion="mainRegion" :funcDefine="funcDefine" :regionData="data" @operationExecuted="operationExecuted"/>
    </el-main>
</el-container>
</div>
</template>
<script>
import { ref } from 'vue';
import moreEditRegion from '../moreDomainObjects/region-moreEditRegion.vue'
import moreDetailRegion from '../moreDomainObjects/region-moreDetailRegion.vue'
import moreCreateRegion from '../moreDomainObjects/region-moreCreateRegion.vue'
import {processLabelValue} from '../../js/common.js'

export default {
    props: ['funcDefine', 'params', 'data'],
    
    emits: ['objectUpdated', 'hideViewPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideViewPage = () => {
            emit('hideViewPage')
        };
        const objectUpdated = (updateParam) => {
            emit('objectUpdated', updateParam)
        };
        
        return {
            hideViewPage,
            objectUpdated
        }
    },
    components: {
    	moreEditRegion,
    	moreDetailRegion,
    	moreCreateRegion
    },
    
    data() {   
        const mainRegion = ref(null);
        
        return {
            mainRegion
        }
    },
    mounted() {
    	console.log(this.funcDefine, this.params, this.data);
        this.mainRegion = this.funcDefine.regionMap["17"];//"17"是增/改/查区模板标识，regionMap的Key使用的是区域模板标识
        
        if(this.data != null) {
            processLabelValue(this.mainRegion, this.data, null, true, this.funcDefine);
        }
    },
    methods: {
    	operationExecuted(srcOperation, data, srcData, srcRegion) {
            console.log(srcOperation, data, srcData, srcRegion);
            if(srcOperation.operationType == 'C' || srcOperation.operationType == 'U') {//新增保存/修改保存
                this.objectUpdated(data);
            }else if(srcOperation.operationType == 'N') {//取消等
                this.hideViewPage();
            }
        }
    }
    
 }
</script>