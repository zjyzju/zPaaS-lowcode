package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.MessageConsumerMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class MessageConsumer {
    private String id;

    private String name;

    private String description;

    private String consumerResourceId;

    private String messageTopics;

    private String serviceObjectId;

    private String operationId;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private MessageConsumerMapper mapper;
    
    public MessageConsumer byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public MessageConsumer save(MessageConsumer messageConsumer) {
    	if(messageConsumer.getId() == null || messageConsumer.getId().trim().length() == 0) {
    		messageConsumer.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	messageConsumer.setCreateTime(nowDate);
    	messageConsumer.setUpdateTime(nowDate);
    	messageConsumer.setStatus(Status.EFF);
    	
    	if(mapper.insert(messageConsumer) > 0) {
    		return messageConsumer;
    	}else {
    		return null;
    	}
    }
    
    public List<MessageConsumer> list(String systemId) {
    	MessageConsumerExample criteria = new MessageConsumerExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<MessageConsumer> list() {
    	MessageConsumerExample criteria = new MessageConsumerExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public MessageConsumer bySystemAndId(String systemId, String id) {
    	MessageConsumerExample criteria = new MessageConsumerExample();
    	criteria.createCriteria().andIdEqualTo(id).andSystemIdEqualTo(systemId);
    	List<MessageConsumer> messageConsumers = mapper.selectByExample(criteria);
    	if(messageConsumers != null && !messageConsumers.isEmpty()) {
    		return messageConsumers.get(0);
    	}else {
    		return null;
    	}
    }
    
    public int delete(String systemId, String id) {
    	MessageConsumer messageConsumer = this.bySystemAndId(systemId, id);
    	if(messageConsumer == null) {
    		return 0;
    	}
    	messageConsumer.setStatus(Status.EXP);
    	messageConsumer.setUpdateTime(new Date());
    	return mapper.updateByPrimaryKey(messageConsumer);
    }
    
    public MessageConsumer update(MessageConsumer messageConsumer) {
    	messageConsumer.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(messageConsumer) > 0) {
    		return messageConsumer;
    	}else {
    		return null;
    	}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getConsumerResourceId() {
        return consumerResourceId;
    }

    public void setConsumerResourceId(String consumerResourceId) {
        this.consumerResourceId = consumerResourceId == null ? null : consumerResourceId.trim();
    }

    public String getMessageTopics() {
        return messageTopics;
    }

    public void setMessageTopics(String messageTopics) {
        this.messageTopics = messageTopics == null ? null : messageTopics.trim();
    }

    public String getServiceObjectId() {
        return serviceObjectId;
    }

    public void setServiceObjectId(String serviceObjectId) {
        this.serviceObjectId = serviceObjectId == null ? null : serviceObjectId.trim();
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId == null ? null : operationId.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}