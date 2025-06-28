<!-- 功能区域设计器-换行控件 -->
<template>
<div width="99%">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTabContainer">
                <div style="height:30px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
                <el-link class="floatDeleteTab" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
</div>
 </template>

<script>

export default {
    props: ['componentIndex', 'brComponent'],
    
    emits: ['deleteContainer', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const deleteContainer = (componentIndex) => {
            emit('deleteContainer', componentIndex);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        return {
        	deleteContainer,
            onCustomizedLayoutSelected
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
        if(this.brComponent.layoutCfg == null || this.brComponent.layoutCfg.trim().length == 0) {
            this.brComponent.layoutCfgJSON = {}; 
        }else {
            this.brComponent.layoutCfgJSON = JSON.parse(this.brComponent.layoutCfg);
        }
    },
    methods: {
        
    }
    
 }
</script>
<style scoped>

.floatDeleteTab{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -25px;
    z-index: 99;
}

.floatDeleteTabContainer{
    &:hover .floatDeleteTab{
        display: block;  
    }
}

</style>