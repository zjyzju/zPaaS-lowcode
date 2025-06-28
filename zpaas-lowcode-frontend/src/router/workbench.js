import { createRouter,createWebHashHistory} from "vue-router";

const blank = () => import("@/workbench/components/workbench-blank.vue");
//管理对象编辑
const editManagedObject = () => import("@/workbench/components/domainModel/workbench-managedObjectEdit.vue");
//流程资源编辑
const editWorkflowProcessResource = () => import("@/workbench/components/integrate/workbench-workflowProcessResourceEdit.vue");
//kafka消息消费服务编辑
const editKafkaMessageConsumer = () => import("@/workbench/components/integrate/workbench-kafkaMessageConsumerEdit.vue");
//动态映射编辑
const editDynamicMapping = () => import("@/workbench/components/dynamicMapping/workbench-dynamicMappingEdit.vue");
//数据库对象编辑
const editDbSchema = () => import("@/workbench/components/dbMgr/workbench-dbSchemaEdit.vue");
//数据库表对象编辑
const editDbTable = () => import("@/workbench/components/dbMgr/workbench-dbTableEdit.vue");
//技术服务资源对象编辑
const editServerResource = () => import("@/workbench/components/integrate/workbench-serverResourceEdit.vue");
//页面对象资源编辑
const editFuncDefine = () => import("@/workbench/components/funcMgr/workbench-funcDefineEdit.vue");
//组合页面对象资源编辑
const editComboFuncDefine = () => import("@/workbench/components/funcMgr/workbench-comboFuncDefineEdit.vue");
//字典资源对象编辑
const editDictResource = () => import("@/workbench/components/integrate/workbench-dictResourceEdit.vue");
//安全密钥资源对象编辑
const editSecurityKeyResource = () => import("@/workbench/components/integrate/workbench-securityKeyResourceEdit.vue");
//数据模型对象编辑
const editDataModel = () => import("@/workbench/components/dataSetMgr/workbench-dataModelEdit.vue");
//数据集对象编辑
const editDataSet = () => import("@/workbench/components/dataSetMgr/workbench-dataSetEdit.vue");
//定时任务编辑
const editCronJob = () => import("@/workbench/components/integrate/workbench-cronJobEdit.vue");

const routes = [
  {path: "/", redirect: "/blank"},
  {
    path: "/blank",
    name: "blank",
    component: blank
  },
  {
    path: "/editManagedObject",
    name: "editManagedObject",
    component: editManagedObject
  },
  {
    path: "/editWorkflowProcessResource",
    name: "editWorkflowProcessResource",
    component: editWorkflowProcessResource
  },
  {
    path: "/editKafkaMessageConsumer",
    name: "editKafkaMessageConsumer",
    component: editKafkaMessageConsumer
  },
  {
    path: "/editDynamicMapping",
    name: "editDynamicMapping",
    component: editDynamicMapping
  },
  {
    path: "/editDbSchema",
    name: "editDbSchema",
    component: editDbSchema
  },
  {
    path: "/editDbTable",
    name: "editDbTable",
    component: editDbTable
  },
  {
    path: "/editServerResource",
    name: "editServerResource",
    component: editServerResource
  },
  {
    path: "/editFuncDefine",
    name: "editFuncDefine",
    component: editFuncDefine
  },
  {
    path: "/editComboFuncDefine",
    name: "editComboFuncDefine",
    component: editComboFuncDefine
  },
  {
    path: "/editDictResource",
    name: "editDictResource",
    component: editDictResource
  },
  {
    path: "/editSecurityKeyResource",
    name: "editSecurityKeyResource",
    component: editSecurityKeyResource
  },
  {
    path: "/editDataModel",
    name: "editDataModel",
    component: editDataModel
  },
  {
    path: "/editDataSet",
    name: "editDataSet",
    component: editDataSet
  },
  {
    path: "/editCronJob",
    name: "editCronJob",
    component: editCronJob
  }
];

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
});

export default router;