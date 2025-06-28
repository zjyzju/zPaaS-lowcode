<!-- 功能区域设计器-分隔线控件 -->
<template>
<div width="99%"  :style="'height: 100%;' + ((customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.direction == 'vertical') ? 'display: flex;align-items: center;justify-content: center;' : '')">
    <el-row :gutter="4"  style="height: 100%;">
        <el-col :span="24">
            <el-divider v-if="customizedLayout.layoutCfgJSON != null" border-style="solid" :direction="customizedLayout.layoutCfgJSON.direction != null ? customizedLayout.layoutCfgJSON.direction : 'horizontal'" :content-position="customizedLayout.layoutCfgJSON.contentPosition" :style="'height:'+((customizedLayout.layoutCfgJSON.direction == 'vertical' && parseInt(customizedLayout.layoutCfgJSON.verticalHeight) > 0) ? parseInt(customizedLayout.layoutCfgJSON.verticalHeight)+'px' : '100%') + ';margin-left: 1px;margin-right: 1px;align-self: center;'">
                <span  :id="customizedLayout.name" class="title" v-if="customizedLayout.layoutCfgJSON.contentPosition != null">{{ customizedLayout.name }}</span>
            </el-divider>
        </el-col>
    </el-row>
</div>
 </template>

<script>

export default {
    props: ['customizedLayout'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        // const deleteContainer = (componentIndex) => {
        //     emit('deleteContainer', componentIndex);
        // };

        return {
        	// deleteContainer,
            
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
        
    },
    methods: {
        
    }
}
</script>
<style scoped>

</style>