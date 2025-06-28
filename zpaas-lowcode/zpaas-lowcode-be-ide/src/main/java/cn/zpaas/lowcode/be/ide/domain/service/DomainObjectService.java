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

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 领域对象领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:10:03
 */
@Service
public class DomainObjectService {
    private static Logger logger = LoggerFactory.getLogger(DomainObjectService.class);

    @Autowired
    private DomainObject domainObjectSV;//领域对象

    @Autowired
    private AttributeService attributeService;//属性对象领域服务

    @Autowired
    private ObjectRelationMappingService objectRelationMappingService;//对象关系映射领域服务

    @Autowired
    private OperationService operationService;//方法对象领域服务

    /**
	 * 根据表结构信息，生成领域对象以及关联的一系列信息
	 * @param tableList 表列表，包括字段信息
	 * @param domain 生成实体对象归属的业务域
	 * @return
	 */
	public List<DomainObject> generateEntityObject(List<MdaTable> tableList, BusinessDomain domain, JsonArray ignorePrefixes, String dbSchemaId) throws EngineException{
		if(logger.isDebugEnabled()) {
			logger.debug("generateEntityObject starting");
		}
		if(CollectionUtils.isEmpty(tableList)) {
			logger.info("table list is empty!");
			return null;
		}
		Date nowDate = new Date();
		List<DomainObject> result = new ArrayList<>();
		DomainObject newObject = null;
		ObjectRelationMapping orm = null;
		String code = null;
		for(MdaTable row : tableList) {
			newObject = new DomainObject();
			code = row.getTableName();
			if(ignorePrefixes != null && ignorePrefixes.size() > 0) {
				for(int i=0; i<ignorePrefixes.size(); i++) {
					if(code.startsWith(ignorePrefixes.get(i).getAsString())) {
						code = code.replaceFirst(ignorePrefixes.get(i).getAsString(), "");
						break;
					}
				}
			}
			newObject.setCode(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, code));
			if(row.getTableRemarks() != null) {
				newObject.setName(row.getTableRemarks());
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
			
			if(domainObjectSV.save(newObject) <= 0) {
				logger.error("create DomainObject record failed: {}", JsonUtils.toJson(newObject));
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create DomainObject record failed!");
			}					
			newObject.setAttributes(attributeService.generateAttributes(row.getTableColumns(), newObject, ManagedObjectType.DOMAIN_OBJECT));
			
			orm = objectRelationMappingService.generateObjectRelationMapping(newObject, row);
			if(orm == null) {
				logger.error("generateObjectRelationMapping failed!");
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "generateObjectRelationMapping failed!");
			}
			
			if(!operationService.generateOperations(newObject, dbSchemaId, orm.getId())) {
				logger.error("create Operation record failed: {}", JsonUtils.toJson(newObject));
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create Operation record failed!");
			}
			
			result.add(newObject);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("generateEntityObject end");
		}
		return result;
	}
}
