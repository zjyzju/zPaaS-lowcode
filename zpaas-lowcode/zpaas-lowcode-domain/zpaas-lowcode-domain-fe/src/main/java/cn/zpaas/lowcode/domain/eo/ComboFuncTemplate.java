package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ComboFuncTemplateMapper;

@Repository
public class ComboFuncTemplate {
    private String id;

    private String name;

    private String url;

    private String description;

    private String status;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private ComboFuncTemplateMapper mapper;
    
    public List<ComboFuncTemplate> list() {
    	ComboFuncTemplateExample criteria = new ComboFuncTemplateExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }

    public ComboFuncTemplate byId(String id) {
    	return mapper.selectByPrimaryKey(id);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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