package cn.zpaas.lowcode.fe.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;


/**
 * @author zjy
 * 前端领域平台服务接口类
 */
public interface FeService {
	/**
	 * 加载功能定义信息（全量）
	 * @param funcId
	 * @return
	 */
	public JsonObject funcInit(String funcId) throws EngineException;

	/**
	 * 加载方法全量信息
	 * @param operationId
	 * @return
	 * @throws EngineException
	 */
	public OperationVo loadOperationAll(String operationId) throws EngineException;
	
	/**
	 * 加载组合功能定义的所有信息
	 * @param comboFuncId
	 * @return
	 */
	public ComboFuncDefineVo comboFuncInit(String comboFuncId) throws EngineException;
	
	/**
	 * 加载功能定义对应的功能模板信息
	 * @param funcId
	 * @return
	 */
	public FuncDefineVo loadFuncDefineAndTpl(String funcId);

	/**
	 * 根据value值加载对应的名称
	 * @param attrOptionsId
	 * @param attrValue
	 * @param parentAttrValue
	 * @return
	 */
	public String loadLabelForObjectAttr(String attrOptionsId, String attrValue, String parentAttrValue, JsonArray params);
	
	/**
	 * 加载下拉列表信息
	 * @param attrOptionsId
	 * @param parentAttrValue
	 * @return
	 */
	public JsonArray loadOptionsForObjectAttr(String attrOptionsId, String parentAttrValue, JsonArray params);
	
	/**
     * 根据数据集明细列表加载数据集数据
     * @param loadReportDataReq
     * @return
     */
    public JsonArray loadReportDatas(LoadReportDataReq loadReportDataReq) throws EngineException;

	/**
     * 根据数据集明细列表加载数据集数据
     * @param loadReportDataReq
     * @return
     */
    public JsonObject loadChartDatas(LoadReportDataReq loadReportDataReq) throws EngineException;
}
