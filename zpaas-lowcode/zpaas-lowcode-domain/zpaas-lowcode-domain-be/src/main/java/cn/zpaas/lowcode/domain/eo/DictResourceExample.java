package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DictResourceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DictResourceExample() {
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

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdIsNull() {
            addCriterion("db_schema_id is null");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdIsNotNull() {
            addCriterion("db_schema_id is not null");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdEqualTo(String value) {
            addCriterion("db_schema_id =", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdNotEqualTo(String value) {
            addCriterion("db_schema_id <>", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdGreaterThan(String value) {
            addCriterion("db_schema_id >", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdGreaterThanOrEqualTo(String value) {
            addCriterion("db_schema_id >=", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdLessThan(String value) {
            addCriterion("db_schema_id <", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdLessThanOrEqualTo(String value) {
            addCriterion("db_schema_id <=", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdLike(String value) {
            addCriterion("db_schema_id like", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdNotLike(String value) {
            addCriterion("db_schema_id not like", value, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdIn(List<String> values) {
            addCriterion("db_schema_id in", values, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdNotIn(List<String> values) {
            addCriterion("db_schema_id not in", values, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdBetween(String value1, String value2) {
            addCriterion("db_schema_id between", value1, value2, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDbSchemaIdNotBetween(String value1, String value2) {
            addCriterion("db_schema_id not between", value1, value2, "dbSchemaId");
            return (Criteria) this;
        }

        public Criteria andDictTableIsNull() {
            addCriterion("dict_table is null");
            return (Criteria) this;
        }

        public Criteria andDictTableIsNotNull() {
            addCriterion("dict_table is not null");
            return (Criteria) this;
        }

        public Criteria andDictTableEqualTo(String value) {
            addCriterion("dict_table =", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableNotEqualTo(String value) {
            addCriterion("dict_table <>", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableGreaterThan(String value) {
            addCriterion("dict_table >", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableGreaterThanOrEqualTo(String value) {
            addCriterion("dict_table >=", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableLessThan(String value) {
            addCriterion("dict_table <", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableLessThanOrEqualTo(String value) {
            addCriterion("dict_table <=", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableLike(String value) {
            addCriterion("dict_table like", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableNotLike(String value) {
            addCriterion("dict_table not like", value, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableIn(List<String> values) {
            addCriterion("dict_table in", values, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableNotIn(List<String> values) {
            addCriterion("dict_table not in", values, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableBetween(String value1, String value2) {
            addCriterion("dict_table between", value1, value2, "dictTable");
            return (Criteria) this;
        }

        public Criteria andDictTableNotBetween(String value1, String value2) {
            addCriterion("dict_table not between", value1, value2, "dictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableIsNull() {
            addCriterion("sub_dict_table is null");
            return (Criteria) this;
        }

        public Criteria andSubDictTableIsNotNull() {
            addCriterion("sub_dict_table is not null");
            return (Criteria) this;
        }

        public Criteria andSubDictTableEqualTo(String value) {
            addCriterion("sub_dict_table =", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableNotEqualTo(String value) {
            addCriterion("sub_dict_table <>", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableGreaterThan(String value) {
            addCriterion("sub_dict_table >", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableGreaterThanOrEqualTo(String value) {
            addCriterion("sub_dict_table >=", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableLessThan(String value) {
            addCriterion("sub_dict_table <", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableLessThanOrEqualTo(String value) {
            addCriterion("sub_dict_table <=", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableLike(String value) {
            addCriterion("sub_dict_table like", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableNotLike(String value) {
            addCriterion("sub_dict_table not like", value, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableIn(List<String> values) {
            addCriterion("sub_dict_table in", values, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableNotIn(List<String> values) {
            addCriterion("sub_dict_table not in", values, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableBetween(String value1, String value2) {
            addCriterion("sub_dict_table between", value1, value2, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andSubDictTableNotBetween(String value1, String value2) {
            addCriterion("sub_dict_table not between", value1, value2, "subDictTable");
            return (Criteria) this;
        }

        public Criteria andDictIdColIsNull() {
            addCriterion("dict_id_col is null");
            return (Criteria) this;
        }

        public Criteria andDictIdColIsNotNull() {
            addCriterion("dict_id_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictIdColEqualTo(String value) {
            addCriterion("dict_id_col =", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColNotEqualTo(String value) {
            addCriterion("dict_id_col <>", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColGreaterThan(String value) {
            addCriterion("dict_id_col >", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_id_col >=", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColLessThan(String value) {
            addCriterion("dict_id_col <", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColLessThanOrEqualTo(String value) {
            addCriterion("dict_id_col <=", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColLike(String value) {
            addCriterion("dict_id_col like", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColNotLike(String value) {
            addCriterion("dict_id_col not like", value, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColIn(List<String> values) {
            addCriterion("dict_id_col in", values, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColNotIn(List<String> values) {
            addCriterion("dict_id_col not in", values, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColBetween(String value1, String value2) {
            addCriterion("dict_id_col between", value1, value2, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictIdColNotBetween(String value1, String value2) {
            addCriterion("dict_id_col not between", value1, value2, "dictIdCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColIsNull() {
            addCriterion("dict_code_col is null");
            return (Criteria) this;
        }

        public Criteria andDictCodeColIsNotNull() {
            addCriterion("dict_code_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictCodeColEqualTo(String value) {
            addCriterion("dict_code_col =", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColNotEqualTo(String value) {
            addCriterion("dict_code_col <>", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColGreaterThan(String value) {
            addCriterion("dict_code_col >", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_code_col >=", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColLessThan(String value) {
            addCriterion("dict_code_col <", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColLessThanOrEqualTo(String value) {
            addCriterion("dict_code_col <=", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColLike(String value) {
            addCriterion("dict_code_col like", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColNotLike(String value) {
            addCriterion("dict_code_col not like", value, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColIn(List<String> values) {
            addCriterion("dict_code_col in", values, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColNotIn(List<String> values) {
            addCriterion("dict_code_col not in", values, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColBetween(String value1, String value2) {
            addCriterion("dict_code_col between", value1, value2, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictCodeColNotBetween(String value1, String value2) {
            addCriterion("dict_code_col not between", value1, value2, "dictCodeCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColIsNull() {
            addCriterion("dict_name_col is null");
            return (Criteria) this;
        }

        public Criteria andDictNameColIsNotNull() {
            addCriterion("dict_name_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictNameColEqualTo(String value) {
            addCriterion("dict_name_col =", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColNotEqualTo(String value) {
            addCriterion("dict_name_col <>", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColGreaterThan(String value) {
            addCriterion("dict_name_col >", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_name_col >=", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColLessThan(String value) {
            addCriterion("dict_name_col <", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColLessThanOrEqualTo(String value) {
            addCriterion("dict_name_col <=", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColLike(String value) {
            addCriterion("dict_name_col like", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColNotLike(String value) {
            addCriterion("dict_name_col not like", value, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColIn(List<String> values) {
            addCriterion("dict_name_col in", values, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColNotIn(List<String> values) {
            addCriterion("dict_name_col not in", values, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColBetween(String value1, String value2) {
            addCriterion("dict_name_col between", value1, value2, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andDictNameColNotBetween(String value1, String value2) {
            addCriterion("dict_name_col not between", value1, value2, "dictNameCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColIsNull() {
            addCriterion("parent_dict_code_col is null");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColIsNotNull() {
            addCriterion("parent_dict_code_col is not null");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColEqualTo(String value) {
            addCriterion("parent_dict_code_col =", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColNotEqualTo(String value) {
            addCriterion("parent_dict_code_col <>", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColGreaterThan(String value) {
            addCriterion("parent_dict_code_col >", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColGreaterThanOrEqualTo(String value) {
            addCriterion("parent_dict_code_col >=", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColLessThan(String value) {
            addCriterion("parent_dict_code_col <", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColLessThanOrEqualTo(String value) {
            addCriterion("parent_dict_code_col <=", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColLike(String value) {
            addCriterion("parent_dict_code_col like", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColNotLike(String value) {
            addCriterion("parent_dict_code_col not like", value, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColIn(List<String> values) {
            addCriterion("parent_dict_code_col in", values, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColNotIn(List<String> values) {
            addCriterion("parent_dict_code_col not in", values, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColBetween(String value1, String value2) {
            addCriterion("parent_dict_code_col between", value1, value2, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictCodeColNotBetween(String value1, String value2) {
            addCriterion("parent_dict_code_col not between", value1, value2, "parentDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColIsNull() {
            addCriterion("parent_dict_value_col is null");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColIsNotNull() {
            addCriterion("parent_dict_value_col is not null");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColEqualTo(String value) {
            addCriterion("parent_dict_value_col =", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColNotEqualTo(String value) {
            addCriterion("parent_dict_value_col <>", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColGreaterThan(String value) {
            addCriterion("parent_dict_value_col >", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColGreaterThanOrEqualTo(String value) {
            addCriterion("parent_dict_value_col >=", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColLessThan(String value) {
            addCriterion("parent_dict_value_col <", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColLessThanOrEqualTo(String value) {
            addCriterion("parent_dict_value_col <=", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColLike(String value) {
            addCriterion("parent_dict_value_col like", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColNotLike(String value) {
            addCriterion("parent_dict_value_col not like", value, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColIn(List<String> values) {
            addCriterion("parent_dict_value_col in", values, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColNotIn(List<String> values) {
            addCriterion("parent_dict_value_col not in", values, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColBetween(String value1, String value2) {
            addCriterion("parent_dict_value_col between", value1, value2, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andParentDictValueColNotBetween(String value1, String value2) {
            addCriterion("parent_dict_value_col not between", value1, value2, "parentDictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColIsNull() {
            addCriterion("dict_value_col is null");
            return (Criteria) this;
        }

        public Criteria andDictValueColIsNotNull() {
            addCriterion("dict_value_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictValueColEqualTo(String value) {
            addCriterion("dict_value_col =", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColNotEqualTo(String value) {
            addCriterion("dict_value_col <>", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColGreaterThan(String value) {
            addCriterion("dict_value_col >", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value_col >=", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColLessThan(String value) {
            addCriterion("dict_value_col <", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColLessThanOrEqualTo(String value) {
            addCriterion("dict_value_col <=", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColLike(String value) {
            addCriterion("dict_value_col like", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColNotLike(String value) {
            addCriterion("dict_value_col not like", value, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColIn(List<String> values) {
            addCriterion("dict_value_col in", values, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColNotIn(List<String> values) {
            addCriterion("dict_value_col not in", values, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColBetween(String value1, String value2) {
            addCriterion("dict_value_col between", value1, value2, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueColNotBetween(String value1, String value2) {
            addCriterion("dict_value_col not between", value1, value2, "dictValueCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColIsNull() {
            addCriterion("dict_value_label_col is null");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColIsNotNull() {
            addCriterion("dict_value_label_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColEqualTo(String value) {
            addCriterion("dict_value_label_col =", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColNotEqualTo(String value) {
            addCriterion("dict_value_label_col <>", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColGreaterThan(String value) {
            addCriterion("dict_value_label_col >", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value_label_col >=", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColLessThan(String value) {
            addCriterion("dict_value_label_col <", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColLessThanOrEqualTo(String value) {
            addCriterion("dict_value_label_col <=", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColLike(String value) {
            addCriterion("dict_value_label_col like", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColNotLike(String value) {
            addCriterion("dict_value_label_col not like", value, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColIn(List<String> values) {
            addCriterion("dict_value_label_col in", values, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColNotIn(List<String> values) {
            addCriterion("dict_value_label_col not in", values, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColBetween(String value1, String value2) {
            addCriterion("dict_value_label_col between", value1, value2, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValueLabelColNotBetween(String value1, String value2) {
            addCriterion("dict_value_label_col not between", value1, value2, "dictValueLabelCol");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColIsNull() {
            addCriterion("dict_value2_col is null");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColIsNotNull() {
            addCriterion("dict_value2_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColEqualTo(String value) {
            addCriterion("dict_value2_col =", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColNotEqualTo(String value) {
            addCriterion("dict_value2_col <>", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColGreaterThan(String value) {
            addCriterion("dict_value2_col >", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value2_col >=", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColLessThan(String value) {
            addCriterion("dict_value2_col <", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColLessThanOrEqualTo(String value) {
            addCriterion("dict_value2_col <=", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColLike(String value) {
            addCriterion("dict_value2_col like", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColNotLike(String value) {
            addCriterion("dict_value2_col not like", value, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColIn(List<String> values) {
            addCriterion("dict_value2_col in", values, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColNotIn(List<String> values) {
            addCriterion("dict_value2_col not in", values, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColBetween(String value1, String value2) {
            addCriterion("dict_value2_col between", value1, value2, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue2ColNotBetween(String value1, String value2) {
            addCriterion("dict_value2_col not between", value1, value2, "dictValue2Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColIsNull() {
            addCriterion("dict_value3_col is null");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColIsNotNull() {
            addCriterion("dict_value3_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColEqualTo(String value) {
            addCriterion("dict_value3_col =", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColNotEqualTo(String value) {
            addCriterion("dict_value3_col <>", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColGreaterThan(String value) {
            addCriterion("dict_value3_col >", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_value3_col >=", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColLessThan(String value) {
            addCriterion("dict_value3_col <", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColLessThanOrEqualTo(String value) {
            addCriterion("dict_value3_col <=", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColLike(String value) {
            addCriterion("dict_value3_col like", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColNotLike(String value) {
            addCriterion("dict_value3_col not like", value, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColIn(List<String> values) {
            addCriterion("dict_value3_col in", values, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColNotIn(List<String> values) {
            addCriterion("dict_value3_col not in", values, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColBetween(String value1, String value2) {
            addCriterion("dict_value3_col between", value1, value2, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictValue3ColNotBetween(String value1, String value2) {
            addCriterion("dict_value3_col not between", value1, value2, "dictValue3Col");
            return (Criteria) this;
        }

        public Criteria andDictStatusColIsNull() {
            addCriterion("dict_status_col is null");
            return (Criteria) this;
        }

        public Criteria andDictStatusColIsNotNull() {
            addCriterion("dict_status_col is not null");
            return (Criteria) this;
        }

        public Criteria andDictStatusColEqualTo(String value) {
            addCriterion("dict_status_col =", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColNotEqualTo(String value) {
            addCriterion("dict_status_col <>", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColGreaterThan(String value) {
            addCriterion("dict_status_col >", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColGreaterThanOrEqualTo(String value) {
            addCriterion("dict_status_col >=", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColLessThan(String value) {
            addCriterion("dict_status_col <", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColLessThanOrEqualTo(String value) {
            addCriterion("dict_status_col <=", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColLike(String value) {
            addCriterion("dict_status_col like", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColNotLike(String value) {
            addCriterion("dict_status_col not like", value, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColIn(List<String> values) {
            addCriterion("dict_status_col in", values, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColNotIn(List<String> values) {
            addCriterion("dict_status_col not in", values, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColBetween(String value1, String value2) {
            addCriterion("dict_status_col between", value1, value2, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andDictStatusColNotBetween(String value1, String value2) {
            addCriterion("dict_status_col not between", value1, value2, "dictStatusCol");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueIsNull() {
            addCriterion("eff_status_value is null");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueIsNotNull() {
            addCriterion("eff_status_value is not null");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueEqualTo(String value) {
            addCriterion("eff_status_value =", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueNotEqualTo(String value) {
            addCriterion("eff_status_value <>", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueGreaterThan(String value) {
            addCriterion("eff_status_value >", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueGreaterThanOrEqualTo(String value) {
            addCriterion("eff_status_value >=", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueLessThan(String value) {
            addCriterion("eff_status_value <", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueLessThanOrEqualTo(String value) {
            addCriterion("eff_status_value <=", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueLike(String value) {
            addCriterion("eff_status_value like", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueNotLike(String value) {
            addCriterion("eff_status_value not like", value, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueIn(List<String> values) {
            addCriterion("eff_status_value in", values, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueNotIn(List<String> values) {
            addCriterion("eff_status_value not in", values, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueBetween(String value1, String value2) {
            addCriterion("eff_status_value between", value1, value2, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffStatusValueNotBetween(String value1, String value2) {
            addCriterion("eff_status_value not between", value1, value2, "effStatusValue");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColIsNull() {
            addCriterion("sub_table_dict_code_col is null");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColIsNotNull() {
            addCriterion("sub_table_dict_code_col is not null");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColEqualTo(String value) {
            addCriterion("sub_table_dict_code_col =", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColNotEqualTo(String value) {
            addCriterion("sub_table_dict_code_col <>", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColGreaterThan(String value) {
            addCriterion("sub_table_dict_code_col >", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColGreaterThanOrEqualTo(String value) {
            addCriterion("sub_table_dict_code_col >=", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColLessThan(String value) {
            addCriterion("sub_table_dict_code_col <", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColLessThanOrEqualTo(String value) {
            addCriterion("sub_table_dict_code_col <=", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColLike(String value) {
            addCriterion("sub_table_dict_code_col like", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColNotLike(String value) {
            addCriterion("sub_table_dict_code_col not like", value, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColIn(List<String> values) {
            addCriterion("sub_table_dict_code_col in", values, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColNotIn(List<String> values) {
            addCriterion("sub_table_dict_code_col not in", values, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColBetween(String value1, String value2) {
            addCriterion("sub_table_dict_code_col between", value1, value2, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableDictCodeColNotBetween(String value1, String value2) {
            addCriterion("sub_table_dict_code_col not between", value1, value2, "subTableDictCodeCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColIsNull() {
            addCriterion("sub_table_status_col is null");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColIsNotNull() {
            addCriterion("sub_table_status_col is not null");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColEqualTo(String value) {
            addCriterion("sub_table_status_col =", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColNotEqualTo(String value) {
            addCriterion("sub_table_status_col <>", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColGreaterThan(String value) {
            addCriterion("sub_table_status_col >", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColGreaterThanOrEqualTo(String value) {
            addCriterion("sub_table_status_col >=", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColLessThan(String value) {
            addCriterion("sub_table_status_col <", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColLessThanOrEqualTo(String value) {
            addCriterion("sub_table_status_col <=", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColLike(String value) {
            addCriterion("sub_table_status_col like", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColNotLike(String value) {
            addCriterion("sub_table_status_col not like", value, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColIn(List<String> values) {
            addCriterion("sub_table_status_col in", values, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColNotIn(List<String> values) {
            addCriterion("sub_table_status_col not in", values, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColBetween(String value1, String value2) {
            addCriterion("sub_table_status_col between", value1, value2, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andSubTableStatusColNotBetween(String value1, String value2) {
            addCriterion("sub_table_status_col not between", value1, value2, "subTableStatusCol");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueIsNull() {
            addCriterion("eff_sub_table_status_value is null");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueIsNotNull() {
            addCriterion("eff_sub_table_status_value is not null");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueEqualTo(String value) {
            addCriterion("eff_sub_table_status_value =", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueNotEqualTo(String value) {
            addCriterion("eff_sub_table_status_value <>", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueGreaterThan(String value) {
            addCriterion("eff_sub_table_status_value >", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueGreaterThanOrEqualTo(String value) {
            addCriterion("eff_sub_table_status_value >=", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueLessThan(String value) {
            addCriterion("eff_sub_table_status_value <", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueLessThanOrEqualTo(String value) {
            addCriterion("eff_sub_table_status_value <=", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueLike(String value) {
            addCriterion("eff_sub_table_status_value like", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueNotLike(String value) {
            addCriterion("eff_sub_table_status_value not like", value, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueIn(List<String> values) {
            addCriterion("eff_sub_table_status_value in", values, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueNotIn(List<String> values) {
            addCriterion("eff_sub_table_status_value not in", values, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueBetween(String value1, String value2) {
            addCriterion("eff_sub_table_status_value between", value1, value2, "effSubTableStatusValue");
            return (Criteria) this;
        }

        public Criteria andEffSubTableStatusValueNotBetween(String value1, String value2) {
            addCriterion("eff_sub_table_status_value not between", value1, value2, "effSubTableStatusValue");
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