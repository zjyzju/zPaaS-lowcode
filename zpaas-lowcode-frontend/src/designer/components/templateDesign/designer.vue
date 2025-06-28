<!-- 功能区域设计器 -->
<template>
    <el-form  v-if="funcRegion.regionAttrAssigns != null" :model="funcRegion" label-width="120px">
        <!--主对象-->
        <formDesigner v-if="funcRegion.tplRegion.regionType != 'MR' && funcRegion.tplRegion.regionType.startsWith('M')" :attrAssignInfo="attrAssignInfo" :funcRegion="funcRegion" :assignObject="mainObject" @onChangeEnd="onChangeEnd" @deleteAttr="deleteAttr" @onAttrSelected="onAttrSelected"/>

        <br/>
        <!--从对象-->
        <el-row :gutter="4" v-if="funcRegion.tplRegion.regionType != 'MR' && funcRegion.tplRegion.regionType != 'MQ' && subObjects != null && subObjects.length > 0" v-for="(subObject, subObjectIndex) in subObjects">
            <el-col :span="24">
                <el-row :gutter="4">
                    <el-col :span="24">
                        &nbsp;
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="24">
                <!--表头区-->
                <titleAreaDesigner :funcRegion="funcRegion" :assignObject="subObject" @deleteRegionOperation="deleteRegionOperation" @onOperationSelected="onOperationSelected"/>
                <!-- 一对多，列表形式 -->
                <tableDesigner  v-if="subObject.mainRelAttribute.isListAttr=='Y' && !funcRegion.tplRegion.regionType.startsWith('S')" :attrAssignInfo="subObjectAttrList[subObjectIndex]" :funcRegion="funcRegion" :assignObject="subObject" @onChangeEnd="onChangeEnd" @deleteAttr="deleteAttr" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @deleteRegionOperation="deleteRegionOperation"/>
                
                <!-- 一对一，form形式 -->
                <formDesigner v-if="subObject.mainRelAttribute.isListAttr=='N' || funcRegion.tplRegion.regionType.startsWith('S')" :attrAssignInfo="subObjectAttrList[subObjectIndex]" :funcRegion="funcRegion" :assignObject="subObject" @onChangeEnd="onChangeEnd" @deleteAttr="deleteAttr" @onAttrSelected="onAttrSelected"/>
                <br/>
                <!--从对象编辑区按钮区-->
                <buttonAreaDesigner v-if="funcRegion.tplRegion.regionType.startsWith('S')" :funcRegion="funcRegion" :assignObject="subObject" @deleteRegionOperation="deleteRegionOperation" @onOperationSelected="onOperationSelected" />
            </el-col>
        </el-row>
        <br/>
        <!--按钮区-->
        <el-row :gutter="4">
            <el-col :span="8">
                &nbsp;
            </el-col>
            <el-col :span="16">
                <span v-if="funcRegion.tplRegion.regionType.startsWith('S')">以下为从对象编辑区公共操作：</span>
            </el-col>
        </el-row>
        <buttonAreaDesigner v-if="funcRegion.tplRegion.regionType != 'MR'" :funcRegion="funcRegion" :assignObject="mainObject" @deleteRegionOperation="deleteRegionOperation" @onOperationSelected="onOperationSelected" />

        <!--查询结果区-->
        <titleAreaDesigner  v-if="funcRegion.tplRegion.regionType == 'MR'" :funcRegion="funcRegion" :assignObject="mainObject" @deleteRegionOperation="deleteRegionOperation" @onOperationSelected="onOperationSelected"/>
        <tableDesigner  v-if="funcRegion.tplRegion.regionType == 'MR'" :attrAssignInfo="attrAssignInfo" :funcRegion="funcRegion" :assignObject="mainObject" @onChangeEnd="onChangeEnd" @deleteAttr="deleteAttr" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @deleteRegionOperation="deleteRegionOperation"/>
        <buttonAreaDesigner v-if="funcRegion.tplRegion.regionType == 'MR' && funcRegion.tplRegion.id=='15'" :funcRegion="funcRegion" :assignObject="mainObject" @deleteRegionOperation="deleteRegionOperation" @onOperationSelected="onOperationSelected" />
    </el-form>
 </template>
<script>
import { ref } from 'vue';

import formDesigner from './formDesigner.vue'
import tableDesigner from './tableDesigner.vue'
import buttonAreaDesigner from './buttonAreaDesigner.vue'
import titleAreaDesigner from './titleAreaDesigner.vue'

export default {
    props: ['showRegionDesignerPage','funcRegion', 'mainObject', 'attrAssignInfo', 'subObjects', 'subObjectAttrList'],
    
    emits: ['deleteAttr', 'onChangeEnd', 'onAttrSelected','onOperationSelected', 'deleteRegionOperation'],
    
    setup (props, {attrs, emit, slots}) {
        const onChangeEnd = (event) => {
            emit('onChangeEnd', event);
        };

        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        const onOperationSelected = (regionOperation) => {
            emit('onOperationSelected', regionOperation);
        };

        const deleteAttr = (srcObject, attr) => {
            emit('deleteAttr', srcObject, attr);
        };

        const deleteRegionOperation = (index) => {
            emit('deleteRegionOperation', index);
        };

        return {
        	onChangeEnd,
            onAttrSelected,
            onOperationSelected,
            deleteAttr,
            deleteRegionOperation
        }
    },
    computed: {
        
    },
    components: {
    	formDesigner,
        tableDesigner,
        buttonAreaDesigner,
        titleAreaDesigner
    },
    
    data() {   
        const regionData = ref({});
        
    	return {
            regionData
        }
    },
    mounted() {
    	//console.log(this.funcRegion);
    },
    methods: {
    	
    }
    
 }
</script>
