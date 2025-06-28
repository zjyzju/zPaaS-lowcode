<!-- 功能区域设计器-锚点控件 -->
<template>
<div width="99%">
    <el-row :gutter="4">
        <el-col :span="24">
            <el-affix :offset="0">
                <el-anchor :offset="-30" :direction="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.anchorDirection != null) ? customizedLayout.layoutCfgJSON.anchorDirection : 'horizontal'">
                    <el-anchor-link v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.anchorNumber != null" href="" v-for="(name,index) in customizedLayout.layoutCfgJSON.anchorNames" @click.prevent="customAnchor(customizedLayout.layoutCfgJSON.anchorValues[index])">
                    {{ customizedLayout.layoutCfgJSON.anchorNames[index] }}
                    </el-anchor-link>
                </el-anchor>
            </el-affix>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24"> </el-col>
    </el-row>
</div>
 </template>

<script>

export default {
    props: ['customizedLayout'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
        	
        }
    },
    computed: {
        
    },
    components: {
        
    },
    watch: {
        
    },
    data() {   
       
    	return {
            
        }
    },
    mounted() {
        if(this.customizedLayout.layoutCfg == null || this.customizedLayout.layoutCfg.trim().length == 0) {
            this.customizedLayout.layoutCfgJSON = {};
        }else {
            this.customizedLayout.layoutCfgJSON = JSON.parse(this.customizedLayout.layoutCfg);
        }
    },
    methods: {
        customAnchor(anchorName) {
            var anchorElement = document.getElementById(anchorName);
            if(anchorElement) {
                anchorElement.scrollIntoView();
            }
        }
    }
}
</script>
<style scoped>

</style>