package cn.zpaas.lowcode.be.ide.vo;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;

/**
 * AI生成流程节点解析结果类
 *
 * @author zjy
 * createTime 2026年1月31日-12:59:53
 */
public class FlowNodeParser {
    //流程节点类型及部分参数
    public static final String NODE_TYPE_CONDITION = "6";
    public static final String NODE_TYPE_FOR_EACH = "8";
    public static final String NODE_TYPE_EXCEL_IMPORT = "18";
    public static final String NODE_TYPE_JSON_IMPORT = "28";
    public static final String NODE_TYPE_TEXT_IMPORT = "30";
    public static final String PROCESS_MODE_F = "F";

    //流程配置信息关键字定义
    public static final String NODE_TYPE_ID = "nodeTypeId";
    public static final String FLOW_DESC = "flowDesc";
    public static final String NODE_CFG = "nodeCfg";
    public static final String PARAM_CFG = "节点配置参数";
    public static final String PRE_CFG = "节点预处理参数";
    public static final String POST_CFG = "节点后处理参数";
    public static final String RESULT_TYPE_CFG = "节点输出结果类型信息";
    public static final String SUB_BUSINESS_FLOW = "subBusinessFlow";
    public static final String PROCESS_MODE = "processMode";

    //开始/结束节点的大小定义
    public static final int EVENT_NODE_WIDTH = 36;
    public static final int EVENT_NODE_HEIGHT = 36;
    //activity节点的大小定义
    public static final int ACTIVITY_NODE_WIDTH = 100;
    public static final int ACTIVITY_NODE_HEIGHT = 80;
    //shape间隔
    public static final int SHAPE_INTERVAL = 40;
    //默认开始节点的坐标
    public static final int START_X = 50;
    public static final int START_Y = 150;
    
    private String flowNodesStr = ""; //生成BPMN文件的流程节点字符串
    private String bpmnShapesStr = "";//生成BPMN文件的BPMN形状字符串

    private int subProcessLevelCount;//子流程层级数 = upper + lower + 1(本身)
    private int upperSubProcessLevelCount;//放置在上部的子流程层级数
    private int lowerSubProcessLevelCount;//放置在下部的子流程层级数

    private int processShapeWidth;//当前流程图形的宽度

    private Map<String, FlowNodeParser> subProcessParseResult = new HashMap<>();

    /**
     * 计算主流程占用的宽度
     * @param nodeCount
     * @return
     */
    public static int calMainFlowWidth(int nodeCount) {
        return (ACTIVITY_NODE_WIDTH + SHAPE_INTERVAL) * nodeCount + (EVENT_NODE_WIDTH + SHAPE_INTERVAL) * 2 + SHAPE_INTERVAL;//主流程节点需要占用的宽度
    }

    /**
     * 计算主流程占用的宽度，考虑子流程的情况
     * @param nodeCount
     * @return
     */
    public static int calMainFlowWidthWithSub(int i, int subProcessShapeWidth) {
        return (ACTIVITY_NODE_WIDTH + SHAPE_INTERVAL) * i + (EVENT_NODE_WIDTH + SHAPE_INTERVAL) + SHAPE_INTERVAL + subProcessShapeWidth + SHAPE_INTERVAL;
    }

    /**
     * 子流程形状的高度
     * @param subProcessLevelCount
     * @return
     */
    public static int calSubProcessShapeWidth(int subProcessLevelCount) {
        return (ACTIVITY_NODE_HEIGHT + 2 * SHAPE_INTERVAL) * subProcessLevelCount + SHAPE_INTERVAL * (subProcessLevelCount - 1);//子流程形状的高度
    }

