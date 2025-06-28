package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DictExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DictExample() {
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

        public Criteria andDictCodeIsNull() {
            addCriterion("dict_code is null");
            return (Criteria) this;
        }

        public Criteria andDictCodeIsNotNull() {
            addCriterion("dict_code is not null");
            return (Criteria) this;
        }

        public Criteria andDictCodeEqualTo(String value) {
            addCriterion("dict_code =", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeNotEqualTo(String value) {
            addCriterion("dict_code <>", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeGreaterThan(String value) {
            addCriterion("dict_code >", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeGreaterThanOrEqualTo(String value) {
            addCriterion("dict_code >=", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeLessThan(String value) {
            addCriterion("dict_code <", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeLessThanOrEqualTo(String value) {
            addCriterion("dict_code <=", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeLike(String value) {
            addCriterion("dict_code like", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeNotLike(String value) {
            addCriterion("dict_code not like", value, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeIn(List<String> values) {
            addCriterion("dict_code in", values, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeNotIn(List<String> values) {
            addCriterion("dict_code not in", values, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeBetween(String value1, String value2) {
            addCriterion("dict_code between", value1, value2, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictCodeNotBetween(String value1, String value2) {
            addCriterion("dict_code not between", value1, value2, "dictCode");
            return (Criteria) this;
        }

        public Criteria andDictNameIsNull() {
            addCriterion("dict_name is null");
            return (Criteria) this;
        }

        public Criteria andDictNameIsNotNull() {
            addCriterion("dict_name is not null");
            return (Criteria) this;
        }

        public Criteria andDictNameEqualTo(String value) {
            addCriterion("dict_name =", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameNotEqualTo(String value) {
            addCriterion("dict_name <>", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameGreaterThan(String value) {
            addCriterion("dict_name >", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameGreaterThanOrEqualTo(String value) {
            addCriterion("dict_name >=", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameLessThan(String value) {
            addCriterion("dict_name <", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameLessThanOrEqualTo(String value) {
            addCriterion("dict_name <=", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameLike(String value) {
            addCriterion("dict_name like", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameNotLike(String value) {
            addCriterion("dict_name not like", value, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameIn(List<String> values) {
            addCriterion("dict_name in", values, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameNotIn(List<String> values) {
            addCriterion("dict_name not in", values, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameBetween(String value1, String value2) {
            addCriterion("dict_name between", value1, value2, "dictName");
            return (Criteria) this;
        }

        public Criteria andDictNameNotBetween(String value1, String value2) {
            addCriterion("dict_name not between", value1, value2, "dictName");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeIsNull() {
            addCriterion("parent_dict_code is null");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeIsNotNull() {
            addCriterion("parent_dict_code is not null");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeEqualTo(String value) {
            addCriterion("parent_dict_code =", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeNotEqualTo(String value) {
            addCriterion("parent_dict_code <>", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeGreaterThan(String value) {
            addCriterion("parent_dict_code >", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeGreaterThanOrEqualTo(String value) {
            addCriterion("parent_dict_code >=", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeLessThan(String value) {
            addCriterion("parent_dict_code <", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeLessThanOrEqualTo(String value) {
            addCriterion("parent_dict_code <=", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeLike(String value) {
            addCriterion("parent_dict_code like", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeNotLike(String value) {
            addCriterion("parent_dict_code not like", value, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeIn(List<String> values) {
            addCriterion("parent_dict_code in", values, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeNotIn(List<String> values) {
            addCriterion("parent_dict_code not in", values, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeBetween(String value1, String value2) {
            addCriterion("parent_dict_code between", value1, value2, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeNotBetween(String value1, String value2) {
            addCriterion("parent_dict_code not between", value1, value2, "parentDictCode");
            return (Criteria) this;
        }

        public Criteria andParentDictValueIsNull() {
            addCriterion("parent_dict_value is null");
            return (Criteria) this;
        }

        public Criteria andParentDictValueIsNotNull() {
            addCriterion("parent_dict_value is not null");
            return (Criteria) this;
        }

        public Criteria andParentDictValueEqualTo(String value) {
            addCriterion("parent_dict_value =", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueNotEqualTo(String value) {
            addCriterion("parent_dict_value <>", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueGreaterThan(String value) {
            addCriterion("parent_dict_value >", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueGreaterThanOrEqualTo(String value) {
            addCriterion("parent_dict_value >=", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueLessThan(String value) {
            addCriterion("parent_dict_value <", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueLessThanOrEqualTo(String value) {
            addCriterion("parent_dict_value <=", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueLike(String value) {
            addCriterion("parent_dict_value like", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueNotLike(String value) {
            addCriterion("parent_dict_value not like", value, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueIn(List<String> values) {
            addCriterion("parent_dict_value in", values, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueNotIn(List<String> values) {
            addCriterion("parent_dict_value not in", values, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueBetween(String value1, String value2) {
            addCriterion("parent_dict_value between", value1, value2, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andParentDictValueNotBetween(String value1, String value2) {
            addCriterion("parent_dict_value not between", value1, value2, "parentDictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueIsNull() {
            addCriterion("dict_value is null");
            return (Criteria) this;
        }

        public Criteria andDictValueIsNotNull() {
            addCriterion("dict_value is not null");
            return (Criteria) this;
        }

        public Criteria andDictValueEqualTo(String value) {
            addCriterion("dict_value =", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueNotEqualTo(String value) {
            addCriterion("dict_value <>", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueGreaterThan(String value) {
            addCriterion("dict_value >", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value >=", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueLessThan(String value) {
            addCriterion("dict_value <", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueLessThanOrEqualTo(String value) {
            addCriterion("dict_value <=", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueLike(String value) {
            addCriterion("dict_value like", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueNotLike(String value) {
            addCriterion("dict_value not like", value, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueIn(List<String> values) {
            addCriterion("dict_value in", values, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueNotIn(List<String> values) {
            addCriterion("dict_value not in", values, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueBetween(String value1, String value2) {
            addCriterion("dict_value between", value1, value2, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueNotBetween(String value1, String value2) {
            addCriterion("dict_value not between", value1, value2, "dictValue");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelIsNull() {
            addCriterion("dict_value_label is null");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelIsNotNull() {
            addCriterion("dict_value_label is not null");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelEqualTo(String value) {
            addCriterion("dict_value_label =", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelNotEqualTo(String value) {
            addCriterion("dict_value_label <>", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelGreaterThan(String value) {
            addCriterion("dict_value_label >", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value_label >=", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelLessThan(String value) {
            addCriterion("dict_value_label <", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelLessThanOrEqualTo(String value) {
            addCriterion("dict_value_label <=", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelLike(String value) {
            addCriterion("dict_value_label like", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelNotLike(String value) {
            addCriterion("dict_value_label not like", value, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelIn(List<String> values) {
            addCriterion("dict_value_label in", values, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelNotIn(List<String> values) {
            addCriterion("dict_value_label not in", values, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelBetween(String value1, String value2) {
            addCriterion("dict_value_label between", value1, value2, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelNotBetween(String value1, String value2) {
            addCriterion("dict_value_label not between", value1, value2, "dictValueLabel");
            return (Criteria) this;
        }

        public Criteria andDictValue2IsNull() {
            addCriterion("dict_value2 is null");
            return (Criteria) this;
        }

        public Criteria andDictValue2IsNotNull() {
            addCriterion("dict_value2 is not null");
            return (Criteria) this;
        }

        public Criteria andDictValue2EqualTo(String value) {
            addCriterion("dict_value2 =", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2NotEqualTo(String value) {
            addCriterion("dict_value2 <>", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2GreaterThan(String value) {
            addCriterion("dict_value2 >", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2GreaterThanOrEqualTo(String value) {
            addCriterion("dict_value2 >=", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2LessThan(String value) {
            addCriterion("dict_value2 <", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2LessThanOrEqualTo(String value) {
            addCriterion("dict_value2 <=", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2Like(String value) {
            addCriterion("dict_value2 like", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2NotLike(String value) {
            addCriterion("dict_value2 not like", value, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2In(List<String> values) {
            addCriterion("dict_value2 in", values, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2NotIn(List<String> values) {
            addCriterion("dict_value2 not in", values, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2Between(String value1, String value2) {
            addCriterion("dict_value2 between", value1, value2, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue2NotBetween(String value1, String value2) {
            addCriterion("dict_value2 not between", value1, value2, "dictValue2");
            return (Criteria) this;
        }

        public Criteria andDictValue3IsNull() {
            addCriterion("dict_value3 is null");
            return (Criteria) this;
        }

        public Criteria andDictValue3IsNotNull() {
            addCriterion("dict_value3 is not null");
            return (Criteria) this;
        }

        public Criteria andDictValue3EqualTo(String value) {
            addCriterion("dict_value3 =", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3NotEqualTo(String value) {
            addCriterion("dict_value3 <>", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3GreaterThan(String value) {
            addCriterion("dict_value3 >", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3GreaterThanOrEqualTo(String value) {
            addCriterion("dict_value3 >=", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3LessThan(String value) {
            addCriterion("dict_value3 <", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3LessThanOrEqualTo(String value) {
            addCriterion("dict_value3 <=", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3Like(String value) {
            addCriterion("dict_value3 like", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3NotLike(String value) {
            addCriterion("dict_value3 not like", value, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3In(List<String> values) {
            addCriterion("dict_value3 in", values, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3NotIn(List<String> values) {
            addCriterion("dict_value3 not in", values, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3Between(String value1, String value2) {
            addCriterion("dict_value3 between", value1, value2, "dictValue3");
            return (Criteria) this;
        }

        public Criteria andDictValue3NotBetween(String value1, String value2) {
            addCriterion("dict_value3 not between", value1, value2, "dictValue3");
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

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("status like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("status not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("status not between", value1, value2, "status");
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