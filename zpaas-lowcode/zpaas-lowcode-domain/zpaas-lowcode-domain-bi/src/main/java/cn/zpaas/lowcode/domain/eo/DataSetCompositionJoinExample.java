package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSetCompositionJoinExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DataSetCompositionJoinExample() {
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

        public Criteria andJoinMainCompoIdIsNull() {
            addCriterion("join_main_compo_id is null");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdIsNotNull() {
            addCriterion("join_main_compo_id is not null");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdEqualTo(String value) {
            addCriterion("join_main_compo_id =", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdNotEqualTo(String value) {
            addCriterion("join_main_compo_id <>", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdGreaterThan(String value) {
            addCriterion("join_main_compo_id >", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdGreaterThanOrEqualTo(String value) {
            addCriterion("join_main_compo_id >=", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdLessThan(String value) {
            addCriterion("join_main_compo_id <", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdLessThanOrEqualTo(String value) {
            addCriterion("join_main_compo_id <=", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdLike(String value) {
            addCriterion("join_main_compo_id like", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdNotLike(String value) {
            addCriterion("join_main_compo_id not like", value, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdIn(List<String> values) {
            addCriterion("join_main_compo_id in", values, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdNotIn(List<String> values) {
            addCriterion("join_main_compo_id not in", values, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdBetween(String value1, String value2) {
            addCriterion("join_main_compo_id between", value1, value2, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andJoinMainCompoIdNotBetween(String value1, String value2) {
            addCriterion("join_main_compo_id not between", value1, value2, "joinMainCompoId");
            return (Criteria) this;
        }

        public Criteria andMainColIdIsNull() {
            addCriterion("main_col_id is null");
            return (Criteria) this;
        }

        public Criteria andMainColIdIsNotNull() {
            addCriterion("main_col_id is not null");
            return (Criteria) this;
        }

        public Criteria andMainColIdEqualTo(String value) {
            addCriterion("main_col_id =", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdNotEqualTo(String value) {
            addCriterion("main_col_id <>", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdGreaterThan(String value) {
            addCriterion("main_col_id >", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdGreaterThanOrEqualTo(String value) {
            addCriterion("main_col_id >=", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdLessThan(String value) {
            addCriterion("main_col_id <", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdLessThanOrEqualTo(String value) {
            addCriterion("main_col_id <=", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdLike(String value) {
            addCriterion("main_col_id like", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdNotLike(String value) {
            addCriterion("main_col_id not like", value, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdIn(List<String> values) {
            addCriterion("main_col_id in", values, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdNotIn(List<String> values) {
            addCriterion("main_col_id not in", values, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdBetween(String value1, String value2) {
            addCriterion("main_col_id between", value1, value2, "mainColId");
            return (Criteria) this;
        }

        public Criteria andMainColIdNotBetween(String value1, String value2) {
            addCriterion("main_col_id not between", value1, value2, "mainColId");
            return (Criteria) this;
        }

        public Criteria andCondTypeIsNull() {
            addCriterion("cond_type is null");
            return (Criteria) this;
        }

        public Criteria andCondTypeIsNotNull() {
            addCriterion("cond_type is not null");
            return (Criteria) this;
        }

        public Criteria andCondTypeEqualTo(String value) {
            addCriterion("cond_type =", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeNotEqualTo(String value) {
            addCriterion("cond_type <>", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeGreaterThan(String value) {
            addCriterion("cond_type >", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeGreaterThanOrEqualTo(String value) {
            addCriterion("cond_type >=", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeLessThan(String value) {
            addCriterion("cond_type <", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeLessThanOrEqualTo(String value) {
            addCriterion("cond_type <=", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeLike(String value) {
            addCriterion("cond_type like", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeNotLike(String value) {
            addCriterion("cond_type not like", value, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeIn(List<String> values) {
            addCriterion("cond_type in", values, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeNotIn(List<String> values) {
            addCriterion("cond_type not in", values, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeBetween(String value1, String value2) {
            addCriterion("cond_type between", value1, value2, "condType");
            return (Criteria) this;
        }

        public Criteria andCondTypeNotBetween(String value1, String value2) {
            addCriterion("cond_type not between", value1, value2, "condType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIsNull() {
            addCriterion("join_type is null");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIsNotNull() {
            addCriterion("join_type is not null");
            return (Criteria) this;
        }

        public Criteria andJoinTypeEqualTo(String value) {
            addCriterion("join_type =", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotEqualTo(String value) {
            addCriterion("join_type <>", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeGreaterThan(String value) {
            addCriterion("join_type >", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeGreaterThanOrEqualTo(String value) {
            addCriterion("join_type >=", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeLessThan(String value) {
            addCriterion("join_type <", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeLessThanOrEqualTo(String value) {
            addCriterion("join_type <=", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeLike(String value) {
            addCriterion("join_type like", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotLike(String value) {
            addCriterion("join_type not like", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIn(List<String> values) {
            addCriterion("join_type in", values, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotIn(List<String> values) {
            addCriterion("join_type not in", values, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeBetween(String value1, String value2) {
            addCriterion("join_type between", value1, value2, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotBetween(String value1, String value2) {
            addCriterion("join_type not between", value1, value2, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdIsNull() {
            addCriterion("join_rel_data_model_id is null");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdIsNotNull() {
            addCriterion("join_rel_data_model_id is not null");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdEqualTo(String value) {
            addCriterion("join_rel_data_model_id =", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdNotEqualTo(String value) {
            addCriterion("join_rel_data_model_id <>", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdGreaterThan(String value) {
            addCriterion("join_rel_data_model_id >", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdGreaterThanOrEqualTo(String value) {
            addCriterion("join_rel_data_model_id >=", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdLessThan(String value) {
            addCriterion("join_rel_data_model_id <", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdLessThanOrEqualTo(String value) {
            addCriterion("join_rel_data_model_id <=", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdLike(String value) {
            addCriterion("join_rel_data_model_id like", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdNotLike(String value) {
            addCriterion("join_rel_data_model_id not like", value, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdIn(List<String> values) {
            addCriterion("join_rel_data_model_id in", values, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdNotIn(List<String> values) {
            addCriterion("join_rel_data_model_id not in", values, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdBetween(String value1, String value2) {
            addCriterion("join_rel_data_model_id between", value1, value2, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andJoinRelDataModelIdNotBetween(String value1, String value2) {
            addCriterion("join_rel_data_model_id not between", value1, value2, "joinRelDataModelId");
            return (Criteria) this;
        }

        public Criteria andRelColTypeIsNull() {
            addCriterion("rel_col_type is null");
            return (Criteria) this;
        }

        public Criteria andRelColTypeIsNotNull() {
            addCriterion("rel_col_type is not null");
            return (Criteria) this;
        }

        public Criteria andRelColTypeEqualTo(String value) {
            addCriterion("rel_col_type =", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeNotEqualTo(String value) {
            addCriterion("rel_col_type <>", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeGreaterThan(String value) {
            addCriterion("rel_col_type >", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeGreaterThanOrEqualTo(String value) {
            addCriterion("rel_col_type >=", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeLessThan(String value) {
            addCriterion("rel_col_type <", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeLessThanOrEqualTo(String value) {
            addCriterion("rel_col_type <=", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeLike(String value) {
            addCriterion("rel_col_type like", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeNotLike(String value) {
            addCriterion("rel_col_type not like", value, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeIn(List<String> values) {
            addCriterion("rel_col_type in", values, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeNotIn(List<String> values) {
            addCriterion("rel_col_type not in", values, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeBetween(String value1, String value2) {
            addCriterion("rel_col_type between", value1, value2, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColTypeNotBetween(String value1, String value2) {
            addCriterion("rel_col_type not between", value1, value2, "relColType");
            return (Criteria) this;
        }

        public Criteria andRelColIdIsNull() {
            addCriterion("rel_col_id is null");
            return (Criteria) this;
        }

        public Criteria andRelColIdIsNotNull() {
            addCriterion("rel_col_id is not null");
            return (Criteria) this;
        }

        public Criteria andRelColIdEqualTo(String value) {
            addCriterion("rel_col_id =", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdNotEqualTo(String value) {
            addCriterion("rel_col_id <>", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdGreaterThan(String value) {
            addCriterion("rel_col_id >", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdGreaterThanOrEqualTo(String value) {
            addCriterion("rel_col_id >=", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdLessThan(String value) {
            addCriterion("rel_col_id <", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdLessThanOrEqualTo(String value) {
            addCriterion("rel_col_id <=", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdLike(String value) {
            addCriterion("rel_col_id like", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdNotLike(String value) {
            addCriterion("rel_col_id not like", value, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdIn(List<String> values) {
            addCriterion("rel_col_id in", values, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdNotIn(List<String> values) {
            addCriterion("rel_col_id not in", values, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdBetween(String value1, String value2) {
            addCriterion("rel_col_id between", value1, value2, "relColId");
            return (Criteria) this;
        }

        public Criteria andRelColIdNotBetween(String value1, String value2) {
            addCriterion("rel_col_id not between", value1, value2, "relColId");
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