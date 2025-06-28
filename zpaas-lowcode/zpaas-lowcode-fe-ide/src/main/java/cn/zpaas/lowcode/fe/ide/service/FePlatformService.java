package cn.zpaas.lowcode.fe.ide.service;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.ComboFuncDefine;
import cn.zpaas.lowcode.domain.eo.ComboFuncTab;
import cn.zpaas.lowcode.domain.eo.ComboFuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayout;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedPage;
import cn.zpaas.lowcode.domain.eo.FuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.domain.eo.FuncRegion;
import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionOperation;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.ide.vo.BusinessSystemFeInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionChartDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionReportDesignInfo;
import cn.zpaas.lowcode.vo.FuncDefineGenerateRequest;


/**
 * @author zjy
 * 前端领域平台服务接口类
 */
public interface FePlatformService {

	/**
	 * 加载业务系统前端领域相关信息
	 * @param businessSystemId
	 * @return
	 */
	public BusinessSystemFeInfo loadBusinessSystemFeInfo(String businessSystemId);

	/**
	 * 生成功能定义信息
	 * @param request
	 * @return
	 */
	public FuncDefine generateFuncDefineAll(FuncDefineGenerateRequest request) throws EngineException;
	
	/**
	 * 删除功能定义信息
	 * @param funcId
	 * @return
	 */
	public int deleteFuncDefine(String funcId);
	
	/**
	 * 加载功能定义信息（全量）
	 * @param funcId
	 * @return
	 */
	public FuncDefine loadFuncDefineAll(String funcId) throws EngineException;
	
	/**
	 * 保存功能定义信息
	 * @param funcDefine
	 * @return
	 */
	public FuncDefine saveFuncDefine(FuncDefine funcDefine);

	/**
	 * 查询功能定义信息
	 * @param funcDefine
	 * @return
	 */
	public FuncDefine queryFuncDefine(String funcId);
	
	/**
	 * 查询功能定义列表信息
	 * @param systemId
	 * @return
	 */
	public List<FuncDefine> listFuncDefines(String systemId);

	/**
	 * 查询模板功能列表
	 * @param systemId
	 * @return
	 */
	public List<FuncDefine> listTplFuncDefines(String systemId);

	/**
	 * 查询自定义功能列表
	 * @param systemId
	 * @return
	 */
	public List<FuncDefine> listFuncCustomizedDefines(String systemId);
	
	/**
	 * 根据主键加载对象绑定信息（全量）
	 * @param objectAssignId
	 * @return
	 */
	public FuncRegion loadFuncRegionAll(String funcRegionId);
	
	/**
	 * 加载功能区域列表信息
	 * @param systemId
	 * @param funcId
	 * @return
	 */
	public List<FuncRegion> listFuncRegions(String systemId, String funcId);
	
	/**
	 * 保存功能区域信息
	 * @param funcRegion
	 * @return
	 */
	public FuncRegion saveFuncRegion(FuncRegion funcRegion);

	/**
	 * 保存功能区域配置信息
	 * @param funcRegion
	 * @return
	 */
	public FuncRegion saveFuncRegionCfg(FuncRegion funcRegion);

	/**
	 * 保存功能区域设计信息
	 * @param funcRegionDesignInfo
	 * @return
	 */
	public int saveFuncRegionDesignInfo(FuncRegionDesignInfo funcRegionDesignInfo) throws EngineException;

	/**
	 * 保存功能区域统计报表设计信息
	 * @param funcRegionDesignInfo
	 * @return
	 */
	public int saveFuncRegionReportDesignInfo(FuncRegionReportDesignInfo funcRegionDesignInfo) throws EngineException;
	
	/**
	 * 保存功能区域统计图表设计信息
	 * @param funcRegionDesignInfo
	 * @return
	 */
	public int saveFuncRegionChartDesignInfo(FuncRegionChartDesignInfo funcRegionDesignInfo) throws EngineException;
	
	/**
	 * 根据主键加载对象绑定信息（全量）
	 * @param objectAssignId
	 * @return
	 */
	public FuncObjectAssign loadFuncObjectAssignAll(String objectAssignId) throws EngineException;

