<!-- 功能区域设计器-换行控件 -->
<template>
<div width="99%">
    <el-row :gutter="4">
        <el-col :span="24">
            <br/>
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