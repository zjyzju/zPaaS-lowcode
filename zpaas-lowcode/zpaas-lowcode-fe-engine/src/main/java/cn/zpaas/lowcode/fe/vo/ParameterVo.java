package cn.zpaas.lowcode.fe.vo;


/**
 * 参数值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:05:55
 */
public class ParameterVo {
    private String name;

    private String code;

    private String isListParam;

    private String paramType;

    private String paramClass;

    private String isInParam;

    private AttributedObjectVo paramClassObject;

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

    public String getIsListParam() {
        return isListParam;
    }

    public void setIsListParam(String isListParam) {
        this.isListParam = isListParam;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamClass() {
        return paramClass;
    }

    public void setParamClass(String paramClass) {
        this.paramClass = paramClass;
    }

    public String getIsInParam() {
        return isInParam;
    }

    public void setIsInParam(String isInParam) {
        this.isInParam = isInParam;
    }

    public AttributedObjectVo getParamClassObject() {
        return paramClassObject;
    }

    public void setParamClassObject(AttributedObjectVo paramClassObject) {
        this.paramClassObject = paramClassObject;
    }

    
}
