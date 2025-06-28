<!-- 系统框架导航页 -->
<template>
<el-scrollbar  height="90vh">
<div>
<el-row>
    <el-col :span="24">
        <el-radio-group v-model="isCollapse" style="margin-bottom: 5px;margin-top: 5px;display:none">
            <el-radio-button :value="true" style="width:50%">expand</el-radio-button>
            <el-radio-button :value="false" style="width:50%">collapse</el-radio-button>
        </el-radio-group>
        <el-menu default-active="6" class="el-menu-vertical-demo" :collapse="isCollapse">
            <el-sub-menu v-if="loginInfo.menuRights != null" v-for="(menuRight) in loginInfo.menuRights" :index="menuRight.id">
                <template #title>
                    <el-icon><Folder /></el-icon>
                    <span>{{menuRight.rightName}}</span>
                </template>
                <div v-if="menuRight.subRights != null" v-for="(subMenuRight) in menuRight.subRights">
                    <el-sub-menu v-if="subMenuRight.rightType == '0'"  :index="subMenuRight.id">
	                    <template #title>
	                        <el-icon><Folder /></el-icon>
	                        <span >{{subMenuRight.rightName}}</span>
	                    </template>
	                    <div v-if="(subMenuRight).subRights != null" v-for="(subSubMenuRight) in subMenuRight.subRights">
	                        <el-menu-item  v-if="subSubMenuRight.rightType == '1'" @click="handleClick(subSubMenuRight)" :index="subSubMenuRight.id">
		                        {{subSubMenuRight.rightName}}
		                    </el-menu-item>
	                    </div>
	                </el-sub-menu>
	                <el-menu-item  v-if="subMenuRight.rightType == '1'" @click="handleClick(subMenuRight)" :index="subMenuRight.id">
                        {{subMenuRight.rightName}}
                    </el-menu-item>
                </div>
            </el-sub-menu>
        </el-menu>
    </el-col>
</el-row>
</div>
</el-scrollbar>
</template>

<script >
import { ref } from 'vue';

export default {
    props: ['loginInfo'],
    
    emits: ['handleClick'],
    
    setup (props, {attrs, emit, slots}) {
        const handleClick = (right) => {
            emit('handleClick', right);
        };
        
        return {
        	handleClick
        };
    },
    components: {
        
    },
    watch: {        
        
    
    },
    data() {            
    	const isCollapse = ref(false);
    	
    	return {
    		isCollapse
    	}
    },
    methods: {
    	
    }
    
}
</script>