	/**
	 * 根据功能标识加载对象绑定信息列表
	 * @param objectAssignId
	 * @return
	 */
	public List<FuncObjectAssign> listFuncObjectAssign(String funcId) throws EngineException ;
	
	/**
	 * 删除对象绑定信息
	 * @param objectAssignId
	 * @return
	 */
	public int deleteFuncObjectAssign(String objectAssignId);

	/**
	 * 查询对象绑定信息
	 * @param objectAssignId
	 * @return
	 */
	public FuncObjectAssign queryFuncObjectAssign(String objectAssignId);
	
	/**
	 * 保存对象绑定信息
	 * @param objectAssign
	 * @return
	 */
	public FuncObjectAssign saveFuncObjectAssign(FuncObjectAssign objectAssign) throws EngineException;
	
	/**
	 * 新建对象绑定信息
	 * @param objectAssign
	 * @return
	 */
	public FuncObjectAssign addFuncObjectAssign(FuncObjectAssign objectAssign);
	
	/**
	 * 查询属性可选值信息
	 * @param objectAssignId
	 * @param attributeId
	 * @return
	 */
	public FuncObjectAttrOptions loadAttrOptions(String objectAssignId, String attributeId);
	
	/**
	 * 保存属性可选值信息
	 * @param objectAssignOptions
	 * @return
	 */
	public FuncObjectAttrOptions saveAttrOptions(FuncObjectAttrOptions objectAssignOptions);
	
	/**
	 * 删除属性可选值信息
	 * @param objectAssignId
	 * @param attributeId
	 * @return
	 */
	public int deleteAttrOptions(String objectAssignId, String attributeId);
	
	/**
	 * 从其他功能复制绑定对象属性选项信息
	 * @param fromFuncObjectAssignId
	 * @param targetFuncObjectAssignId
	 * @return
	 * @throws EngineException
	 */
	public List<FuncObjectAttrOptions> copyFuncObjectAttrOptionsFromOther(String fromFuncObjectAssignId, String targetFuncObjectAssignId);

	/**
	 * 根据主键查询功能区域绑定属性信息
	 * @param attrAssignId
	 * @return
	 */
	public FuncRegionAttrAssign loadFuncRegionAttrAssign(String attrAssignId);
	
