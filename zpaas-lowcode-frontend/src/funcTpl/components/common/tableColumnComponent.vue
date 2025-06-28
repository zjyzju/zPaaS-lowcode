<!--表格列展示组件-->
<template>
	<!-- 文件上传/下载类型-->
	<div style="display: flex; align-items: center" v-if="displayType == 'F' || displayType == 'G'">
		<span v-if="row[attrCode] != null" v-for="file in JSON.parse(row[attrCode])" class="el-upload-list__item-file-name">
            <el-link type="primary" :href="file.fileUrl">{{file.name}}</el-link> &nbsp;
        </span>
		<span v-else class="el-upload-list__item-file-name">
            &nbsp;
        </span>
	</div>
	<div style="display: flex; align-items: center" v-else-if="row[attrCode+'_label'] != undefined">
		<span :style="(row[attrCode] != null && attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.fontColor != null && attrAssign.displayCfgJSON.fontColor[row[attrCode]] != null)?'color:'+attrAssign.displayCfgJSON.fontColor[row[attrCode]] : ''">
            {{ row[attrCode+'_label'] }}
        </span>
	</div>
	<div style="display: flex; align-items: center" v-else>
		<span :style="(row[attrCode] != null && attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.fontColor != null && attrAssign.displayCfgJSON.fontColor[row[attrCode]] != null)?'color:'+attrAssign.displayCfgJSON.fontColor[row[attrCode]] : ''">
            {{ row[attrCode] }}
        </span>
	</div>
</template>
<script>
import {getLableByValue} from '../../js/common.js'

export default {
    props: ['attrOption','row','attrCode', 'displayType', 'attrAssign'],
    
    components: {
    	
    },
    data() { 
    	
		return {
			
		}
    },
	watch: {
		'row' : function(value, old) {
			this.processLabelValue();
		}
	},
    mounted() {
		this.processLabelValue();
    },
    methods: {
    	processLabelValue() {
			if(this.displayType == 'C' || this.displayType == 'E') {//多选框/级联选择框
				if(this.row[this.attrCode] == null || this.row[this.attrCode] == "") {
					
				}else {//使用“$;”进行分拆
					this.row[this.attrCode] = this.row[this.attrCode].split("$;");
				}
			}else if(this.displayType == 'P' || this.displayType == 'V') {
				if(this.row[this.attrCode+'_label'] == undefined && this.attrCode == 'compositionId') {
					this.row[this.attrCode+'_label'] = null;
				}
			}

			
			if(this.row[this.attrCode] != null && this.row[this.attrCode+'_label'] == null && this.attrOption != null && this.attrOption.options != null && this.attrOption.options.length > 0) {
				var label = getLableByValue(this.row[this.attrCode], this.attrOption.options);
				if(label != null) {
					this.row[this.attrCode+'_label'] = label;
				}
			}else if(this.row[this.attrCode] != null && this.row[this.attrCode+'_label'] == null && this.row[this.attrCode+'_options'] != null && this.row[this.attrCode+'_options'].length > 0) {
				var label = getLableByValue(this.row[this.attrCode], this.row[this.attrCode+'_options']);
				if(label != null) {
					this.row[this.attrCode+'_label'] = label;
				}
			}
		}
    }
}
</script>