package cn.zpaas.lowcode.fe.vo;

/**
 * 功能模板值传递对象
 *
 * @author zjy
 * createTime 2025年04月04日-16:21:17
 */
public class FuncTemplateVo {
    private String id;

    private String name;

    private String url;

    private String templateType;
    
    private String isMainFunc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsMainFunc() {
        return isMainFunc;
    }

    public void setIsMainFunc(String isMainFunc) {
        this.isMainFunc = isMainFunc;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    
}
