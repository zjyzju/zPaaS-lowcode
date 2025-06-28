package cn.zpaas.lowcode.fe.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.DataSet;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 功能对象绑定领域服务
 *
 * @author zjy
 * createTime 2025年04月05日-09:50:18
 */
@Service
public class FuncObjectAssignService {

    @Autowired
    private FuncObjectAssign funcObjectAssignSV;//领域对象

    @Autowired 
    private DomainObject domainObjectSV;
    
    @Autowired
    private ValueObject valueObjectSV;
    
    @Autowired
    private Attribute attributeSV;

    @Autowired
    private FuncObjectAttrOptionsService funcObjectAttrOptionsService;

	@Autowired
	private DataSet dataSetSV;

    /**
     * 加载绑定对象信息
     * @param funcId
     * @param initFlag
     * @return
     * @throws EngineException
     */
    public List<FuncObjectAssign> loadByFunc(String funcId, boolean initFlag) throws EngineException{
    	List<FuncObjectAssign> list = funcObjectAssignSV.listByFunc(funcId);
    	if(list != null && !list.isEmpty()) {
    		for(FuncObjectAssign objectAssign : list) {
    			List<FuncObjectAttrOptions> attrOptions = funcObjectAttrOptionsService.loadByObjectAssign(objectAssign.getId(), initFlag);
    			if(attrOptions != null && !attrOptions.isEmpty()) {
    				objectAssign.setAttrOptionMap(attrOptions.stream().collect(Collectors.toMap(FuncObjectAttrOptions::getAttributeId, t->t)));
    			}
    			
    			if(ManagedObjectType.DOMAIN_OBJECT.equals(objectAssign.getObjectType())) {
    				objectAssign.setAssignObject(domainObjectSV.loadDomainObjectAndAttrs(objectAssign.getObjectId()));
    			}else if(ManagedObjectType.VALUE_OBJECT.equals(objectAssign.getObjectType())) {
    				objectAssign.setAssignObject(valueObjectSV.byId(objectAssign.getObjectId()));
    			}else if(FuncObjectAssign.OBJECT_TYPE_B.equals(objectAssign.getObjectType())) {
    				objectAssign.setBiDataSet(dataSetSV.loadWithDetails(objectAssign.getObjectId()));
    			}
    			
				if(!FuncObjectAssign.OBJECT_TYPE_B.equals(objectAssign.getObjectType())) {//数据集时不需要处理
					List<Attribute> attributes = attributeSV.listByObjectId(objectAssign.getObjectType(), objectAssign.getObjectId());
					objectAssign.setAttributes(attributes);
					if(attributes != null) {
						for(Attribute attribute : attributes) {
							if(attribute.getId().equals(objectAssign.getKeyAttrId())) {
								objectAssign.setKeyAttribute(attribute);
							}else if(attribute.getId().equals(objectAssign.getRelAttrId())) {
								objectAssign.setRelAttribute(attribute);
							}
						}
					}
					
					if(objectAssign.getMainRelAttrId() != null && objectAssign.getMainRelAttrId().trim().length() > 0) {
						objectAssign.setMainRelAttribute(attributeSV.byId(objectAssign.getMainRelAttrId()));
					}
				}
    			
    		}
    	}
    	
    	return list;
    }

	
}
