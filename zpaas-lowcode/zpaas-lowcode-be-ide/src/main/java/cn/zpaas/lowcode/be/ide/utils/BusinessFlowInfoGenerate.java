package cn.zpaas.lowcode.be.ide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.be.ide.vo.FlowNodeParser;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;

/**
 * BPMN文件生成工具类
 *
 * @author zjy
 * createTime 2026年1月23日-17:43:43
 */
public class BusinessFlowInfoGenerate {
    public static final String BPMN_PROCESS_ITEMS = "${bpmnProcesssItems}";
    public static final String BPMN_PLANE_ITEMS = "${bpmnPlaneItems}";
    public static final String ID = "${id}";
    public static final String NAME = "${name}";
    public static final String OUTGOING_ITEMS = "${outgoingItems}";
    public static final String INCOMING_ITEMS = "${incomingItems}";
    public static final String SOURCE_REF = "${sourceRef}";
    public static final String TARGET_REF = "${targetRef}";
    public static final String BPMN_LABEL = "${bpmnLabel}";
    public static final String BPMN_ELEMENT = "${bpmnElement}";
    public static final String X = "${x}";
    public static final String Y = "${y}";
    public static final String WIDTH = "${width}";
    public static final String HEIGHT = "${height}";
    public static final String WAY_POINT = "${wayPoints}";

    public static final String START_EVENT_PREFIX = "StartEvent_";
    public static final String END_EVENT_PREFIX = "EndEvent_";
    public static final String SEQUENCE_FLOW_PREFIX = "SequenceFlow_";
    public static final String ACTIVITY_PREFIX = "Activity_";
    public static final String SHAPE_POSTFIX = "_di";
    
    //BPMN文件模板
    public static final String BPMN_FILE_TPL = """
            <?xml version="1.0" encoding="UTF-8"?>
            <bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
                <bpmn:process id="Process_1" isExecutable="false">
                    ${bpmnProcesssItems}
                </bpmn:process>
                <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
                        ${bpmnPlaneItems}
                    </bpmndi:BPMNPlane>
                </bpmndi:BPMNDiagram>
            </bpmn:definitions>
            """;
    //outgoing模板
    public static final String OUTGOING_TPL = """
            <bpmn:outgoing>${id}</bpmn:outgoing>
            """;
    //incoming模板
    public static final String INCOMING_TPL = """
            <bpmn:incoming>${id}</bpmn:incoming>
            """;
    //开始节点模板
    public static final String START_EVENT_TPL = """
            <bpmn:startEvent id="${id}">
                ${outgoingItems}
            </bpmn:startEvent>
            """;
    //结束节点模板
    public static final String END_EVENT_TPL = """
            <bpmn:endEvent id="${id}">
                ${incomingItems}
            </bpmn:endEvent>
            """;
    //任务节点模板
    public static final String TASK_TPL = """
            <bpmn:task id="${id}" name="${name}">
                ${incomingItems}
                ${outgoingItems}
            </bpmn:task>
            """;
    //连接线模板
    public static final String SEQUENCE_FLOW_TPL = """
            <bpmn:sequenceFlow id="${id}" sourceRef="${sourceRef}" targetRef="${targetRef}"/>
            """;
    //子流程模板
    public static final String SUB_PROCESS_TPL = """
            <bpmn:subProcess id="${id}">
                ${incomingItems}
                ${bpmnProcesssItems}
            </bpmn:subProcess>
            """;
    //标签模板
    public static final String BPMN_LABEL_TPL = """
            <bpmndi:BPMNLabel/>
            """;
    //BPMN形状模板
    public static final String BPMN_SHAPE_TPL = """
            <bpmndi:BPMNShape id="${id}" bpmnElement="${bpmnElement}">
				<dc:Bounds x="${x}" y="${y}" width="${width}" height="${height}"/>
				${bpmnLabel}
			</bpmndi:BPMNShape>
            """;
    
    public static final String SUB_PROCESS_SHAPE_TPL = """
            <bpmndi:BPMNShape id="${id}" bpmnElement="${bpmnElement}" isExpanded="true">
				<dc:Bounds x="${x}" y="${y}" width="${width}" height="${height}"/>
				${bpmnLabel}
			</bpmndi:BPMNShape>
            """;
    
