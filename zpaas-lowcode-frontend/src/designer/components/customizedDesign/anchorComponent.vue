<!-- 功能区域设计器-锚点控件 -->
<template>
<div width="99%">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTabContainer">
                <el-icon @click="onCustomizedLayoutSelected(anchorComponent)"><LocationFilled /></el-icon>
                <el-link class="floatDeleteTab" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-affix :offset="60">
                <el-anchor :offset="70" :direction="(anchorComponent.layoutCfgJSON != null && anchorComponent.layoutCfgJSON.anchorDirection != null) ? anchorComponent.layoutCfgJSON.anchorDirection : 'horizontal'">
                    <el-anchor-link v-if="anchorComponent.layoutCfgJSON != null && anchorComponent.layoutCfgJSON.anchorNumber != null" href="" v-for="(name,index) in anchorComponent.layoutCfgJSON.anchorNames" @click.prevent="customAnchor(anchorComponent.layoutCfgJSON.anchorValues[index])">
                    {{ anchorComponent.layoutCfgJSON.anchorNames[index] }}
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
    props: ['componentIndex', 'anchorComponent'],
    
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
        'anchorComponent.layoutCfgJSON.anchorNumber': function(value, old) {
            if(old == null) {
                return;
            }
            var anchorNumber = parseInt(value);
            var originNumber = parseInt(old);
            if(anchorNumber > originNumber) {
                for(var i=originNumber; i < anchorNumber; i++) {
                    this.anchorComponent.layoutCfgJSON.anchorNames[i] = '';
                    this.anchorComponent.layoutCfgJSON.anchorValues[i] = '';
                }
            }else {
                this.anchorComponent.layoutCfgJSON.anchorNames.splice(anchorNumber, (originNumber-anchorNumber));
                this.anchorComponent.layoutCfgJSON.anchorValues.splice(anchorNumber, (originNumber-anchorNumber));
            }
            this.anchorComponent.layoutCfg = JSON.stringify(this.anchorComponent.layoutCfgJSON);
        }
    },
    data() {   
       
    	return {
            
        }
    },
    mounted() {
        if(this.anchorComponent.layoutCfg == null || this.anchorComponent.layoutCfg.trim().length == 0) {
            this.anchorComponent.layoutCfgJSON = {};
        }else {
            this.anchorComponent.layoutCfgJSON = JSON.parse(this.anchorComponent.layoutCfg);
        }
        if(this.anchorComponent.layoutCfgJSON.anchorNumber == null || parseInt(this.anchorComponent.layoutCfgJSON.anchorNumber) <=0 ) {
            this.anchorComponent.layoutCfgJSON.anchorNumber = 1;
            this.anchorComponent.layoutCfgJSON.anchorNames = [];
            this.anchorComponent.layoutCfgJSON.anchorValues = [];
            this.anchorComponent.layoutCfgJSON.anchorNames[0] = '锚点1';
            this.anchorComponent.layoutCfgJSON.anchorValues[0] = 'Anchor1';
            this.anchorComponent.layoutCfg = JSON.stringify(this.anchorComponent.layoutCfgJSON);
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

.floatDeleteTab{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -35px;
    z-index: 99;
}

.floatDeleteTabContainer{
    &:hover .floatDeleteTab{
        display: block;  
    }
}

</style>