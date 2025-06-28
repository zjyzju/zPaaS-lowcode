package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FuncTemplateRegionOperationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FuncTemplateRegionOperationExample() {
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdIsNull() {
            addCriterion("tpl_region_id is null");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdIsNotNull() {
            addCriterion("tpl_region_id is not null");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdEqualTo(String value) {
            addCriterion("tpl_region_id =", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdNotEqualTo(String value) {
            addCriterion("tpl_region_id <>", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdGreaterThan(String value) {
            addCriterion("tpl_region_id >", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdGreaterThanOrEqualTo(String value) {
            addCriterion("tpl_region_id >=", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdLessThan(String value) {
            addCriterion("tpl_region_id <", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdLessThanOrEqualTo(String value) {
            addCriterion("tpl_region_id <=", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdLike(String value) {
            addCriterion("tpl_region_id like", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdNotLike(String value) {
            addCriterion("tpl_region_id not like", value, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdIn(List<String> values) {
            addCriterion("tpl_region_id in", values, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdNotIn(List<String> values) {
            addCriterion("tpl_region_id not in", values, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdBetween(String value1, String value2) {
            addCriterion("tpl_region_id between", value1, value2, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andTplRegionIdNotBetween(String value1, String value2) {
            addCriterion("tpl_region_id not between", value1, value2, "tplRegionId");
            return (Criteria) this;
        }

        public Criteria andOperationTypeIsNull() {
            addCriterion("operation_type is null");
            return (Criteria) this;
        }

        public Criteria andOperationTypeIsNotNull() {
            addCriterion("operation_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperationTypeEqualTo(String value) {
            addCriterion("operation_type =", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotEqualTo(String value) {
            addCriterion("operation_type <>", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeGreaterThan(String value) {
            addCriterion("operation_type >", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeGreaterThanOrEqualTo(String value) {
            addCriterion("operation_type >=", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeLessThan(String value) {
            addCriterion("operation_type <", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeLessThanOrEqualTo(String value) {
            addCriterion("operation_type <=", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeLike(String value) {
            addCriterion("operation_type like", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotLike(String value) {
            addCriterion("operation_type not like", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeIn(List<String> values) {
            addCriterion("operation_type in", values, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotIn(List<String> values) {
            addCriterion("operation_type not in", values, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeBetween(String value1, String value2) {
            addCriterion("operation_type between", value1, value2, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotBetween(String value1, String value2) {
            addCriterion("operation_type not between", value1, value2, "operationType");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderIsNull() {
            addCriterion("display_order is null");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderIsNotNull() {
            addCriterion("display_order is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderEqualTo(Integer value) {
            addCriterion("display_order =", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderNotEqualTo(Integer value) {
            addCriterion("display_order <>", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderGreaterThan(Integer value) {
            addCriterion("display_order >", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("display_order >=", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderLessThan(Integer value) {
            addCriterion("display_order <", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderLessThanOrEqualTo(Integer value) {
            addCriterion("display_order <=", value, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderIn(List<Integer> values) {
            addCriterion("display_order in", values, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderNotIn(List<Integer> values) {
            addCriterion("display_order not in", values, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderBetween(Integer value1, Integer value2) {
            addCriterion("display_order between", value1, value2, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andDisplayOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("display_order not between", value1, value2, "displayOrder");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdIsNull() {
            addCriterion("target_tpl_region_id is null");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdIsNotNull() {
            addCriterion("target_tpl_region_id is not null");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdEqualTo(String value) {
            addCriterion("target_tpl_region_id =", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdNotEqualTo(String value) {
            addCriterion("target_tpl_region_id <>", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdGreaterThan(String value) {
            addCriterion("target_tpl_region_id >", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdGreaterThanOrEqualTo(String value) {
            addCriterion("target_tpl_region_id >=", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdLessThan(String value) {
            addCriterion("target_tpl_region_id <", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdLessThanOrEqualTo(String value) {
            addCriterion("target_tpl_region_id <=", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdLike(String value) {
            addCriterion("target_tpl_region_id like", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdNotLike(String value) {
            addCriterion("target_tpl_region_id not like", value, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdIn(List<String> values) {
            addCriterion("target_tpl_region_id in", values, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdNotIn(List<String> values) {
            addCriterion("target_tpl_region_id not in", values, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdBetween(String value1, String value2) {
            addCriterion("target_tpl_region_id between", value1, value2, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andTargetTplRegionIdNotBetween(String value1, String value2) {
            addCriterion("target_tpl_region_id not between", value1, value2, "targetTplRegionId");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeIsNull() {
            addCriterion("display_type is null");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeIsNotNull() {
            addCriterion("display_type is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeEqualTo(String value) {
            addCriterion("display_type =", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeNotEqualTo(String value) {
            addCriterion("display_type <>", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeGreaterThan(String value) {
            addCriterion("display_type >", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeGreaterThanOrEqualTo(String value) {
            addCriterion("display_type >=", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeLessThan(String value) {
            addCriterion("display_type <", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeLessThanOrEqualTo(String value) {
            addCriterion("display_type <=", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeLike(String value) {
            addCriterion("display_type like", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeNotLike(String value) {
            addCriterion("display_type not like", value, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeIn(List<String> values) {
            addCriterion("display_type in", values, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeNotIn(List<String> values) {
            addCriterion("display_type not in", values, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeBetween(String value1, String value2) {
            addCriterion("display_type between", value1, value2, "displayType");
            return (Criteria) this;
        }

        public Criteria andDisplayTypeNotBetween(String value1, String value2) {
            addCriterion("display_type not between", value1, value2, "displayType");
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