package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DomainObjectMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DomainObject extends AttributedObject {
	
	private List<Operation> operations;

	@Autowired
	private DomainObjectMapper mapper;

	@Autowired
	private Attribute attribute;

	public int save(DomainObject domainObject) {
		if (domainObject.getId() == null || domainObject.getId().trim().length() == 0) {
			domainObject.setId(KeyGenerate.uuidKey());
		}
		return mapper.insert(domainObject);
	}

	public int updateById(DomainObject domainObject) {
		if (domainObject.getId() == null || domainObject.getId().trim().length() == 0) {
			return 0;
		}
		return mapper.updateByPrimaryKey(domainObject);
	}

	public DomainObject loadDomainObjectAndAttrs(String id) {
		DomainObject domainObject = mapper.selectByPrimaryKey(id);
		domainObject.setAttributes(attribute.listByObjectId(ManagedObjectType.DOMAIN_OBJECT, id));
		return domainObject;
	}

	public DomainObject loadDomainObjectAndAttrsTwoLevel(String id) {
		DomainObject domainObject = mapper.selectByPrimaryKey(id);
		domainObject.setAttributes(attribute.listByObjectId(ManagedObjectType.DOMAIN_OBJECT, id));
		if(!CollectionUtils.isEmpty(domainObject.getAttributes())) {
			for(Attribute attr : domainObject.getAttributes()) {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(attr.getAttrType())) {
					attr.setAttrClassObject(this.loadDomainObjectAndAttrs(attr.getAttrClass()));
				}
			}
		}
		return domainObject;
	}

	public DomainObject byId(String id) {
		return mapper.selectByPrimaryKey(id);
	}

	public List<DomainObject> list() {
		DomainObjectExample criteria = new DomainObjectExample();
		criteria.createCriteria().andStatusEqualTo(Status.EFF);
		return mapper.selectByExample(criteria);
	}

	public List<DomainObject> listDomainObjectBySystemId(String systemId) {
		DomainObjectExample criteria = new DomainObjectExample();
		criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
		criteria.setOrderByClause("code");
		return mapper.selectByExample(criteria);
	}

	public DomainObject queryDomainObject(String domainId, String objectCode) {
		DomainObjectExample criteria = new DomainObjectExample();
		criteria.createCriteria().andDomainIdEqualTo(domainId).andCodeEqualTo(objectCode).andStatusEqualTo(Status.EFF);
		List<DomainObject> list = mapper.selectByExample(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int delete(String id) {
		DomainObject domainObject = mapper.selectByPrimaryKey(id);
		if (domainObject != null) {
			domainObject.setStatus(Status.EXP);
			domainObject.setUpdateTime(new Date());
			DomainObjectExample criteria = new DomainObjectExample();
			criteria.createCriteria().andIdEqualTo(id).andStatusEqualTo(Status.EFF);
			return mapper.updateByExample(domainObject, criteria);
		} else {
			return 0;
		}
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
}