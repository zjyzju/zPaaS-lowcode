package cn.zpaas.lowcode.be.engine.domain.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.be.engine.flow.BusinessFlowTemplate;
import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.constant.FlowType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowBpmnFile;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月13日-11:47:51
 */
@Component
public class BusinessFlowService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessFlowService.class);

    @Autowired
	private BusinessFlow businessFlowSV;
	
	@Autowired
	private BusinessFlowNode businessFlowNodeSV;
	
	@Autowired 
	private BusinessFlowBpmnFile businessFlowBpmnFileSV;

    @Autowired
	private SqlManagement sqlManagementSV;
	
	public BusinessFlowInfo loadBusinessFlowInfo(String businessFlowId) {
		//加载业务流信息
		BusinessFlow businessFlowBean = businessFlowSV.byId(businessFlowId);
		//如果未找到业务流信息，则直接返回null
		if(businessFlowBean == null) {
			return null;
		}	
		//初始化返回对象
		BusinessFlowInfo info = new BusinessFlowInfo();
		//设置业务流对象到返回对象中
		info.setBusinessFlow(businessFlowBean);
		//如果是主业务流的话，加载对应的bpmn2.0 xml格式流程文件
		if(StringUtils.isBlank(info.getBusinessFlow().getParentFlowId())) {
			BusinessFlowBpmnFile bpmnFile = this.businessFlowBpmnFileSV.queryByFlowId(info.getBusinessFlow().getId());
			if(bpmnFile != null) {
				info.setBpmnXml(bpmnFile.getBpmnXml());
			}
		}
		//加载对应的的业务流节点
		info.setBusinessFlowNodes(businessFlowNodeSV.listByBusinessFlowId(businessFlowId));
		//循环处理每个节点，设置节点类型对象，如果存在子业务流，则进行递归处理
		info.setSubBusinessFlowMap(new HashMap<>());
		if(!CollectionUtils.isEmpty(info.getBusinessFlowNodes())) {
			for(BusinessFlowNode node : info.getBusinessFlowNodes()) {
				if(StringUtils.isBlank(node.getFlowNodeId())) {
					node.setFlowNode(BusinessFlowTemplate.getNode(node.getFlowNodeId()).getFlowNode());
				}

				String nodeCfgString = node.getNodeCfg();
				if(!StringUtils.isBlank(nodeCfgString)) {
					JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
					if(nodeCfg != null) {
						BusinessFlowTemplate.getNode(node.getFlowNodeId()).nodeCfgLoad(nodeCfg, node, info, this);
					}
				}
			}
		}	
		return info;
	}

    /**
     * 加载sql配置信息
     * @param sqlId
     * @return
     */
    public SqlManagement loadSqlManagement(String sqlId) {
        return sqlManagementSV.byId(sqlId);
    }

    /**
     * 保存业务流信息
     * @param businessFlow
     * @return
     */
    public BusinessFlow saveBusinessFlow(BusinessFlow businessFlow) {
		Date now = new Date();
		if(businessFlow.getCreateTime() == null) {
			businessFlow.setCreateTime(now);
		}
		businessFlow.setUpdateTime(now);
		
		if(businessFlow.getId() == null || businessFlow.getId().trim().length() == 0) {
			this.businessFlowSV.save(businessFlow);
		}else {		
			if(this.businessFlowSV.updateById(businessFlow) <= 0) {
				this.businessFlowSV.save(businessFlow);
			}
		}
		return businessFlow;
	}

    /**
     * 保存业务流全量信息
     * @param businessFlowInfo
     * @return
     * @throws EngineException
     */
    public BusinessFlowInfo saveBusinessFlowAllInfo(BusinessFlowInfo businessFlowInfo) throws EngineException{
		//保存主业务流信息，当业务流标识为空时新增；不为空是更新
		this.saveBusinessFlow(businessFlowInfo.getBusinessFlow());

		//清理已经被删除的业务流节点和对应的bpmn文件记录
		//查询库里存在的业务流节点
		List<BusinessFlowNode> list = this.businessFlowNodeSV.listByBusinessFlowId(businessFlowInfo.getBusinessFlow().getId());
		if(list != null && list.size() > 0) {
			//循环前台传回来的列表，与库里的记录进行比对，剔除还存在的记录，剩下的list列表中为需要删除的记录
			if(!CollectionUtils.isEmpty(businessFlowInfo.getBusinessFlowNodes())) {
				int size = list.size();				
				for(int i=size-1; i>=0; i--) {
					BusinessFlowNode node = list.get(i);
					for(BusinessFlowNode row : businessFlowInfo.getBusinessFlowNodes()) {
						if(node.getId().equals(row.getId())) {
							list.remove(i);
							break;
						}
					}
				}
			}
			//删除剩下已经不存在的记录
			if(list.size() > 0) {
				for(BusinessFlowNode row : list) {
					//删除业务流节点
					this.businessFlowNodeSV.deleteById(row.getId());
					//删除业务流对应的bpmn文件
					this.businessFlowBpmnFileSV.deleteByFlowId(row.getId());
				}
			}
		}
		Date nowDate = new Date();
		// 保存业务流对应bpmn文件（xml）
		//根据业务流标识查询库里的bpmn文件记录
		if(StringUtils.isBlank(businessFlowInfo.getBusinessFlow().getParentFlowId())) {
			BusinessFlowBpmnFile bpmnFile = this.businessFlowBpmnFileSV
					.queryByFlowId(businessFlowInfo.getBusinessFlow().getId());
			//如果为空，则需要新增
			if (bpmnFile == null) {
				bpmnFile = new BusinessFlowBpmnFile();
			}
			bpmnFile.setBpmnXml(businessFlowInfo.getBpmnXml());
			
			if (!StringUtils.isBlank(bpmnFile.getId())) {//id不为空时执行更新操作
				bpmnFile.setUpdateTime(nowDate);
				this.businessFlowBpmnFileSV.updateById(bpmnFile);
			} else {//为空时，执行插入操作
				bpmnFile.setBusinessFlowId(businessFlowInfo.getBusinessFlow().getId());
				bpmnFile.setCreateTime(nowDate);
				bpmnFile.setSystemId(businessFlowInfo.getBusinessFlow().getSystemId());
				bpmnFile.setTenantId(businessFlowInfo.getBusinessFlow().getTenantId());
				this.businessFlowBpmnFileSV.save(bpmnFile);
			}
		}
				
		//循环保存业务流节点信息
		if(!CollectionUtils.isEmpty(businessFlowInfo.getBusinessFlowNodes())) {
			for(BusinessFlowNode businessFlowNode : businessFlowInfo.getBusinessFlowNodes()) {
				Node node = BusinessFlowTemplate.getNode(businessFlowNode.getFlowNodeId());
                node.beforeSave(businessFlowNode, businessFlowInfo, this);
				businessFlowNode.setBusinessFlowId(businessFlowInfo.getBusinessFlow().getId());
				this.businessFlowNodeSV.saveOrUpdate(businessFlowNode);
			}
		}		
		return businessFlowInfo;
	}

    /**
     * 保存子业务流信息
     * @param node
     * @param info
     * @param businessFlowService
     * @param subBusinessFlowIdKey
     * @throws EngineException
     */
    public void saveSubBusinessFlow(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService, String subBusinessFlowIdKey) throws EngineException {
		if(!MapUtils.isEmpty(info.getSubBusinessFlowMap())) {
			BusinessFlowInfo subBusinessFlowInfo = info.getSubBusinessFlowMap().get(node.getBpmnNodeId());
			//如果存在子业务流
			if(subBusinessFlowInfo != null) {
				//初始化子业务流对象
				if(subBusinessFlowInfo.getBusinessFlow() == null) {
					BusinessFlow subBusinessFlow = new BusinessFlow();
					subBusinessFlow.setName(node.getName() + "的子业务流");
					subBusinessFlow.setDbSchemaId(info.getBusinessFlow().getDbSchemaId());
					subBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
					subBusinessFlow.setParentFlowId(info.getBusinessFlow().getId());
					subBusinessFlow.setTransactionRequired(info.getBusinessFlow().getTransactionRequired());
					subBusinessFlow.setSystemId(info.getBusinessFlow().getSystemId());
					subBusinessFlow.setTenantId(info.getBusinessFlow().getTenantId());
					subBusinessFlowInfo.setBusinessFlow(subBusinessFlow);
				}
				//保存子业务流,-----递归调用
				businessFlowService.saveBusinessFlowAllInfo(subBusinessFlowInfo);
				//将子业务流的id，设置到父业务流的节点的节点配置信息中
				String nodeCfgString = node.getNodeCfg();
				JsonObject nodeCfg = null;
				if(nodeCfgString != null && nodeCfgString.trim().length() > 0) {
					nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
				}else {
					nodeCfg = new JsonObject();
				}
				nodeCfg.addProperty(subBusinessFlowIdKey, subBusinessFlowInfo.getBusinessFlow().getId());
				node.setNodeCfg(JsonUtils.toJson(nodeCfg));
			}
		}
	}

    /**
     * 新增/更新SQL信息
     * @param node
     * @param info
     * @param businessFlowService
     * @throws EngineException
     */
    public void nodeSaveSqlManagement(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService, String resourceType, String resourceId, String sqlIdKey, String sqlSentenceKey)
			throws EngineException {
		String nodeCfgString = node.getNodeCfg();
		if(!StringUtils.isBlank(nodeCfgString)) {
			JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
			String sqlSentence = JsonUtils.getString(nodeCfg, sqlSentenceKey);
			String sqlId = JsonUtils.getString(nodeCfg, sqlIdKey);
			if(!StringUtils.isBlank(sqlSentence)) {
				Date nowDate = new Date();
				if(StringUtils.isBlank(sqlId)) {//新增sql的情况
					SqlManagement sqlManagement = new SqlManagement();
					sqlManagement.setSqlSentence(sqlSentence);
					sqlManagement.setResourceType(resourceType);
					sqlManagement.setResourceId(resourceId);
					sqlManagement.setSystemId(node.getSystemId());
					sqlManagement.setTenantId(node.getTenantId());
					sqlManagement.setCreateTime(nowDate);
					sqlManagement.setUpdateTime(nowDate);
					sqlManagement.setDescription(info.getBusinessFlow().getName()+"创建的SQL");
					sqlManagement.setCode("created by BusinessFlowNode");
					if(this.sqlManagementSV.save(sqlManagement) <= 0) {
						logger.error("save sqlManagement failed!");
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "save sqlManagement failed!");
					}
					nodeCfg.addProperty(sqlIdKey, sqlManagement.getId());
					node.setNodeCfg(JsonUtils.toJson(nodeCfg));
				}else {//更新sql
					SqlManagement sqlManagement = this.sqlManagementSV.byId(sqlId);
					sqlManagement.setSqlSentence(sqlSentence);
					sqlManagement.setResourceType(resourceType);
					sqlManagement.setResourceId(resourceId);
					sqlManagement.setUpdateTime(nowDate);
					if(this.sqlManagementSV.update(sqlManagement) <= 0) {
						logger.error("update sqlManagement failed!");
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "update sqlManagement failed!");
					}
				}
			}
		}
	}
}
