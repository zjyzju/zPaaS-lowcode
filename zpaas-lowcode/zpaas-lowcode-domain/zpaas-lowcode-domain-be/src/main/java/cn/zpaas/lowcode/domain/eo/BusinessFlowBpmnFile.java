package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.BusinessFlowBpmnFileMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class BusinessFlowBpmnFile {
    private String id;

    private String businessFlowId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private String bpmnXml;
    
    public static final String EXAMPLE_BPMN_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" id=\"Definitions_1\" targetNamespace=\"http://bpmn.io/schema/bpmn\">   <bpmn:process id=\"Process_1\" isExecutable=\"false\">     <bpmn:startEvent id=\"StartEvent_1\">       <bpmn:outgoing>Flow_096tk6b</bpmn:outgoing>     </bpmn:startEvent>     <bpmn:task id=\"Activity_147r80f\" name=\"ORM数据库存取节点(1)\">       <bpmn:incoming>Flow_096tk6b</bpmn:incoming>       <bpmn:outgoing>Flow_1473x36</bpmn:outgoing>     </bpmn:task>     <bpmn:sequenceFlow id=\"Flow_096tk6b\" sourceRef=\"StartEvent_1\" targetRef=\"Activity_147r80f\" />     <bpmn:endEvent id=\"Event_1ews9d9\">       <bpmn:incoming>Flow_1473x36</bpmn:incoming>     </bpmn:endEvent>     <bpmn:sequenceFlow id=\"Flow_1473x36\" sourceRef=\"Activity_147r80f\" targetRef=\"Event_1ews9d9\" />   </bpmn:process>   <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">     <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1\">       <bpmndi:BPMNEdge id=\"Flow_096tk6b_di\" bpmnElement=\"Flow_096tk6b\">         <di:waypoint x=\"209\" y=\"120\" />         <di:waypoint x=\"260\" y=\"120\" />       </bpmndi:BPMNEdge>       <bpmndi:BPMNEdge id=\"Flow_1473x36_di\" bpmnElement=\"Flow_1473x36\">         <di:waypoint x=\"360\" y=\"120\" />         <di:waypoint x=\"412\" y=\"120\" />       </bpmndi:BPMNEdge>       <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">         <dc:Bounds x=\"173\" y=\"102\" width=\"36\" height=\"36\" />       </bpmndi:BPMNShape>       <bpmndi:BPMNShape id=\"Activity_147r80f_di\" bpmnElement=\"Activity_147r80f\">         <dc:Bounds x=\"260\" y=\"80\" width=\"100\" height=\"80\" />         <bpmndi:BPMNLabel />       </bpmndi:BPMNShape>       <bpmndi:BPMNShape id=\"Event_1ews9d9_di\" bpmnElement=\"Event_1ews9d9\">         <dc:Bounds x=\"412\" y=\"102\" width=\"36\" height=\"36\" />       </bpmndi:BPMNShape>     </bpmndi:BPMNPlane>   </bpmndi:BPMNDiagram> </bpmn:definitions>";
    
    public static final String EXAMPLE_BPMN_NODE_ID = "Activity_147r80f";
    
    public static final String LOCAL_INVOKE_BPMN_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" id=\"Definitions_1\" targetNamespace=\"http://bpmn.io/schema/bpmn\"><bpmn:process id=\"Process_1\" isExecutable=\"false\"><bpmn:startEvent id=\"StartEvent_1\"><bpmn:outgoing>Flow_0ty9tp1</bpmn:outgoing></bpmn:startEvent><bpmn:task id=\"Activity_08td85o\" name=\"本地方法调用(3)\"><bpmn:incoming>Flow_0ty9tp1</bpmn:incoming><bpmn:outgoing>Flow_0e82qbl</bpmn:outgoing></bpmn:task><bpmn:sequenceFlow id=\"Flow_0ty9tp1\" sourceRef=\"StartEvent_1\" targetRef=\"Activity_08td85o\" /><bpmn:endEvent id=\"Event_1muqi88\"><bpmn:incoming>Flow_0e82qbl</bpmn:incoming></bpmn:endEvent><bpmn:sequenceFlow id=\"Flow_0e82qbl\" sourceRef=\"Activity_08td85o\" targetRef=\"Event_1muqi88\" /></bpmn:process><bpmndi:BPMNDiagram id=\"BPMNDiagram_1\"><bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1\"><bpmndi:BPMNEdge id=\"Flow_0e82qbl_di\" bpmnElement=\"Flow_0e82qbl\"><di:waypoint x=\"360\" y=\"120\" /><di:waypoint x=\"412\" y=\"120\" /></bpmndi:BPMNEdge><bpmndi:BPMNEdge id=\"Flow_0ty9tp1_di\" bpmnElement=\"Flow_0ty9tp1\"><di:waypoint x=\"209\" y=\"120\" /><di:waypoint x=\"260\" y=\"120\" /></bpmndi:BPMNEdge><bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\"><dc:Bounds x=\"173\" y=\"102\" width=\"36\" height=\"36\" /></bpmndi:BPMNShape><bpmndi:BPMNShape id=\"Activity_08td85o_di\" bpmnElement=\"Activity_08td85o\"><dc:Bounds x=\"260\" y=\"80\" width=\"100\" height=\"80\" /><bpmndi:BPMNLabel /></bpmndi:BPMNShape><bpmndi:BPMNShape id=\"Event_1muqi88_di\" bpmnElement=\"Event_1muqi88\"><dc:Bounds x=\"412\" y=\"102\" width=\"36\" height=\"36\" /></bpmndi:BPMNShape></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></bpmn:definitions>";
    
    public static final String LOCAL_INVOKE_BPMN_NODE_ID = "Activity_08td85o";
    
    @Autowired
    private BusinessFlowBpmnFileMapper mapper;
    
    public BusinessFlowBpmnFile byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public int save(BusinessFlowBpmnFile businessFlowBpmnFile) {
    	if(businessFlowBpmnFile.getId() == null || businessFlowBpmnFile.getId().trim().length() == 0) {
    		businessFlowBpmnFile.setId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(businessFlowBpmnFile);
    }
    
    public int updateById(BusinessFlowBpmnFile businessFlowBpmnFile) {
    	if(businessFlowBpmnFile.getId() == null || businessFlowBpmnFile.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKeyWithBLOBs(businessFlowBpmnFile);
    }
    
    public int saveOrUpdate(BusinessFlowBpmnFile businessFlowBpmnFile) {
    	Date nowDate = new Date();
    	businessFlowBpmnFile.setUpdateTime(nowDate);
    	int result = this.updateById(businessFlowBpmnFile);
    	if(result <=0) {
    		businessFlowBpmnFile.setCreateTime(nowDate);
    		result = this.save(businessFlowBpmnFile);
    	}
    	return result;
    }
    
    public int deleteByFlowId(String businessFlowId) {
    	BusinessFlowBpmnFileExample criteria = new BusinessFlowBpmnFileExample();
    	criteria.createCriteria().andBusinessFlowIdEqualTo(businessFlowId);
    	return mapper.deleteByExample(criteria);
    }
    
    public BusinessFlowBpmnFile queryByFlowId(String businessFlowId) {
    	BusinessFlowBpmnFileExample criteria = new BusinessFlowBpmnFileExample();
    	criteria.createCriteria().andBusinessFlowIdEqualTo(businessFlowId);
    	List<BusinessFlowBpmnFile> list = mapper.selectByExampleWithBLOBs(criteria);
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBusinessFlowId() {
        return businessFlowId;
    }

    public void setBusinessFlowId(String businessFlowId) {
        this.businessFlowId = businessFlowId == null ? null : businessFlowId.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml == null ? null : bpmnXml.trim();
    }
}