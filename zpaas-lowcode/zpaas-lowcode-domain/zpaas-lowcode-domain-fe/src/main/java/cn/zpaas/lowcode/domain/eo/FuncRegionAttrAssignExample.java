package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FuncRegionAttrAssignExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FuncRegionAttrAssignExample() {
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

        public Criteria andFuncRegionIdIsNull() {
            addCriterion("func_region_id is null");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdIsNotNull() {
            addCriterion("func_region_id is not null");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdEqualTo(String value) {
            addCriterion("func_region_id =", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdNotEqualTo(String value) {
            addCriterion("func_region_id <>", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdGreaterThan(String value) {
            addCriterion("func_region_id >", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdGreaterThanOrEqualTo(String value) {
            addCriterion("func_region_id >=", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdLessThan(String value) {
            addCriterion("func_region_id <", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdLessThanOrEqualTo(String value) {
            addCriterion("func_region_id <=", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdLike(String value) {
            addCriterion("func_region_id like", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdNotLike(String value) {
            addCriterion("func_region_id not like", value, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdIn(List<String> values) {
            addCriterion("func_region_id in", values, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdNotIn(List<String> values) {
            addCriterion("func_region_id not in", values, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdBetween(String value1, String value2) {
            addCriterion("func_region_id between", value1, value2, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andFuncRegionIdNotBetween(String value1, String value2) {
            addCriterion("func_region_id not between", value1, value2, "funcRegionId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdIsNull() {
            addCriterion("object_assign_id is null");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdIsNotNull() {
            addCriterion("object_assign_id is not null");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdEqualTo(String value) {
            addCriterion("object_assign_id =", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdNotEqualTo(String value) {
            addCriterion("object_assign_id <>", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdGreaterThan(String value) {
            addCriterion("object_assign_id >", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdGreaterThanOrEqualTo(String value) {
            addCriterion("object_assign_id >=", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdLessThan(String value) {
            addCriterion("object_assign_id <", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdLessThanOrEqualTo(String value) {
            addCriterion("object_assign_id <=", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdLike(String value) {
            addCriterion("object_assign_id like", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdNotLike(String value) {
            addCriterion("object_assign_id not like", value, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdIn(List<String> values) {
            addCriterion("object_assign_id in", values, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdNotIn(List<String> values) {
            addCriterion("object_assign_id not in", values, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdBetween(String value1, String value2) {
            addCriterion("object_assign_id between", value1, value2, "objectAssignId");
            return (Criteria) this;
        }

        public Criteria andObjectAssignIdNotBetween(String value1, String value2) {
            addCriterion("object_assign_id not between", value1, value2, "objectAssignId");
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

        public Criteria andAttributeIdIsNull() {
            addCriterion("attribute_id is null");
            return (Criteria) this;
        }

        public Criteria andAttributeIdIsNotNull() {
            addCriterion("attribute_id is not null");
            return (Criteria) this;
        }

        public Criteria andAttributeIdEqualTo(String value) {
            addCriterion("attribute_id =", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdNotEqualTo(String value) {
            addCriterion("attribute_id <>", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdGreaterThan(String value) {
            addCriterion("attribute_id >", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdGreaterThanOrEqualTo(String value) {
            addCriterion("attribute_id >=", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdLessThan(String value) {
            addCriterion("attribute_id <", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdLessThanOrEqualTo(String value) {
            addCriterion("attribute_id <=", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdLike(String value) {
            addCriterion("attribute_id like", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdNotLike(String value) {
            addCriterion("attribute_id not like", value, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdIn(List<String> values) {
            addCriterion("attribute_id in", values, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdNotIn(List<String> values) {
            addCriterion("attribute_id not in", values, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdBetween(String value1, String value2) {
            addCriterion("attribute_id between", value1, value2, "attributeId");
            return (Criteria) this;
        }

        public Criteria andAttributeIdNotBetween(String value1, String value2) {
            addCriterion("attribute_id not between", value1, value2, "attributeId");
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

        public Criteria andDisplayNameIsNull() {
            addCriterion("display_name is null");
            return (Criteria) this;
        }

        public Criteria andDisplayNameIsNotNull() {
            addCriterion("display_name is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayNameEqualTo(String value) {
            addCriterion("display_name =", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameNotEqualTo(String value) {
            addCriterion("display_name <>", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameGreaterThan(String value) {
            addCriterion("display_name >", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameGreaterThanOrEqualTo(String value) {
            addCriterion("display_name >=", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameLessThan(String value) {
            addCriterion("display_name <", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameLessThanOrEqualTo(String value) {
            addCriterion("display_name <=", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameLike(String value) {
            addCriterion("display_name like", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameNotLike(String value) {
            addCriterion("display_name not like", value, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameIn(List<String> values) {
            addCriterion("display_name in", values, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameNotIn(List<String> values) {
            addCriterion("display_name not in", values, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameBetween(String value1, String value2) {
            addCriterion("display_name between", value1, value2, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayNameNotBetween(String value1, String value2) {
            addCriterion("display_name not between", value1, value2, "displayName");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthIsNull() {
            addCriterion("display_width is null");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthIsNotNull() {
            addCriterion("display_width is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthEqualTo(Integer value) {
            addCriterion("display_width =", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthNotEqualTo(Integer value) {
            addCriterion("display_width <>", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthGreaterThan(Integer value) {
            addCriterion("display_width >", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthGreaterThanOrEqualTo(Integer value) {
            addCriterion("display_width >=", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthLessThan(Integer value) {
            addCriterion("display_width <", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthLessThanOrEqualTo(Integer value) {
            addCriterion("display_width <=", value, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthIn(List<Integer> values) {
            addCriterion("display_width in", values, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthNotIn(List<Integer> values) {
            addCriterion("display_width not in", values, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthBetween(Integer value1, Integer value2) {
            addCriterion("display_width between", value1, value2, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andDisplayWidthNotBetween(Integer value1, Integer value2) {
            addCriterion("display_width not between", value1, value2, "displayWidth");
            return (Criteria) this;
        }

        public Criteria andColSpanIsNull() {
            addCriterion("col_span is null");
            return (Criteria) this;
        }

        public Criteria andColSpanIsNotNull() {
            addCriterion("col_span is not null");
            return (Criteria) this;
        }

        public Criteria andColSpanEqualTo(Integer value) {
            addCriterion("col_span =", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanNotEqualTo(Integer value) {
            addCriterion("col_span <>", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanGreaterThan(Integer value) {
            addCriterion("col_span >", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanGreaterThanOrEqualTo(Integer value) {
            addCriterion("col_span >=", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanLessThan(Integer value) {
            addCriterion("col_span <", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanLessThanOrEqualTo(Integer value) {
            addCriterion("col_span <=", value, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanIn(List<Integer> values) {
            addCriterion("col_span in", values, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanNotIn(List<Integer> values) {
            addCriterion("col_span not in", values, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanBetween(Integer value1, Integer value2) {
            addCriterion("col_span between", value1, value2, "colSpan");
            return (Criteria) this;
        }

        public Criteria andColSpanNotBetween(Integer value1, Integer value2) {
            addCriterion("col_span not between", value1, value2, "colSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanIsNull() {
            addCriterion("row_span is null");
            return (Criteria) this;
        }

        public Criteria andRowSpanIsNotNull() {
            addCriterion("row_span is not null");
            return (Criteria) this;
        }

        public Criteria andRowSpanEqualTo(Integer value) {
            addCriterion("row_span =", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanNotEqualTo(Integer value) {
            addCriterion("row_span <>", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanGreaterThan(Integer value) {
            addCriterion("row_span >", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanGreaterThanOrEqualTo(Integer value) {
            addCriterion("row_span >=", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanLessThan(Integer value) {
            addCriterion("row_span <", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanLessThanOrEqualTo(Integer value) {
            addCriterion("row_span <=", value, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanIn(List<Integer> values) {
            addCriterion("row_span in", values, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanNotIn(List<Integer> values) {
            addCriterion("row_span not in", values, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanBetween(Integer value1, Integer value2) {
            addCriterion("row_span between", value1, value2, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andRowSpanNotBetween(Integer value1, Integer value2) {
            addCriterion("row_span not between", value1, value2, "rowSpan");
            return (Criteria) this;
        }

        public Criteria andReadonlyIsNull() {
            addCriterion("readonly is null");
            return (Criteria) this;
        }

        public Criteria andReadonlyIsNotNull() {
            addCriterion("readonly is not null");
            return (Criteria) this;
        }

        public Criteria andReadonlyEqualTo(String value) {
            addCriterion("readonly =", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyNotEqualTo(String value) {
            addCriterion("readonly <>", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyGreaterThan(String value) {
            addCriterion("readonly >", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyGreaterThanOrEqualTo(String value) {
            addCriterion("readonly >=", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyLessThan(String value) {
            addCriterion("readonly <", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyLessThanOrEqualTo(String value) {
            addCriterion("readonly <=", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyLike(String value) {
            addCriterion("readonly like", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyNotLike(String value) {
            addCriterion("readonly not like", value, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyIn(List<String> values) {
            addCriterion("readonly in", values, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyNotIn(List<String> values) {
            addCriterion("readonly not in", values, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyBetween(String value1, String value2) {
            addCriterion("readonly between", value1, value2, "readonly");
            return (Criteria) this;
        }

        public Criteria andReadonlyNotBetween(String value1, String value2) {
            addCriterion("readonly not between", value1, value2, "readonly");
            return (Criteria) this;
        }

        public Criteria andRequiredIsNull() {
            addCriterion("required is null");
            return (Criteria) this;
        }

        public Criteria andRequiredIsNotNull() {
            addCriterion("required is not null");
            return (Criteria) this;
        }

        public Criteria andRequiredEqualTo(String value) {
            addCriterion("required =", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredNotEqualTo(String value) {
            addCriterion("required <>", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredGreaterThan(String value) {
            addCriterion("required >", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredGreaterThanOrEqualTo(String value) {
            addCriterion("required >=", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredLessThan(String value) {
            addCriterion("required <", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredLessThanOrEqualTo(String value) {
            addCriterion("required <=", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredLike(String value) {
            addCriterion("required like", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredNotLike(String value) {
            addCriterion("required not like", value, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredIn(List<String> values) {
            addCriterion("required in", values, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredNotIn(List<String> values) {
            addCriterion("required not in", values, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredBetween(String value1, String value2) {
            addCriterion("required between", value1, value2, "required");
            return (Criteria) this;
        }

        public Criteria andRequiredNotBetween(String value1, String value2) {
            addCriterion("required not between", value1, value2, "required");
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

        public Criteria andDisplayCfgIsNull() {
            addCriterion("display_cfg is null");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgIsNotNull() {
            addCriterion("display_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgEqualTo(String value) {
            addCriterion("display_cfg =", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgNotEqualTo(String value) {
            addCriterion("display_cfg <>", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgGreaterThan(String value) {
            addCriterion("display_cfg >", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgGreaterThanOrEqualTo(String value) {
            addCriterion("display_cfg >=", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgLessThan(String value) {
            addCriterion("display_cfg <", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgLessThanOrEqualTo(String value) {
            addCriterion("display_cfg <=", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgLike(String value) {
            addCriterion("display_cfg like", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgNotLike(String value) {
            addCriterion("display_cfg not like", value, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgIn(List<String> values) {
            addCriterion("display_cfg in", values, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgNotIn(List<String> values) {
            addCriterion("display_cfg not in", values, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgBetween(String value1, String value2) {
            addCriterion("display_cfg between", value1, value2, "displayCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayCfgNotBetween(String value1, String value2) {
            addCriterion("display_cfg not between", value1, value2, "displayCfg");
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