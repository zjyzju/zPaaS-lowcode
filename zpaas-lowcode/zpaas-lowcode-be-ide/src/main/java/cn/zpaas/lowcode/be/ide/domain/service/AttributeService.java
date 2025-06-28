package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;

import cn.zpaas.lowcode.bean.JavaTypeResolver;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 属性对象领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:17:49
 */
@Service
public class AttributeService {
    private static Logger logger = LoggerFactory.getLogger(AttributeService.class);

    @Autowired
    private Attribute attributeSV; //属性领域对象

    /**
	 * 根据表字段信息生成领域对象的属性
	 * @param columnList 字段列表
	 * @param domainObject 领域对象
	 * @return
	 * @throws EngineException
	 */
	public List<Attribute> generateAttributes(List<MdaColumn> columnList, AttributedObject attributedObject, String objectType) throws EngineException {
		logger.debug("generateAttributes starting");
		
		if(CollectionUtils.isEmpty(columnList)) {
			logger.info("column list is empty!");
			return new ArrayList<>();
		}
		Date nowDate = new Date();
		List<Attribute> result = new ArrayList<>();
		
		for(MdaColumn row : columnList) {
			Attribute newAttribute = new Attribute();
			newAttribute.setCode(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, row.getColumnName()));
			if(row.getRemarks() != null) {
				newAttribute.setName(row.getRemarks());
			}else {
				newAttribute.setName(newAttribute.getCode());
			}
			newAttribute.setIsListAttr(YesOrNo.NO);
			newAttribute.setAttrType(ManagedObjectType.JAVA_OBJECT);
			newAttribute.setAttrClass(JavaTypeResolver.getInstance().resolveJavaType(row.getDataType()));
			newAttribute.setObjectType(objectType);
			newAttribute.setObjectId(attributedObject.getId());
			newAttribute.setSystemId(attributedObject.getSystemId());
			newAttribute.setTenantId(attributedObject.getTenantId());
			newAttribute.setCreateTime(nowDate);
			newAttribute.setUpdateTime(nowDate);
			
			if(attributeSV.save(newAttribute) <= 0) {
                String jsonString = JsonUtils.toJson(newAttribute);
				logger.error("create Attribute record failed: {}", jsonString);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create Attribute record failed!");
			}	
			result.add(newAttribute);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("generateAttributes end");
		}
		return result;
	}
}