    //路径点模板
    public static final String WAY_POINT_TPL = """
            <di:waypoint x="${x}" y="${y}"/>
            """;
    //BPMN边缘模板
    public static final String BPMN_EDGE_TPL = """
            <bpmndi:BPMNEdge id="${id}" bpmnElement="${bpmnElement}">
				${wayPoints}
			</bpmndi:BPMNEdge>
            """;

    /**
     * 生成outgoing节点
     * @param outgoingId
     * @return
     */
    public static String genOutgoing(String outgoingId) {
        return OUTGOING_TPL.replace(ID, outgoingId);
    }

    /**
     * 生成incoming节点
     * @param incomingId
     * @return
     */
    public static String genIncoming(String incomingId) {
        return INCOMING_TPL.replace(ID, incomingId);
    }

    /**
     * 生成开始节点
     * @param id
     * @param outgoingItems
     * @return
     */
    public static String genStartEvent(String id, String outgoingItems) {
        return START_EVENT_TPL.replace(ID, id).replace(OUTGOING_ITEMS, outgoingItems);
    }

    /**
     * 生成结束节点
     * @param id
     * @param incomingItems
     * @return
     */
    public static String genEndEvent(String id, String incomingItems) {
        return END_EVENT_TPL.replace(ID, id).replace(INCOMING_ITEMS, incomingItems);
    }

    /**
     * 生成任务节点
     * @param id
     * @param name
     * @param outgoingItems
     * @param incomingItems
     * @return
     */
    public static String genTask(String id, String name, String outgoingItems, String incomingItems) {
        return TASK_TPL.replace(ID, id).replace(NAME, name).replace(OUTGOING_ITEMS, outgoingItems).replace(INCOMING_ITEMS, incomingItems);
    }

    /**
     * 生成连接线
     * @param id
     * @param sourceRef
     * @param targetRef
     * @return
     */
    public static String genSequenceFlow(String id, String sourceRef, String targetRef) {
        return SEQUENCE_FLOW_TPL.replace(ID, id).replace(SOURCE_REF, sourceRef).replace(TARGET_REF, targetRef);
    }

    /**
     * 生成子流程节点
     * @param id
     * @param incomingItems
     * @param bpmnProcessItems
     * @return
     */
    public static String genSubProcess(String id, String incomingItems, String bpmnProcessItems) {
        return SUB_PROCESS_TPL.replace(ID, id).replace(INCOMING_ITEMS, incomingItems).replace(BPMN_PROCESS_ITEMS, bpmnProcessItems);
    }

    /**
     * 生成label节点
     * @return
     */
    public static String genBpmnLabel() {
        return BPMN_LABEL_TPL;
    }

    /**
     * 生成bpmn shape节点
     * @param id
     * @param bpmnElementId
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bpmnLabel
     * @return
     */
    public static String genBpmnShape(String id, String bpmnElementId, int x, int y, int width, int height, String bpmnLabel) {
        return BPMN_SHAPE_TPL.replace(ID, id).replace(BPMN_ELEMENT, bpmnElementId).replace(X, String.valueOf(x)).replace(Y, String.valueOf(y)).replace(WIDTH, String.valueOf(width)).replace(HEIGHT, String.valueOf(height)).replace(BPMN_LABEL, bpmnLabel);
    }

    /**
     * 生成bpmn shape节点
     * @param id
     * @param bpmnElementId
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bpmnLabel
     * @return
     */
    public static String genSubProcessShape(String id, String bpmnElementId, int x, int y, int width, int height, String bpmnLabel) {
        return SUB_PROCESS_SHAPE_TPL.replace(ID, id).replace(BPMN_ELEMENT, bpmnElementId).replace(X, String.valueOf(x)).replace(Y, String.valueOf(y)).replace(WIDTH, String.valueOf(width)).replace(HEIGHT, String.valueOf(height)).replace(BPMN_LABEL, bpmnLabel);
    }