    /**
     * 如果节点类型为6条件判断分支节点 or 8循环节点 or ((18Excel文件导入 or 28JSON文件导入 or 30Txt文件导入）and 处理模式processMode为F流式处理)，存在子流程
     * @param nodeTypeId
     * @param paramCfg
     * @return
     */
    public static boolean hasSubProcessNode(String nodeTypeId, JsonObject paramCfg) {
        if(NODE_TYPE_CONDITION.equals(nodeTypeId) || NODE_TYPE_FOR_EACH.equals(nodeTypeId) || 
                ((NODE_TYPE_EXCEL_IMPORT.equals(nodeTypeId) || NODE_TYPE_JSON_IMPORT.equals(nodeTypeId) || NODE_TYPE_TEXT_IMPORT.equals(nodeTypeId)) && 
                PROCESS_MODE_F.equals(JsonUtils.getString(paramCfg, PROCESS_MODE)))){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取流程节点类型id
     * @param flowNode
     * @return
     */
    public static String getNodeTypeId(JsonObject flowNode) {
        return JsonUtils.getString(flowNode, NODE_TYPE_ID);//节点类型id
    }

    /**
     * 获取流程节点说明
     * @param flowNode
     * @return
     */
    public static String getNodeDesc(JsonObject flowNode) {
        return JsonUtils.getString(flowNode, FLOW_DESC);//节点说明
    }

    /**
     * 获取流程节点配置信息
     * @param flowNode
     * @return
     */
    public static JsonObject getNodeCfg(JsonObject flowNode) {
        return JsonUtils.getJsonObject(flowNode, NODE_CFG);//节点配置信息
    }

    /**
     * 获取流程节点参数配置信息
     * @param flowNode
     * @return
     */
    public static JsonObject getParamCfg(JsonObject nodeCfg) {
        return JsonUtils.getJsonObject(nodeCfg, PARAM_CFG);//节点参数配置信息
    }

    /**
     * 获取流程节点预处理参数配置信息
     * @param flowNode
     * @return
     */
    public static JsonObject getPreCfg(JsonObject nodeCfg) {
        return JsonUtils.getJsonObject(nodeCfg, PRE_CFG);//节点预处理参数配置信息
    }

    /**
     * 获取流程节点后处理参数配置信息
     * @param flowNode
     * @return
     */
    public static JsonObject getPostCfg(JsonObject nodeCfg) {
        return JsonUtils.getJsonObject(nodeCfg, POST_CFG);//节点后处理参数配置信息
    }

    /**
     * 获取流程节点输出结果类型配置信息
     * @param flowNode
     * @return
     */
    public static JsonObject getResultCfg(JsonObject nodeCfg) {
        return JsonUtils.getJsonObject(nodeCfg, RESULT_TYPE_CFG);//节点输出结果类型配置信息
    }

    /**
     * 获取流程节点的子流程节点列表
     * @param flowNode
     * @return
     */
    public static JsonArray getSubProcessNodes(JsonObject paramCfg) {
        return JsonUtils.getJsonArray(paramCfg, SUB_BUSINESS_FLOW);//子流程节点列表
    }

    public void addSubParseResult(String i, FlowNodeParser subParseResult) {
        this.subProcessParseResult.put(i, subParseResult);
    }

    public FlowNodeParser getSubParseResult(String i) {
        return this.subProcessParseResult.get(i);
    }

    public String getFlowNodesStr() {
        return flowNodesStr;
    }

    public void setFlowNodesStr(String flowNodesStr) {
        this.flowNodesStr = flowNodesStr;
    }

    public String getBpmnShapesStr() {
        return bpmnShapesStr;
    }

    public void setBpmnShapesStr(String bpmnShapesStr) {
        this.bpmnShapesStr = bpmnShapesStr;
    }

    public int getSubProcessLevelCount() {
        return subProcessLevelCount;
    }

    public void setSubProcessLevelCount(int subProcessLevelCount) {
        this.subProcessLevelCount = subProcessLevelCount;
    }

    public int getUpperSubProcessLevelCount() {
        return upperSubProcessLevelCount;
    }

    public void setUpperSubProcessLevelCount(int upperSubProcessLevelCount) {
        this.upperSubProcessLevelCount = upperSubProcessLevelCount;
    }

    public int getLowerSubProcessLevelCount() {
        return lowerSubProcessLevelCount;
    }

    public void setLowerSubProcessLevelCount(int lowerSubProcessLevelCount) {
        this.lowerSubProcessLevelCount = lowerSubProcessLevelCount;
    }

    public int getProcessShapeWidth() {
        return processShapeWidth;
    }

    public void setProcessShapeWidth(int processShapeWidth) {
        this.processShapeWidth = processShapeWidth;
    }

    public Map<String, FlowNodeParser> getSubProcessParseResult() {
        return subProcessParseResult;
    }

    public void setSubProcessParseResult(Map<String, FlowNodeParser> subProcessParseResult) {
        this.subProcessParseResult = subProcessParseResult;
    }

    
}
