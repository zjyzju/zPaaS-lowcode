package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ValueObjectMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class ValueObject extends AttributedObject {
	@Autowired
    private ValueObjectMapper mapper;
    
    @Autowired
    private Attribute attribute;

	@Autowired
	private DomainObject domainObjectSV;
    
    public int save(ValueObject vo) {
    	if(vo.getId() == null || vo.getId().trim().length() == 0) {
    		vo.setId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(vo);
    }
    
    public int updateById(ValueObject vo) {
    	if(vo.getId() == null || vo.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(vo);
    }
    
    public int delete(String id) {
    	ValueObject vo = mapper.selectByPrimaryKey(id);
    	if(vo != null) {
    		
    		vo.setStatus(Status.EXP);
    		vo.setUpdateTime(new Date());
    		ValueObjectExample criteria = new ValueObjectExample();
    		criteria.createCriteria().andIdEqualTo(id).andStatusEqualTo(Status.EFF);
    		return mapper.updateByExample(vo, criteria);
    	}else {
    		return 0;
    	}
    }
    
    public ValueObject byId(String id) {
    	ValueObject valueObject = mapper.selectByPrimaryKey(id);
    	valueObject.setAttributes(attribute.listByObjectId(ManagedObjectType.VALUE_OBJECT, id));
    	return valueObject;
    }

	public ValueObject loadObjectAndAttrsTwoLevel(String id) {
    	ValueObject valueObject = mapper.selectByPrimaryKey(id);
    	valueObject.setAttributes(attribute.listByObjectId(ManagedObjectType.VALUE_OBJECT, id));
		if(!CollectionUtils.isEmpty(valueObject.getAttributes())) {
			for(Attribute attr : valueObject.getAttributes()) {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(attr.getAttrType())) {
					attr.setAttrClassObject(this.domainObjectSV.loadDomainObjectAndAttrs(attr.getAttrClass()));
				}else if(ManagedObjectType.VALUE_OBJECT.equals(attr.getAttrType())) {
					attr.setAttrClassObject(this.byId(attr.getAttrClass()));
				}
			}
		}
    	return valueObject;
    }
    
    public List<ValueObject> list() {
    	ValueObjectExample criteria = new ValueObjectExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ValueObject> listValueObjectBySystemId(String systemId) {
    	ValueObjectExample criteria = new ValueObjectExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("code");
    	return mapper.selectByExample(criteria);
    }
}