package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 值传递对象领域方法
 *
 * @author zjy
 * createTime 2025年04月28日-14:59:02
 */
@Service
public class ValueObjectService {
    private static Logger logger = LoggerFactory.getLogger(ValueObjectService.class);
	
	private static final String VO = "VO";

    @Autowired
    private ValueObject valueObjectSV;//值传递对象领域对象

    @Autowired
    private AttributeService attributeService;//属性领域方法

    /**
	 * 根据表结构信息，生成值传递对象以及关联的属性信息
	 * @param tableList 表列表，包括字段信息
	 * @param domain 生成实体对象归属的业务域
	 * @return
	 */
	public List<ValueObject> generateValueObject(List<MdaTable> tableList, BusinessDomain domain, JsonArray ignorePrefixes, String dbSchemaId) throws EngineException{
		if(logger.isDebugEnabled()) {
			logger.debug("generateValueObject starting");
		}
		if(tableList == null || tableList.size() == 0) {
			logger.info("table list is empty!");
			return null;
		}
		Date nowDate = new Date();
		List<ValueObject> result = new ArrayList<>();
		ValueObject newObject = null;
		String code = null;
		for(MdaTable row : tableList) {
			newObject = new ValueObject();
			code = row.getTableName();
			if(ignorePrefixes != null && ignorePrefixes.size() > 0) {
				for(int i=0; i<ignorePrefixes.size(); i++) {
					if(code.startsWith(ignorePrefixes.get(i).getAsString())) {
						code = code.replaceFirst(ignorePrefixes.get(i).getAsString(), "");
						break;
					}
				}
			}
			newObject.setCode(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, code) + VO);
			if(row.getTableRemarks() != null) {
				newObject.setName(row.getTableRemarks()+VO);
			}else {
				newObject.setName(newObject.getCode());
			}
			newObject.setDomainId(domain.getId());
			newObject.setSystemId(domain.getSystemId());
			newObject.setTenantId(domain.getTenantId());
			newObject.setDescription(newObject.getName());
			newObject.setCreateTime(nowDate);
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(Status.EFF);
			
			if(valueObjectSV.save(newObject) <= 0) {
				logger.error("create ValueObject record failed: {}", JsonUtils.toJson(newObject));
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create ValueObject record failed!");
			}					
			newObject.setAttributes(attributeService.generateAttributes(row.getTableColumns(), newObject, ManagedObjectType.VALUE_OBJECT));
			
			result.add(newObject);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("generateValueObject end");
		}
		return result;
	}
}
