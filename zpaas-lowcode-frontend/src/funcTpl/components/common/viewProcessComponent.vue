<!--查看流程信息组件-->
<template>
    <el-row :gutter="4"> 
       <el-col :span="24">
           <div class="content" ref="js-drop-zone">
                <div ref="canvasViewer"  style="width: 100%; height: 55vh"></div>
           </div>
       </el-col>       
    </el-row>
<br/>
    <el-row :gutter="4">
        <el-col :span="20">
            &nbsp;
        </el-col>
        <el-col :span="4">
                <el-button size="small" @click="hideViewPage">取消</el-button>
        </el-col>
    </el-row>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import BpmnViewer from 'camunda-bpmn-js/lib/camunda-platform/Viewer';
import 'camunda-bpmn-js/dist/assets/camunda-platform-viewer.css';

export default {
    props: ['params'],
    
    emits: ['hideViewPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideViewPage = () => {
            emit('hideViewPage')
        };
        
        return {
        	hideViewPage
        }
    },
    
    components: {
        
    },
    
    data() {   
    	const bpmnViewer = ref(null);
    	
        return {
        	bpmnViewer
        }
    },
    mounted() {
    	setTimeout(()=>{
            this.init();
            this.viewProcessInstDiagram()
        },100);
    	
    },
    methods: {
    	init() {
    		const canvasViewer = this.$refs.canvasViewer;
            this.bpmnViewer = new BpmnViewer({
                container: canvasViewer
              });
            // const Logo = canvasViewer.querySelector(".bjs-powered-by");
            // while (Logo.firstChild) {
            //     Logo.removeChild(Logo.firstChild);
            // }
        },
        viewProcessInstDiagram() {
            var url = "/lcdp-proxy/lowcode/be/api/workflow/loadProcessDiagramInfo/" + this.params.processInstId; 
            const canvas = this.bpmnViewer.get('canvas');
            axiosClient.get(url).then((response) => {
                const data = response.data; 
                console.log(data);
                if(data != null && data.status == "200" && data.data != null) {
                   const processDiagramInfo = data.data; 
                    this.bpmnViewer.importXML(processDiagramInfo.bpmnXml).then(() => {
                        canvas.zoom('fit-viewport');
                        for(var i in processDiagramInfo.histActivityIds) {
                            if(processDiagramInfo.histActivityIds[i].indexOf("#multiInstanceBody") != -1) {
                                continue;
                            }
                            canvas.addMarker(processDiagramInfo.histActivityIds[i], 'highlight'); 
                        }
                    }).catch((err)=> {
                        console.log(err);
                    });
                }else {
                    ElMessage.error(`加载流程实例流图信息失败`);
                }
             });
        }
    }
}
</script>

<style type="text/css">
.djs-container .highlight .djs-outline {
   stroke-width: 2px !important;
   stroke: #08c !important;
   fill: #bce2f5 !important;
}
.djs-container .highlight .djs-visual>:nth-child(1) {
   fill: #bce2f5 !important;
}
</style>