	/**
	 * 保存功能区域绑定属性信息
	 * @param funcRegionAttrAssign
	 * @return
	 */
	public FuncRegionAttrAssign saveFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign);
	
	/**
	 * 新增功能区域绑定属性信息
	 * @param funcRegionAttrAssign
	 * @return
	 */
	public FuncRegionAttrAssign addFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign);
	
	/**
	 * 复制功能区域绑定属性信息
	 * @param funcId
	 * @param fromRegionId
	 * @param targetRegionId
	 * @return
	 */
	public List<FuncRegionAttrAssign> copyFuncRegionAttrAssign(String funcId, String fromRegionId, String targetRegionId) throws EngineException;
	
	/**
	 * 复制功能区域绑定属性信息
	 * @param funcId
	 * @param fromRegionId
	 * @param targetFuncId
	 * @param targetRegionId
	 * @return
	 * @throws EngineException
	 */
	public List<FuncRegionAttrAssign> copyFuncRegionAttrAssignFromOther(String fromFuncId, String fromRegionId, String targetFuncId, String targetRegionId) throws EngineException;
	
	/**
	 * 根据主键删除功能区域绑定属性信息
	 * @param attrAssignId
	 * @return
	 */
	public int deleteFuncRegionAttrAssign(String attrAssignId);
	
	/**
	 * 根据主键查询功能区域绑定操作信息
	 * @param regionOperationId
	 * @return
	 */
	public FuncRegionOperation loadFuncRegionOperation(String regionOperationId);
	
	/**
	 * 保存功能区域绑定操作信息
	 * @param funcRegionOperation
	 * @return
	 */
	public FuncRegionOperation saveFuncRegionOperation(FuncRegionOperation funcRegionOperation);
	
	/**
	 * 新增功能区域绑定操作信息
	 * @param funcRegionOperation
	 * @return
	 */
	public FuncRegionOperation addFuncRegionOperation(FuncRegionOperation funcRegionOperation);
	
	/**
	 * 根据主键删除功能区域绑定操作信息
	 * @param regionOperationId
	 * @return
	 */
	public int deleteFuncRegionOperation(String regionOperationId);
	
	/**
	 * 根据模板区域标识加载模板操作列表
	 * @param tplRegionId
	 * @return
	 */
	public List<FuncTemplateRegionOperation> loadFuncTemplateRegionOperations(String tplRegionId);
	
	/**
	 * 加载所有功能模板以及模板的附属信息
	 * @return
	 */
	public List<FuncTemplate> queryFuncTemplateAll();
	
	
	
	/**
	 * 加载组合功能模板列表信息
	 * @return
	 */
	public List<ComboFuncTemplate> listComboFuncTemplates();
	
	/**
	 * 保存组合功能定义信息
	 * @param comboFuncDefine
	 * @return
	 */
	public ComboFuncDefine saveComboFuncDefine(ComboFuncDefine comboFuncDefine);
	
	/**
	 * 新增组合功能定义信息
	 * @param comboFuncDefine
	 * @return
	 */
	public ComboFuncDefine addComboFuncDefine(ComboFuncDefine comboFuncDefine);
	
	/**
	 * 加载组合功能定义的所有信息
	 * @param comboFuncId
	 * @return
	 */
	public ComboFuncDefine loadComboFuncDefineAll(String comboFuncId);

	/**
	 * 加载组合功能定义信息
	 * @param comboFuncId
	 * @return
	 */
	public ComboFuncDefine loadComboFuncDefine(String comboFuncId);

	/**
	 * 删除组合功能定义信息
	 * @param comboFuncId
	 * @return
	 */
	public int deleteComboFuncDefine(String comboFuncId);
	
	/**
	 * 加载组合功能标签页全部信息
	 * @param comboFuncTabId
	 * @return
	 */
	public ComboFuncTab loadComboFuncTabAll(String comboFuncTabId);
	
	/**
	 * 保存组合功能标签页信息
	 * @param comboFuncTab
	 * @return
	 */
	public ComboFuncTab saveComboFuncTab(ComboFuncTab comboFuncTab);
	
	/**
	 * 新增组合功能标签页信息
	 * @param comboFuncTab
	 * @return
	 */
	public ComboFuncTab addComboFuncTab(ComboFuncTab comboFuncTab);
	
	/**
	 * 删除组合功能标签页信息
	 * @param systemId
	 * @param id
	 * @return
	 */
	public int deleteComboFuncTab(String systemId, String id);

	/**
	 * 保存自定义功能的布局信息，包括所有的子信息
	 * @param customizedLayouts
	 * @return
	 */
	public int saveCustomizePageLayoutsAll(String pageId, List<FuncCustomizedLayout> customizedLayouts) throws EngineException;

	/**
	 * 加载自定义页面全量信息
	 * @param pageId
	 * @return
	 */
	public FuncCustomizedPage loadFuncCustomizedPageAll(String pageId);

	/**
	 * 加载自定义页面信息
	 * @param pageId
	 * @return
	 */
	public FuncCustomizedPage loadFuncCustomizedPage(String pageId);


	/**
	 * 保存自定义页面信息
	 * @param funcCustomizedPage
	 * @return
	 */
	public FuncCustomizedPage saveFuncCustomizedPage(FuncCustomizedPage funcCustomizedPage);

	/**
	 * 创建自定义页面信息
	 * @param funcCustomizedPage
	 * @return
	 */
	public FuncCustomizedPage createFuncCustomizedPage(FuncCustomizedPage funcCustomizedPage);

	/**
	 * 加载自定义页面列表信息
	 * @param systemId
	 * @param funcId
	 * @return
	 */
	public List<FuncCustomizedPage> listFuncCustomizedPages(String systemId, String funcId);

	/**
	 * 复制自定义页面设计信息
	 * @param fromFuncId
	 * @param fromPageId
	 * @param targetFuncId
	 * @param targetPageId
	 * @return
	 * @throws EngineException
	 */
	public boolean copyFuncCustomizedPageFromOther(String fromFuncId, String fromPageId, String targetFuncId, String targetPageId) throws EngineException;
}