    /**
     * 生成way point节点
     * @param x
     * @param y
     * @return
     */
    public static String genWayPoint(int x, int y) {
        return WAY_POINT_TPL.replace(X, String.valueOf(x)).replace(Y, String.valueOf(y));
    }

    /**
     * 生成bpmn edge节点
     * @param id
     * @param bpmnElementId
     * @param wayPoints
     * @return
     */
    public static String genBpmnEdge(String id, String bpmnElementId, String wayPoints) {
        return BPMN_EDGE_TPL.replace(ID, id).replace(BPMN_ELEMENT, bpmnElementId).replace(WAY_POINT, wayPoints);
    }

    /**
     * 生成开始节点的id
     * @return
     */
    public static String genStartEventId() {
        return START_EVENT_PREFIX + UUID.uuidKey();
    }

    /**
     * 生成结束节点的id
     * @return
     */
    public static String genEndEventId() {
        return END_EVENT_PREFIX + UUID.uuidKey();
    }

    /**
     * 生成Activity节点的id
     * @return
     */
    public static String genActivityId() {
        return ACTIVITY_PREFIX + UUID.uuidKey();
    }

    /**
     * 生成sequence flow的id
     * @return
     */
    public static String genSequenceFlowId() {
        return SEQUENCE_FLOW_PREFIX + UUID.uuidKey();
    }

    /**
     * 生成bpmn shape的id
     * @param bpmnElementId
     * @return
     */
    public static String genShapeId(String bpmnElementId) {
        return bpmnElementId + SHAPE_POSTFIX;
    }

    /**
     * 生成bpmn文件
     * @param bpmnProcesssItems
     * @param bpmnPlaneItems
     * @return
     */
    public static String genBpmnFile(String bpmnProcesssItems, String bpmnPlaneItems) {
        return BPMN_FILE_TPL.replace(BPMN_PROCESS_ITEMS, bpmnProcesssItems).replace(BPMN_PLANE_ITEMS, bpmnPlaneItems);
    }

