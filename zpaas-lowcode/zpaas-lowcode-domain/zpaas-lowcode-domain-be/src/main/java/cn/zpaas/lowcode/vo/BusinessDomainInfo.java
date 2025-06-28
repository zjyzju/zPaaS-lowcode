package cn.zpaas.lowcode.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.ServiceObject;

/**
 * 模型驱动辅助生成数据库列vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class BusinessDomainInfo {
	
	private BusinessDomain businessDomain;
	
	private List<ServiceObject> serviceObjects;
	
	private List<DomainObject> domainObjects;
	
	private List<ValueObject> valueObjects;

	public BusinessDomain getBusinessDomain() {
		return businessDomain;
	}

	public void setBusinessDomain(BusinessDomain businessDomain) {
		this.businessDomain = businessDomain;
	}

	public List<ServiceObject> getServiceObjects() {
		return serviceObjects;
	}

	public void setServiceObjects(List<ServiceObject> serviceObjects) {
		this.serviceObjects = serviceObjects;
	}

	public List<DomainObject> getDomainObjects() {
		return domainObjects;
	}

	public void setDomainObjects(List<DomainObject> domainObjects) {
		this.domainObjects = domainObjects;
	}

	public List<ValueObject> getValueObjects() {
		return valueObjects;
	}

	public void setValueObjects(List<ValueObject> valueObjects) {
		this.valueObjects = valueObjects;
	}

	

	
}
