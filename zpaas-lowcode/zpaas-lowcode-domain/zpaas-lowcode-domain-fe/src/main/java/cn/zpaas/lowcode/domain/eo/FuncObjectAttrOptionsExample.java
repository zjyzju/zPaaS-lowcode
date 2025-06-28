package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FuncObjectAttrOptionsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FuncObjectAttrOptionsExample() {
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

        public Criteria andInteractionTypeIsNull() {
            addCriterion("interaction_type is null");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeIsNotNull() {
            addCriterion("interaction_type is not null");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeEqualTo(String value) {
            addCriterion("interaction_type =", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeNotEqualTo(String value) {
            addCriterion("interaction_type <>", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeGreaterThan(String value) {
            addCriterion("interaction_type >", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeGreaterThanOrEqualTo(String value) {
            addCriterion("interaction_type >=", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeLessThan(String value) {
            addCriterion("interaction_type <", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeLessThanOrEqualTo(String value) {
            addCriterion("interaction_type <=", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeLike(String value) {
            addCriterion("interaction_type like", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeNotLike(String value) {
            addCriterion("interaction_type not like", value, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeIn(List<String> values) {
            addCriterion("interaction_type in", values, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeNotIn(List<String> values) {
            addCriterion("interaction_type not in", values, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeBetween(String value1, String value2) {
            addCriterion("interaction_type between", value1, value2, "interactionType");
            return (Criteria) this;
        }

        public Criteria andInteractionTypeNotBetween(String value1, String value2) {
            addCriterion("interaction_type not between", value1, value2, "interactionType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeIsNull() {
            addCriterion("option_cfg_type is null");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeIsNotNull() {
            addCriterion("option_cfg_type is not null");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeEqualTo(String value) {
            addCriterion("option_cfg_type =", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeNotEqualTo(String value) {
            addCriterion("option_cfg_type <>", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeGreaterThan(String value) {
            addCriterion("option_cfg_type >", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeGreaterThanOrEqualTo(String value) {
            addCriterion("option_cfg_type >=", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeLessThan(String value) {
            addCriterion("option_cfg_type <", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeLessThanOrEqualTo(String value) {
            addCriterion("option_cfg_type <=", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeLike(String value) {
            addCriterion("option_cfg_type like", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeNotLike(String value) {
            addCriterion("option_cfg_type not like", value, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeIn(List<String> values) {
            addCriterion("option_cfg_type in", values, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeNotIn(List<String> values) {
            addCriterion("option_cfg_type not in", values, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeBetween(String value1, String value2) {
            addCriterion("option_cfg_type between", value1, value2, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgTypeNotBetween(String value1, String value2) {
            addCriterion("option_cfg_type not between", value1, value2, "optionCfgType");
            return (Criteria) this;
        }

        public Criteria andOptionCfgIsNull() {
            addCriterion("option_cfg is null");
            return (Criteria) this;
        }

        public Criteria andOptionCfgIsNotNull() {
            addCriterion("option_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andOptionCfgEqualTo(String value) {
            addCriterion("option_cfg =", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgNotEqualTo(String value) {
            addCriterion("option_cfg <>", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgGreaterThan(String value) {
            addCriterion("option_cfg >", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgGreaterThanOrEqualTo(String value) {
            addCriterion("option_cfg >=", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgLessThan(String value) {
            addCriterion("option_cfg <", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgLessThanOrEqualTo(String value) {
            addCriterion("option_cfg <=", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgLike(String value) {
            addCriterion("option_cfg like", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgNotLike(String value) {
            addCriterion("option_cfg not like", value, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgIn(List<String> values) {
            addCriterion("option_cfg in", values, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgNotIn(List<String> values) {
            addCriterion("option_cfg not in", values, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgBetween(String value1, String value2) {
            addCriterion("option_cfg between", value1, value2, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andOptionCfgNotBetween(String value1, String value2) {
            addCriterion("option_cfg not between", value1, value2, "optionCfg");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdIsNull() {
            addCriterion("parent_object_assign_id is null");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdIsNotNull() {
            addCriterion("parent_object_assign_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdEqualTo(String value) {
            addCriterion("parent_object_assign_id =", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdNotEqualTo(String value) {
            addCriterion("parent_object_assign_id <>", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdGreaterThan(String value) {
            addCriterion("parent_object_assign_id >", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdGreaterThanOrEqualTo(String value) {
            addCriterion("parent_object_assign_id >=", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdLessThan(String value) {
            addCriterion("parent_object_assign_id <", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdLessThanOrEqualTo(String value) {
            addCriterion("parent_object_assign_id <=", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdLike(String value) {
            addCriterion("parent_object_assign_id like", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdNotLike(String value) {
            addCriterion("parent_object_assign_id not like", value, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdIn(List<String> values) {
            addCriterion("parent_object_assign_id in", values, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdNotIn(List<String> values) {
            addCriterion("parent_object_assign_id not in", values, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdBetween(String value1, String value2) {
            addCriterion("parent_object_assign_id between", value1, value2, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentObjectAssignIdNotBetween(String value1, String value2) {
            addCriterion("parent_object_assign_id not between", value1, value2, "parentObjectAssignId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdIsNull() {
            addCriterion("parent_attribute_id is null");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdIsNotNull() {
            addCriterion("parent_attribute_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdEqualTo(String value) {
            addCriterion("parent_attribute_id =", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdNotEqualTo(String value) {
            addCriterion("parent_attribute_id <>", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdGreaterThan(String value) {
            addCriterion("parent_attribute_id >", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdGreaterThanOrEqualTo(String value) {
            addCriterion("parent_attribute_id >=", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdLessThan(String value) {
            addCriterion("parent_attribute_id <", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdLessThanOrEqualTo(String value) {
            addCriterion("parent_attribute_id <=", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdLike(String value) {
            addCriterion("parent_attribute_id like", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdNotLike(String value) {
            addCriterion("parent_attribute_id not like", value, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdIn(List<String> values) {
            addCriterion("parent_attribute_id in", values, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdNotIn(List<String> values) {
            addCriterion("parent_attribute_id not in", values, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdBetween(String value1, String value2) {
            addCriterion("parent_attribute_id between", value1, value2, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andParentAttributeIdNotBetween(String value1, String value2) {
            addCriterion("parent_attribute_id not between", value1, value2, "parentAttributeId");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgIsNull() {
            addCriterion("display_hide_cfg is null");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgIsNotNull() {
            addCriterion("display_hide_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgEqualTo(String value) {
            addCriterion("display_hide_cfg =", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgNotEqualTo(String value) {
            addCriterion("display_hide_cfg <>", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgGreaterThan(String value) {
            addCriterion("display_hide_cfg >", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgGreaterThanOrEqualTo(String value) {
            addCriterion("display_hide_cfg >=", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgLessThan(String value) {
            addCriterion("display_hide_cfg <", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgLessThanOrEqualTo(String value) {
            addCriterion("display_hide_cfg <=", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgLike(String value) {
            addCriterion("display_hide_cfg like", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgNotLike(String value) {
            addCriterion("display_hide_cfg not like", value, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgIn(List<String> values) {
            addCriterion("display_hide_cfg in", values, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgNotIn(List<String> values) {
            addCriterion("display_hide_cfg not in", values, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgBetween(String value1, String value2) {
            addCriterion("display_hide_cfg between", value1, value2, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andDisplayHideCfgNotBetween(String value1, String value2) {
            addCriterion("display_hide_cfg not between", value1, value2, "displayHideCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgIsNull() {
            addCriterion("popup_cfg is null");
            return (Criteria) this;
        }

        public Criteria andPopupCfgIsNotNull() {
            addCriterion("popup_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andPopupCfgEqualTo(String value) {
            addCriterion("popup_cfg =", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgNotEqualTo(String value) {
            addCriterion("popup_cfg <>", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgGreaterThan(String value) {
            addCriterion("popup_cfg >", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgGreaterThanOrEqualTo(String value) {
            addCriterion("popup_cfg >=", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgLessThan(String value) {
            addCriterion("popup_cfg <", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgLessThanOrEqualTo(String value) {
            addCriterion("popup_cfg <=", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgLike(String value) {
            addCriterion("popup_cfg like", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgNotLike(String value) {
            addCriterion("popup_cfg not like", value, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgIn(List<String> values) {
            addCriterion("popup_cfg in", values, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgNotIn(List<String> values) {
            addCriterion("popup_cfg not in", values, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgBetween(String value1, String value2) {
            addCriterion("popup_cfg between", value1, value2, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andPopupCfgNotBetween(String value1, String value2) {
            addCriterion("popup_cfg not between", value1, value2, "popupCfg");
            return (Criteria) this;
        }

        public Criteria andQueryTypeIsNull() {
            addCriterion("query_type is null");
            return (Criteria) this;
        }

        public Criteria andQueryTypeIsNotNull() {
            addCriterion("query_type is not null");
            return (Criteria) this;
        }

        public Criteria andQueryTypeEqualTo(String value) {
            addCriterion("query_type =", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeNotEqualTo(String value) {
            addCriterion("query_type <>", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeGreaterThan(String value) {
            addCriterion("query_type >", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeGreaterThanOrEqualTo(String value) {
            addCriterion("query_type >=", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeLessThan(String value) {
            addCriterion("query_type <", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeLessThanOrEqualTo(String value) {
            addCriterion("query_type <=", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeLike(String value) {
            addCriterion("query_type like", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeNotLike(String value) {
            addCriterion("query_type not like", value, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeIn(List<String> values) {
            addCriterion("query_type in", values, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeNotIn(List<String> values) {
            addCriterion("query_type not in", values, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeBetween(String value1, String value2) {
            addCriterion("query_type between", value1, value2, "queryType");
            return (Criteria) this;
        }

        public Criteria andQueryTypeNotBetween(String value1, String value2) {
            addCriterion("query_type not between", value1, value2, "queryType");
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