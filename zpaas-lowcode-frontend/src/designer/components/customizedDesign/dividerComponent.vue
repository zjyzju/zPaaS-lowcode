<!-- 功能区域设计器-分隔线控件 -->
<template>
<div width="99%" :style="'height: 100%;' + ((dividerComponent.layoutCfgJSON != null && dividerComponent.layoutCfgJSON.direction == 'vertical') ? 'display: flex;align-items: center;justify-content: center;' : '')">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTabContainer">
                <el-divider v-if="dividerComponent.layoutCfgJSON != null" border-style="solid" :direction="dividerComponent.layoutCfgJSON.direction != null ? dividerComponent.layoutCfgJSON.direction : 'horizontal'" :content-position="dividerComponent.layoutCfgJSON.contentPosition" :style="'height:'+((dividerComponent.layoutCfgJSON.direction == 'vertical' && parseInt(dividerComponent.layoutCfgJSON.verticalHeight) > 0) ? parseInt(dividerComponent.layoutCfgJSON.verticalHeight)+'px' : ((dividerComponent.layoutCfgJSON.direction != null && dividerComponent.layoutCfgJSON.direction == 'vertical') ? '100px': '100%')) + ';margin-left: 1px;margin-right: 1px;align-self: center;'" @click="onCustomizedLayoutSelected(dividerComponent)">
                    <span :id="dividerComponent.name" class="title" v-if="dividerComponent.layoutCfgJSON.contentPosition != null">{{ dividerComponent.name }}</span>
                </el-divider>
                <el-link :class="(dividerComponent.layoutCfgJSON != null && dividerComponent.layoutCfgJSON.direction == 'vertical') ? 'floatDeleteTabVertical': 'floatDeleteTab'" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
</div>
 </template>

<script>

export default {
    props: ['componentIndex', 'dividerComponent'],
    
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
        if(this.dividerComponent.layoutCfg == null || this.dividerComponent.layoutCfg.trim().length == 0) {
            this.dividerComponent.layoutCfgJSON = {};
        }else {
            this.dividerComponent.layoutCfgJSON = JSON.parse(this.dividerComponent.layoutCfg);
        }
        console.log((this.dividerComponent.layoutCfgJSON.direction == 'vertical' && parseInt(this.dividerComponent.layoutCfgJSON.verticalHeight) > 0) ? parseInt(this.dividerComponent.layoutCfgJSON.verticalHeight)+'px' : '100%');
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
    margin-top: -80px;
    z-index: 99;
}

.floatDeleteTabVertical{
    display: none;
    float: right;
    margin-left: -100px;
    z-index: 99;
}

.floatDeleteTabContainer{
    &:hover .floatDeleteTab{
        display: block;  
    };
}

.floatDeleteTabContainer{
    &:hover .floatDeleteTabVertical{
        display: block;  
    }
}

</style>