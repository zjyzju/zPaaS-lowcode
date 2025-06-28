package cn.zpaas.lowcode.fe.vo;

import java.util.List;

/**
 * 值传递对象-属性对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:18:07
 */
public class AttributedObjectVo {
    private String id;

    private String name;

    private String code;

    private List<AttributeVo> attributes;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AttributeVo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeVo> attributes) {
        this.attributes = attributes;
    }

    
}
