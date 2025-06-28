package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSetDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DataSetDetailExample() {
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

        public Criteria andDataSetIdIsNull() {
            addCriterion("data_set_id is null");
            return (Criteria) this;
        }

        public Criteria andDataSetIdIsNotNull() {
            addCriterion("data_set_id is not null");
            return (Criteria) this;
        }

        public Criteria andDataSetIdEqualTo(String value) {
            addCriterion("data_set_id =", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdNotEqualTo(String value) {
            addCriterion("data_set_id <>", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdGreaterThan(String value) {
            addCriterion("data_set_id >", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdGreaterThanOrEqualTo(String value) {
            addCriterion("data_set_id >=", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdLessThan(String value) {
            addCriterion("data_set_id <", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdLessThanOrEqualTo(String value) {
            addCriterion("data_set_id <=", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdLike(String value) {
            addCriterion("data_set_id like", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdNotLike(String value) {
            addCriterion("data_set_id not like", value, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdIn(List<String> values) {
            addCriterion("data_set_id in", values, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdNotIn(List<String> values) {
            addCriterion("data_set_id not in", values, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdBetween(String value1, String value2) {
            addCriterion("data_set_id between", value1, value2, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDataSetIdNotBetween(String value1, String value2) {
            addCriterion("data_set_id not between", value1, value2, "dataSetId");
            return (Criteria) this;
        }

        public Criteria andDetailTypeIsNull() {
            addCriterion("detail_type is null");
            return (Criteria) this;
        }

        public Criteria andDetailTypeIsNotNull() {
            addCriterion("detail_type is not null");
            return (Criteria) this;
        }

        public Criteria andDetailTypeEqualTo(String value) {
            addCriterion("detail_type =", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeNotEqualTo(String value) {
            addCriterion("detail_type <>", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeGreaterThan(String value) {
            addCriterion("detail_type >", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeGreaterThanOrEqualTo(String value) {
            addCriterion("detail_type >=", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeLessThan(String value) {
            addCriterion("detail_type <", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeLessThanOrEqualTo(String value) {
            addCriterion("detail_type <=", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeLike(String value) {
            addCriterion("detail_type like", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeNotLike(String value) {
            addCriterion("detail_type not like", value, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeIn(List<String> values) {
            addCriterion("detail_type in", values, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeNotIn(List<String> values) {
            addCriterion("detail_type not in", values, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeBetween(String value1, String value2) {
            addCriterion("detail_type between", value1, value2, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailTypeNotBetween(String value1, String value2) {
            addCriterion("detail_type not between", value1, value2, "detailType");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdIsNull() {
            addCriterion("detail_content_id is null");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdIsNotNull() {
            addCriterion("detail_content_id is not null");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdEqualTo(String value) {
            addCriterion("detail_content_id =", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdNotEqualTo(String value) {
            addCriterion("detail_content_id <>", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdGreaterThan(String value) {
            addCriterion("detail_content_id >", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdGreaterThanOrEqualTo(String value) {
            addCriterion("detail_content_id >=", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdLessThan(String value) {
            addCriterion("detail_content_id <", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdLessThanOrEqualTo(String value) {
            addCriterion("detail_content_id <=", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdLike(String value) {
            addCriterion("detail_content_id like", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdNotLike(String value) {
            addCriterion("detail_content_id not like", value, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdIn(List<String> values) {
            addCriterion("detail_content_id in", values, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdNotIn(List<String> values) {
            addCriterion("detail_content_id not in", values, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdBetween(String value1, String value2) {
            addCriterion("detail_content_id between", value1, value2, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentIdNotBetween(String value1, String value2) {
            addCriterion("detail_content_id not between", value1, value2, "detailContentId");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasIsNull() {
            addCriterion("detail_content_alias is null");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasIsNotNull() {
            addCriterion("detail_content_alias is not null");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasEqualTo(String value) {
            addCriterion("detail_content_alias =", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasNotEqualTo(String value) {
            addCriterion("detail_content_alias <>", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasGreaterThan(String value) {
            addCriterion("detail_content_alias >", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasGreaterThanOrEqualTo(String value) {
            addCriterion("detail_content_alias >=", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasLessThan(String value) {
            addCriterion("detail_content_alias <", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasLessThanOrEqualTo(String value) {
            addCriterion("detail_content_alias <=", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasLike(String value) {
            addCriterion("detail_content_alias like", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasNotLike(String value) {
            addCriterion("detail_content_alias not like", value, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasIn(List<String> values) {
            addCriterion("detail_content_alias in", values, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasNotIn(List<String> values) {
            addCriterion("detail_content_alias not in", values, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasBetween(String value1, String value2) {
            addCriterion("detail_content_alias between", value1, value2, "detailContentAlias");
            return (Criteria) this;
        }

        public Criteria andDetailContentAliasNotBetween(String value1, String value2) {
            addCriterion("detail_content_alias not between", value1, value2, "detailContentAlias");
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