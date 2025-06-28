package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamicMappingDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DynamicMappingDetailExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdIsNull() {
            addCriterion("dynamic_mapping_id is null");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdIsNotNull() {
            addCriterion("dynamic_mapping_id is not null");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdEqualTo(String value) {
            addCriterion("dynamic_mapping_id =", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdNotEqualTo(String value) {
            addCriterion("dynamic_mapping_id <>", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdGreaterThan(String value) {
            addCriterion("dynamic_mapping_id >", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdGreaterThanOrEqualTo(String value) {
            addCriterion("dynamic_mapping_id >=", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdLessThan(String value) {
            addCriterion("dynamic_mapping_id <", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdLessThanOrEqualTo(String value) {
            addCriterion("dynamic_mapping_id <=", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdLike(String value) {
            addCriterion("dynamic_mapping_id like", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdNotLike(String value) {
            addCriterion("dynamic_mapping_id not like", value, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdIn(List<String> values) {
            addCriterion("dynamic_mapping_id in", values, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdNotIn(List<String> values) {
            addCriterion("dynamic_mapping_id not in", values, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdBetween(String value1, String value2) {
            addCriterion("dynamic_mapping_id between", value1, value2, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andDynamicMappingIdNotBetween(String value1, String value2) {
            addCriterion("dynamic_mapping_id not between", value1, value2, "dynamicMappingId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueIsNull() {
            addCriterion("key_attr_value is null");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueIsNotNull() {
            addCriterion("key_attr_value is not null");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueEqualTo(String value) {
            addCriterion("key_attr_value =", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueNotEqualTo(String value) {
            addCriterion("key_attr_value <>", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueGreaterThan(String value) {
            addCriterion("key_attr_value >", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueGreaterThanOrEqualTo(String value) {
            addCriterion("key_attr_value >=", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueLessThan(String value) {
            addCriterion("key_attr_value <", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueLessThanOrEqualTo(String value) {
            addCriterion("key_attr_value <=", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueLike(String value) {
            addCriterion("key_attr_value like", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueNotLike(String value) {
            addCriterion("key_attr_value not like", value, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueIn(List<String> values) {
            addCriterion("key_attr_value in", values, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueNotIn(List<String> values) {
            addCriterion("key_attr_value not in", values, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueBetween(String value1, String value2) {
            addCriterion("key_attr_value between", value1, value2, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andKeyAttrValueNotBetween(String value1, String value2) {
            addCriterion("key_attr_value not between", value1, value2, "keyAttrValue");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdIsNull() {
            addCriterion("mapping_object_id is null");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdIsNotNull() {
            addCriterion("mapping_object_id is not null");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdEqualTo(String value) {
            addCriterion("mapping_object_id =", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdNotEqualTo(String value) {
            addCriterion("mapping_object_id <>", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdGreaterThan(String value) {
            addCriterion("mapping_object_id >", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdGreaterThanOrEqualTo(String value) {
            addCriterion("mapping_object_id >=", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdLessThan(String value) {
            addCriterion("mapping_object_id <", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdLessThanOrEqualTo(String value) {
            addCriterion("mapping_object_id <=", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdLike(String value) {
            addCriterion("mapping_object_id like", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdNotLike(String value) {
            addCriterion("mapping_object_id not like", value, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdIn(List<String> values) {
            addCriterion("mapping_object_id in", values, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdNotIn(List<String> values) {
            addCriterion("mapping_object_id not in", values, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdBetween(String value1, String value2) {
            addCriterion("mapping_object_id between", value1, value2, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andMappingObjectIdNotBetween(String value1, String value2) {
            addCriterion("mapping_object_id not between", value1, value2, "mappingObjectId");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andTenantIdIsNull() {
            addCriterion("tenant_id is null");
            return (Criteria) this;
        }

        public Criteria andTenantIdIsNotNull() {
            addCriterion("tenant_id is not null");
            return (Criteria) this;
        }

        public Criteria andTenantIdEqualTo(String value) {
            addCriterion("tenant_id =", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotEqualTo(String value) {
            addCriterion("tenant_id <>", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdGreaterThan(String value) {
            addCriterion("tenant_id >", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdGreaterThanOrEqualTo(String value) {
            addCriterion("tenant_id >=", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdLessThan(String value) {
            addCriterion("tenant_id <", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdLessThanOrEqualTo(String value) {
            addCriterion("tenant_id <=", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdLike(String value) {
            addCriterion("tenant_id like", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotLike(String value) {
            addCriterion("tenant_id not like", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdIn(List<String> values) {
            addCriterion("tenant_id in", values, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotIn(List<String> values) {
            addCriterion("tenant_id not in", values, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdBetween(String value1, String value2) {
            addCriterion("tenant_id between", value1, value2, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotBetween(String value1, String value2) {
            addCriterion("tenant_id not between", value1, value2, "tenantId");
            return (Criteria) this;
        }

        public Criteria andSystemIdIsNull() {
            addCriterion("system_id is null");
            return (Criteria) this;
        }

        public Criteria andSystemIdIsNotNull() {
            addCriterion("system_id is not null");
            return (Criteria) this;
        }

        public Criteria andSystemIdEqualTo(String value) {
            addCriterion("system_id =", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdNotEqualTo(String value) {
            addCriterion("system_id <>", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdGreaterThan(String value) {
            addCriterion("system_id >", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdGreaterThanOrEqualTo(String value) {
            addCriterion("system_id >=", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdLessThan(String value) {
            addCriterion("system_id <", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdLessThanOrEqualTo(String value) {
            addCriterion("system_id <=", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdLike(String value) {
            addCriterion("system_id like", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdNotLike(String value) {
            addCriterion("system_id not like", value, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdIn(List<String> values) {
            addCriterion("system_id in", values, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdNotIn(List<String> values) {
            addCriterion("system_id not in", values, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdBetween(String value1, String value2) {
            addCriterion("system_id between", value1, value2, "systemId");
            return (Criteria) this;
        }

        public Criteria andSystemIdNotBetween(String value1, String value2) {
            addCriterion("system_id not between", value1, value2, "systemId");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}