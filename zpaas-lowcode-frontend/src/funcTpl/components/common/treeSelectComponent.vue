<!--下拉树形选择组件-->
<template>
    <el-cascader v-model="selected" :options="options" :disabled="disabled" :show-all-levels="false" clearable  size="small"/>
</template>
<script>
import { ref } from 'vue';


export default {
    props: ['options','regionData','attrCode', 'disabled'],
    
    components: {
    	
    },
    data() { 
    	const selected = ref([]);
    	return {
    		selected
    	}
    },
	watch: {
		'selected': {
            handler: function(val, old){
            	if(val == null || val.length == 0) {
					this.regionData[this.attrCode] = null;
				} else{
					this.regionData[this.attrCode] = val[val.length-1];
				}
            }
        }
	},
    mounted() {
    	if(this.regionData != null && this.regionData[this.attrCode] != null && this.options != null) {
			var temp = [];
			this.initSelected(this.regionData[this.attrCode], this.options, temp);
			if(temp.length > 0) {
				for(var i=temp.length; i--; i>0) {
					this.selected.push(temp[i]);
				}
			}
		}
    },
    methods: {
    	initSelected(value, treeNodes, cvalue) {
    		for(var index in treeNodes ) {
				if(treeNodes[index].value == value) {
					cvalue.push(treeNodes[index].value)
					return true;
				}else {
					if(treeNodes[index].children != null && treeNodes[index].children.length > 0) {
						if(this.initSelected(value, treeNodes[index].children, cvalue)) {
							cvalue.push(treeNodes[index].value)
							return true;
						}
					}
				}
			}
    	},
    }
}
</script>