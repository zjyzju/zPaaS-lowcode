package cn.zpaas.lowcode.fe.vo;



/**
 * 功能区域操作值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-14:21:05
 */
public class FuncRegionOperationVo {
    private String name;

    private String funcRegionId;

    private String tplOperationId;
    
    private String operationType;
    
    private String operationCfg;

    private String isUserDefined;
    
    private String exposedServiceId;

    private String serviceObjectId;

    private String serviceOperationId;

    private String objectAssignId;

    private String targetRegionId;

    private String displayType;

    private Integer displayOrder;

    private String exposedServiceMapping;
    
    private OperationVo operation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFuncRegionId() {
        return funcRegionId;
    }

    public void setFuncRegionId(String funcRegionId) {
        this.funcRegionId = funcRegionId;
    }

    public String getTplOperationId() {
        return tplOperationId;
    }

    public void setTplOperationId(String tplOperationId) {
        this.tplOperationId = tplOperationId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationCfg() {
        return operationCfg;
    }

    public void setOperationCfg(String operationCfg) {
        this.operationCfg = operationCfg;
    }

    public String getIsUserDefined() {
        return isUserDefined;
    }

    public void setIsUserDefined(String isUserDefined) {
        this.isUserDefined = isUserDefined;
    }

    public String getExposedServiceId() {
        return exposedServiceId;
    }

    public void setExposedServiceId(String exposedServiceId) {
        this.exposedServiceId = exposedServiceId;
    }

    public String getServiceObjectId() {
        return serviceObjectId;
    }

    public void setServiceObjectId(String serviceObjectId) {
        this.serviceObjectId = serviceObjectId;
    }

    public String getServiceOperationId() {
        return serviceOperationId;
    }

    public void setServiceOperationId(String serviceOperationId) {
        this.serviceOperationId = serviceOperationId;
    }

    public String getObjectAssignId() {
        return objectAssignId;
    }

    public void setObjectAssignId(String objectAssignId) {
        this.objectAssignId = objectAssignId;
    }

    public String getTargetRegionId() {
        return targetRegionId;
    }

    public void setTargetRegionId(String targetRegionId) {
        this.targetRegionId = targetRegionId;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public OperationVo getOperation() {
        return operation;
    }

    public void setOperation(OperationVo operation) {
        this.operation = operation;
    }

    public String getExposedServiceMapping() {
        return exposedServiceMapping;
    }

    public void setExposedServiceMapping(String exposedServiceMapping) {
        this.exposedServiceMapping = exposedServiceMapping;
    }

    
}