    /**
     * 解析并生成业务流全部信息
     * @param flowNodes
     * @param businessFlowId
     * @param tenantId
     * @param systemId
     * @return
     * @throws EngineException
     */
    public static BusinessFlowInfo genBusinessFlowInfo(JsonArray flowNodes, String businessFlowId, String tenantId, String systemId) throws EngineException {
        if(JsonUtils.isEmpty(flowNodes)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "flowNodes is null!");
        }
        BusinessFlowInfo businessFlowInfo = new BusinessFlowInfo();
        FlowNodeParser parseResult = new FlowNodeParser();
        parseFlowNodes(flowNodes, FlowNodeParser.START_X, FlowNodeParser.START_Y, parseResult, businessFlowInfo, businessFlowId, tenantId, systemId);
        businessFlowInfo.setBpmnXml(genBpmnFile(parseResult.getFlowNodesStr(), parseResult.getBpmnShapesStr()));
        System.out.println(businessFlowInfo.getBpmnXml());
        return businessFlowInfo;
    }
    
    /**
     * 解析并生成业务流全部信息
     * @param flowNodes
     * @param startX
     * @param startY
     * @param parseResult
     * @param businessFlowInfo
     * @param businessFlowId
     * @param tenantId
     * @param systemId
     * @throws EngineException
     */
    private static void parseFlowNodes(JsonArray flowNodes, Integer startX, Integer startY, FlowNodeParser parseResult, BusinessFlowInfo businessFlowInfo, String businessFlowId, String tenantId, String systemId) throws EngineException {
        //生成的节点字符串
        String flowNodesStr = "";
        //生成的图形字符串
        String bpmnShapesStr = "";
        //图形的起始度坐标
        if(startX == null || startY == null) {
            startX = FlowNodeParser.START_X;
            startY = FlowNodeParser.START_Y;
        }
        
        //计算子流程的级数，用于生成子流程图形的大小和布局
        calSubProcessLevel(flowNodes, parseResult);
        
        //生成开始节点及连线
        String startNodeId = genStartEventId();//开始节点id
        String outFlowId = genSequenceFlowId();//连线id
        String startNode = genStartEvent(startNodeId, genOutgoing(outFlowId));//生成开始节点
        flowNodesStr += startNode;
        //生成对应的形状
        String startShape = genBpmnShape(genShapeId(startNodeId), startNodeId, startX, startY, FlowNodeParser.EVENT_NODE_WIDTH, FlowNodeParser.EVENT_NODE_HEIGHT, StringUtils.emptyString());
        bpmnShapesStr += startShape;

        int count = flowNodes.size();
        JsonObject flowNode = null;
        String prevActivityId = startNodeId;
        String prevFlowId = outFlowId;
        int prevX = startX;
        int prevY = startY;
        int prevFlowX = prevX + FlowNodeParser.EVENT_NODE_WIDTH;
        int prevFlowY = prevY + FlowNodeParser.EVENT_NODE_HEIGHT/2;
        //当主流程超过2个子流程时，用于错开处于同一部位（上部/下部）子流程的图形
        int upperShapeY = startY - (FlowNodeParser.ACTIVITY_NODE_HEIGHT - FlowNodeParser.EVENT_NODE_HEIGHT)/2;
        int lowerShapeY = startY + (FlowNodeParser.ACTIVITY_NODE_HEIGHT + FlowNodeParser.EVENT_NODE_HEIGHT)/2;

        List<BusinessFlowNode> businessFlowNodes = new ArrayList<>();
        Map<String, BusinessFlowInfo> subBusinessFlowMap = new HashMap<>();
        for(int i=0; i<count; i++) {
            //获取节点配置信息
            flowNode = flowNodes.get(i).getAsJsonObject();
            String nodeTypeId = FlowNodeParser.getNodeTypeId(flowNode);//节点类型id
            String flowDesc = FlowNodeParser.getNodeDesc(flowNode);//节点描述
            JsonObject nodeCfg = FlowNodeParser.getNodeCfg(flowNode);//节点配置信息
            if(StringUtils.isBlank(nodeTypeId) || JsonUtils.isEmpty(nodeCfg)) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "nodeTypeId or nodeCfg is null!");
            }
            JsonObject paramCfg = FlowNodeParser.getParamCfg(nodeCfg);
            if(JsonUtils.isEmpty(paramCfg)) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "paramCfg is null!");
            }
            //生成节点及连线
            String activityId = genActivityId();//生成当前节点id
            String incomingFlow = genSequenceFlow(prevFlowId, prevActivityId, activityId);//生成incoming连线
            flowNodesStr += incomingFlow;
            outFlowId = genSequenceFlowId();//生成outcoming连线id
            String incomingItems = genIncoming(prevFlowId);
            String outgoingItems = genOutgoing(outFlowId);
            //计算task节点的x、y坐标
            int x = prevFlowX + FlowNodeParser.SHAPE_INTERVAL;
            int y = prevY - (FlowNodeParser.ACTIVITY_NODE_HEIGHT - FlowNodeParser.EVENT_NODE_HEIGHT)/2;           

            //生成业务流节点对象
            businessFlowNodes.add(genBusinessFlowNode(businessFlowId, nodeTypeId, activityId, flowDesc, nodeCfg, i, tenantId, systemId));
            
            //如果节点类型为6条件判断分支节点 or 8循环节点 or ((18Excel文件导入 or 28JSON文件导入 or 30Txt文件导入）and 处理模式processMode为F流式处理)，存在子流程
            if(FlowNodeParser.hasSubProcessNode(nodeTypeId, paramCfg)) {
                JsonArray subProcessNodes = FlowNodeParser.getSubProcessNodes(paramCfg);
                if(JsonUtils.isEmpty(subProcessNodes)) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "subProcessNodes is null!");
                }
                //生成指向子流程节点的outcoming连线id
                String subOutFlowId = genSequenceFlowId();
                outgoingItems += genOutgoing(subOutFlowId);
                //生成指向子流程节点的连线
                String subProcessId = genActivityId();
                String subIncomingFlow = genSequenceFlow(subOutFlowId, activityId, subProcessId);
                flowNodesStr += subIncomingFlow;
                //计算子流程相关的坐标及宽高
                FlowNodeParser subParseResult = parseResult.getSubParseResult(String.valueOf(i));
                int subProcessNodeHeight = FlowNodeParser.calSubProcessShapeWidth(subParseResult.getSubProcessLevelCount());//子流程形状的高度
                int subProcessNodeWidth = subParseResult.getProcessShapeWidth();//子流程形状的宽度
                int subProcessShapeX = x;//子流程图形的X坐标，x坐标与主流程节点相同
                int subProcessShapeY = 0;//子流程图形的Y坐标
                int subProcessStartX = x + FlowNodeParser.SHAPE_INTERVAL;//子流程开始节点的x坐标
                int subProcessStartY = 0;//子流程开始节点的Y坐标
                String wayPoints = null;
                if(i % 2 == 0) {//奇数位的子流程放在上部
                    subProcessShapeY = upperShapeY - FlowNodeParser.SHAPE_INTERVAL - subProcessNodeHeight;
                    upperShapeY = subProcessShapeY;
                    subProcessStartY = subProcessShapeY + subProcessNodeHeight - (FlowNodeParser.ACTIVITY_NODE_HEIGHT + 3 * FlowNodeParser.SHAPE_INTERVAL) * subParseResult.getLowerSubProcessLevelCount() - FlowNodeParser.SHAPE_INTERVAL - FlowNodeParser.ACTIVITY_NODE_HEIGHT/2 - FlowNodeParser.EVENT_NODE_HEIGHT/2;
                    //subProcessStartY = subProcessShapeY + subProcessNodeHeight - FlowNodeParser.SHAPE_INTERVAL - FlowNodeParser.ACTIVITY_NODE_HEIGHT/2 - FlowNodeParser.EVENT_NODE_HEIGHT/2;
                    wayPoints = genWayPoint(x + FlowNodeParser.ACTIVITY_NODE_WIDTH/2, y) + genWayPoint(subProcessShapeX + FlowNodeParser.ACTIVITY_NODE_WIDTH/2, subProcessShapeY + subProcessNodeHeight);
                }else {//偶数位的子流程放在下部
                    subProcessShapeY = lowerShapeY + FlowNodeParser.SHAPE_INTERVAL;
                    lowerShapeY = subProcessShapeY + subProcessNodeHeight;
                    subProcessStartY = subProcessShapeY + (FlowNodeParser.ACTIVITY_NODE_HEIGHT + 3 * FlowNodeParser.SHAPE_INTERVAL) * subParseResult.getUpperSubProcessLevelCount() + FlowNodeParser.SHAPE_INTERVAL + FlowNodeParser.ACTIVITY_NODE_HEIGHT/2 - FlowNodeParser.EVENT_NODE_HEIGHT/2;
                    //subProcessStartY = subProcessShapeY + FlowNodeParser.SHAPE_INTERVAL + FlowNodeParser.ACTIVITY_NODE_HEIGHT/2 - FlowNodeParser.EVENT_NODE_HEIGHT/2;
                    wayPoints = genWayPoint(x + FlowNodeParser.ACTIVITY_NODE_WIDTH/2, y + FlowNodeParser.ACTIVITY_NODE_HEIGHT) + genWayPoint(subProcessShapeX + FlowNodeParser.ACTIVITY_NODE_WIDTH/2, subProcessShapeY);
                }
                //递归调用处理子流程的节点
                BusinessFlowInfo subBusinessFlowInfo = new BusinessFlowInfo();
                subBusinessFlowMap.put(activityId, subBusinessFlowInfo);
                parseFlowNodes(subProcessNodes, subProcessStartX, subProcessStartY, subParseResult, subBusinessFlowInfo, businessFlowId, tenantId, systemId);
                //生成子流程节点
                String subProcessNode = genSubProcess(subProcessId, incomingItems, subParseResult.getFlowNodesStr());
                flowNodesStr += subProcessNode;
                //生成对应的形状
                String subProcessShape = genSubProcessShape(genShapeId(subProcessId), subProcessId, subProcessShapeX, subProcessShapeY, subProcessNodeWidth, subProcessNodeHeight, StringUtils.emptyString());
                bpmnShapesStr += subProcessShape;
                bpmnShapesStr += subParseResult.getBpmnShapesStr();
                String bpmnEdge = genBpmnEdge(genShapeId(subOutFlowId), subOutFlowId, wayPoints);
                bpmnShapesStr += bpmnEdge;
            }
            String activityNode = genTask(activityId, flowDesc, outgoingItems, incomingItems);
            flowNodesStr += activityNode;

            //生成对应的形状
            String activityShape = genBpmnShape(genShapeId(activityId), activityId, x, y, FlowNodeParser.ACTIVITY_NODE_WIDTH, FlowNodeParser.ACTIVITY_NODE_HEIGHT, genBpmnLabel());
            bpmnShapesStr += activityShape;
            String wayPoints = genWayPoint(prevFlowX, prevFlowY) + genWayPoint(x, y + FlowNodeParser.ACTIVITY_NODE_HEIGHT/2);
            String bpmnEdge = genBpmnEdge(genShapeId(prevFlowId), prevFlowId, wayPoints);
            bpmnShapesStr += bpmnEdge;

            prevActivityId = activityId;
            prevFlowId = outFlowId;
            prevX = x;//prevY保持不变
            prevFlowX = x + FlowNodeParser.ACTIVITY_NODE_WIDTH;
            prevFlowY = y + FlowNodeParser.ACTIVITY_NODE_HEIGHT/2;
        }

        //生成结束节点及连线
        String endNodeId = genEndEventId();//结束节点id
        String incomingFlowId = prevFlowId;//连线id
        String endNode = genEndEvent(endNodeId, genIncoming(incomingFlowId));//生成结束节点
        flowNodesStr += endNode;
        String incomingFlow = genSequenceFlow(incomingFlowId, prevActivityId, endNodeId);//生成incoming连线
        flowNodesStr += incomingFlow;
        //生成对应的形状
        int endX = prevFlowX + FlowNodeParser.SHAPE_INTERVAL;
        int endY = prevY;
        String endShape = genBpmnShape(genShapeId(endNodeId), endNodeId, endX, endY, FlowNodeParser.EVENT_NODE_WIDTH, FlowNodeParser.EVENT_NODE_HEIGHT, StringUtils.emptyString());
        bpmnShapesStr += endShape;
        String wayPoints = genWayPoint(prevFlowX, prevFlowY) + genWayPoint(endX, endY + FlowNodeParser.EVENT_NODE_HEIGHT/2);
        String bpmnEdge = genBpmnEdge(genShapeId(incomingFlowId), incomingFlowId, wayPoints);
        bpmnShapesStr += bpmnEdge;

        parseResult.setFlowNodesStr(flowNodesStr);
        parseResult.setBpmnShapesStr(bpmnShapesStr);

        businessFlowInfo.setBusinessFlowNodes(businessFlowNodes);
        businessFlowInfo.setSubBusinessFlowMap(subBusinessFlowMap);
    }

    /**
     * 生成业务流节点对象
     * @param businessFlowId
     * @param nodeTypeId
     * @param bpmnNodeId
     * @param nodeDesc
     * @param nodeCfg
     * @param nodeOrder
     * @param tenantId
     * @param systemId
     * @return
     */
    private static BusinessFlowNode genBusinessFlowNode(String businessFlowId, String nodeTypeId, String bpmnNodeId, String nodeDesc, JsonObject nodeCfg, int nodeOrder, String tenantId, String systemId) {
        BusinessFlowNode businessFlowNode = new BusinessFlowNode();
    	businessFlowNode.setBusinessFlowId(businessFlowId);
    	businessFlowNode.setFlowNodeId(nodeTypeId);
    	businessFlowNode.setName(nodeDesc);
    	businessFlowNode.setNodeOrder(nodeOrder);
    	businessFlowNode.setTenantId(tenantId);
    	businessFlowNode.setSystemId(systemId);
    	businessFlowNode.setBpmnNodeId(bpmnNodeId);
    	// Date nowDate = new Date();
    	// businessFlowNode.setCreateTime(nowDate);
    	// businessFlowNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	businessFlowNode.setNodePreCfg(JsonUtils.toJson(FlowNodeParser.getPreCfg(nodeCfg)));
    	
    	//set node process cfg
    	JsonObject paramCfg = FlowNodeParser.getParamCfg(nodeCfg);
        JsonObject resultCfg = FlowNodeParser.getResultCfg(nodeCfg);
    	paramCfg.addProperty(Node.IS_LIST_RESULT_KEY, JsonUtils.getBoolean(resultCfg, Node.IS_LIST_RESULT_KEY));
    	paramCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, JsonUtils.getString(resultCfg, Node.NODE_RESULT_TYPE_KEY));
    	paramCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, JsonUtils.getString(resultCfg, Node.NODE_RESULT_CLASS_KEY));   	
    	businessFlowNode.setNodeCfg(JsonUtils.toJson(paramCfg));
    	
    	//set node postProcess cfg
    	businessFlowNode.setNodePostCfg(JsonUtils.toJson(FlowNodeParser.getPostCfg(nodeCfg)));

        return businessFlowNode;
    }

    /**
     * 计算子流程图形需要占用的行数
     * @param flowNodes
     * @param parseResult
     * @throws EngineException
     */
    private static void calSubProcessLevel(JsonArray flowNodes, FlowNodeParser parseResult) throws EngineException{
        JsonObject flowNode = null;
        int count = flowNodes.size();
        int processShapeWidth = FlowNodeParser.calMainFlowWidth(count);//主流程节点需要占用的宽度
        int upperLevelCount = 0, lowerLevelCount = 0; //本层级包含的子流程级数(分上部、下部)
        for(int i=0; i<count; i++) {
            //获取节点配置信息
            flowNode = flowNodes.get(i).getAsJsonObject();
            String nodeTypeId = FlowNodeParser.getNodeTypeId(flowNode);//节点类型id
            JsonObject nodeCfg = FlowNodeParser.getNodeCfg(flowNode);//节点配置信息
            if(StringUtils.isBlank(nodeTypeId) || JsonUtils.isEmpty(nodeCfg)) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "nodeTypeId or nodeCfg is null!");
            }
            JsonObject paramCfg = FlowNodeParser.getParamCfg(nodeCfg);
            if(JsonUtils.isEmpty(paramCfg)) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "paramCfg is null!");
            }

            //如果节点类型为6条件判断分支节点 or 8循环节点 or ((18Excel文件导入 or 28JSON文件导入 or 30Txt文件导入）and 处理模式processMode为F流式处理)，存在子流程
            if(FlowNodeParser.hasSubProcessNode(nodeTypeId, paramCfg)) {
                JsonArray subProcessNodes = FlowNodeParser.getSubProcessNodes(paramCfg);
                if(JsonUtils.isEmpty(subProcessNodes)) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "node type " + nodeTypeId + " subProcessNodes is null!");
                }
                FlowNodeParser subParseResult = new FlowNodeParser();
                calSubProcessLevel(subProcessNodes, subParseResult);//递归解析下级子流程
                parseResult.addSubParseResult(String.valueOf(i), subParseResult);//将下级子流程的解析结果加入到当前解析结果中
                if(i % 2 == 0) {//奇数位的子流程放在上部
                    upperLevelCount += subParseResult.getSubProcessLevelCount();
                }else {//偶数位的子流程放在下部
                    lowerLevelCount += subParseResult.getSubProcessLevelCount();
                }
                //比较子流程与主流程哪个需要的宽度大
                int widthWithSub =  FlowNodeParser.calMainFlowWidthWithSub(i, subParseResult.getProcessShapeWidth());
                if(processShapeWidth < widthWithSub) {//哪个宽，使用哪个
                    processShapeWidth = widthWithSub;
                }
            }
        }
        parseResult.setUpperSubProcessLevelCount(upperLevelCount);
        parseResult.setLowerSubProcessLevelCount(lowerLevelCount);
        parseResult.setSubProcessLevelCount(upperLevelCount + lowerLevelCount + 1);
        parseResult.setProcessShapeWidth(processShapeWidth);
    }
}
