package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FuncObjectAssignExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FuncObjectAssignExample() {
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

        public Criteria andFuncIdIsNull() {
            addCriterion("func_id is null");
            return (Criteria) this;
        }

        public Criteria andFuncIdIsNotNull() {
            addCriterion("func_id is not null");
            return (Criteria) this;
        }

        public Criteria andFuncIdEqualTo(String value) {
            addCriterion("func_id =", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotEqualTo(String value) {
            addCriterion("func_id <>", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdGreaterThan(String value) {
            addCriterion("func_id >", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdGreaterThanOrEqualTo(String value) {
            addCriterion("func_id >=", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLessThan(String value) {
            addCriterion("func_id <", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLessThanOrEqualTo(String value) {
            addCriterion("func_id <=", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLike(String value) {
            addCriterion("func_id like", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotLike(String value) {
            addCriterion("func_id not like", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdIn(List<String> values) {
            addCriterion("func_id in", values, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotIn(List<String> values) {
            addCriterion("func_id not in", values, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdBetween(String value1, String value2) {
            addCriterion("func_id between", value1, value2, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotBetween(String value1, String value2) {
            addCriterion("func_id not between", value1, value2, "funcId");
            return (Criteria) this;
        }

        public Criteria andObjectTypeIsNull() {
            addCriterion("object_type is null");
            return (Criteria) this;
        }

        public Criteria andObjectTypeIsNotNull() {
            addCriterion("object_type is not null");
            return (Criteria) this;
        }

        public Criteria andObjectTypeEqualTo(String value) {
            addCriterion("object_type =", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeNotEqualTo(String value) {
            addCriterion("object_type <>", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeGreaterThan(String value) {
            addCriterion("object_type >", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeGreaterThanOrEqualTo(String value) {
            addCriterion("object_type >=", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeLessThan(String value) {
            addCriterion("object_type <", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeLessThanOrEqualTo(String value) {
            addCriterion("object_type <=", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeLike(String value) {
            addCriterion("object_type like", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeNotLike(String value) {
            addCriterion("object_type not like", value, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeIn(List<String> values) {
            addCriterion("object_type in", values, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeNotIn(List<String> values) {
            addCriterion("object_type not in", values, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeBetween(String value1, String value2) {
            addCriterion("object_type between", value1, value2, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectTypeNotBetween(String value1, String value2) {
            addCriterion("object_type not between", value1, value2, "objectType");
            return (Criteria) this;
        }

        public Criteria andObjectIdIsNull() {
            addCriterion("object_id is null");
            return (Criteria) this;
        }

        public Criteria andObjectIdIsNotNull() {
            addCriterion("object_id is not null");
            return (Criteria) this;
        }

        public Criteria andObjectIdEqualTo(String value) {
            addCriterion("object_id =", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotEqualTo(String value) {
            addCriterion("object_id <>", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdGreaterThan(String value) {
            addCriterion("object_id >", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdGreaterThanOrEqualTo(String value) {
            addCriterion("object_id >=", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLessThan(String value) {
            addCriterion("object_id <", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLessThanOrEqualTo(String value) {
            addCriterion("object_id <=", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdLike(String value) {
            addCriterion("object_id like", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotLike(String value) {
            addCriterion("object_id not like", value, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdIn(List<String> values) {
            addCriterion("object_id in", values, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotIn(List<String> values) {
            addCriterion("object_id not in", values, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdBetween(String value1, String value2) {
            addCriterion("object_id between", value1, value2, "objectId");
            return (Criteria) this;
        }

        public Criteria andObjectIdNotBetween(String value1, String value2) {
            addCriterion("object_id not between", value1, value2, "objectId");
            return (Criteria) this;
        }

        public Criteria andAssignTypeIsNull() {
            addCriterion("assign_type is null");
            return (Criteria) this;
        }

        public Criteria andAssignTypeIsNotNull() {
            addCriterion("assign_type is not null");
            return (Criteria) this;
        }

        public Criteria andAssignTypeEqualTo(String value) {
            addCriterion("assign_type =", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeNotEqualTo(String value) {
            addCriterion("assign_type <>", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeGreaterThan(String value) {
            addCriterion("assign_type >", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeGreaterThanOrEqualTo(String value) {
            addCriterion("assign_type >=", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeLessThan(String value) {
            addCriterion("assign_type <", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeLessThanOrEqualTo(String value) {
            addCriterion("assign_type <=", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeLike(String value) {
            addCriterion("assign_type like", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeNotLike(String value) {
            addCriterion("assign_type not like", value, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeIn(List<String> values) {
            addCriterion("assign_type in", values, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeNotIn(List<String> values) {
            addCriterion("assign_type not in", values, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeBetween(String value1, String value2) {
            addCriterion("assign_type between", value1, value2, "assignType");
            return (Criteria) this;
        }

        public Criteria andAssignTypeNotBetween(String value1, String value2) {
            addCriterion("assign_type not between", value1, value2, "assignType");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdIsNull() {
            addCriterion("key_attr_id is null");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdIsNotNull() {
            addCriterion("key_attr_id is not null");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdEqualTo(String value) {
            addCriterion("key_attr_id =", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdNotEqualTo(String value) {
            addCriterion("key_attr_id <>", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdGreaterThan(String value) {
            addCriterion("key_attr_id >", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdGreaterThanOrEqualTo(String value) {
            addCriterion("key_attr_id >=", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdLessThan(String value) {
            addCriterion("key_attr_id <", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdLessThanOrEqualTo(String value) {
            addCriterion("key_attr_id <=", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdLike(String value) {
            addCriterion("key_attr_id like", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdNotLike(String value) {
            addCriterion("key_attr_id not like", value, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdIn(List<String> values) {
            addCriterion("key_attr_id in", values, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdNotIn(List<String> values) {
            addCriterion("key_attr_id not in", values, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdBetween(String value1, String value2) {
            addCriterion("key_attr_id between", value1, value2, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andKeyAttrIdNotBetween(String value1, String value2) {
            addCriterion("key_attr_id not between", value1, value2, "keyAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdIsNull() {
            addCriterion("rel_attr_id is null");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdIsNotNull() {
            addCriterion("rel_attr_id is not null");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdEqualTo(String value) {
            addCriterion("rel_attr_id =", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdNotEqualTo(String value) {
            addCriterion("rel_attr_id <>", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdGreaterThan(String value) {
            addCriterion("rel_attr_id >", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdGreaterThanOrEqualTo(String value) {
            addCriterion("rel_attr_id >=", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdLessThan(String value) {
            addCriterion("rel_attr_id <", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdLessThanOrEqualTo(String value) {
            addCriterion("rel_attr_id <=", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdLike(String value) {
            addCriterion("rel_attr_id like", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdNotLike(String value) {
            addCriterion("rel_attr_id not like", value, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdIn(List<String> values) {
            addCriterion("rel_attr_id in", values, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdNotIn(List<String> values) {
            addCriterion("rel_attr_id not in", values, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdBetween(String value1, String value2) {
            addCriterion("rel_attr_id between", value1, value2, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andRelAttrIdNotBetween(String value1, String value2) {
            addCriterion("rel_attr_id not between", value1, value2, "relAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdIsNull() {
            addCriterion("main_rel_attr_id is null");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdIsNotNull() {
            addCriterion("main_rel_attr_id is not null");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdEqualTo(String value) {
            addCriterion("main_rel_attr_id =", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdNotEqualTo(String value) {
            addCriterion("main_rel_attr_id <>", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdGreaterThan(String value) {
            addCriterion("main_rel_attr_id >", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdGreaterThanOrEqualTo(String value) {
            addCriterion("main_rel_attr_id >=", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdLessThan(String value) {
            addCriterion("main_rel_attr_id <", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdLessThanOrEqualTo(String value) {
            addCriterion("main_rel_attr_id <=", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdLike(String value) {
            addCriterion("main_rel_attr_id like", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdNotLike(String value) {
            addCriterion("main_rel_attr_id not like", value, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdIn(List<String> values) {
            addCriterion("main_rel_attr_id in", values, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdNotIn(List<String> values) {
            addCriterion("main_rel_attr_id not in", values, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdBetween(String value1, String value2) {
            addCriterion("main_rel_attr_id between", value1, value2, "mainRelAttrId");
            return (Criteria) this;
        }

        public Criteria andMainRelAttrIdNotBetween(String value1, String value2) {
            addCriterion("main_rel_attr_id not between", value1, value2, "mainRelAttrId");
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