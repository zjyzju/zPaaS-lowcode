package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessFlowNodeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BusinessFlowNodeExample() {
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

        public Criteria andFlowNodeIdIsNull() {
            addCriterion("flow_node_id is null");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdIsNotNull() {
            addCriterion("flow_node_id is not null");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdEqualTo(String value) {
            addCriterion("flow_node_id =", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdNotEqualTo(String value) {
            addCriterion("flow_node_id <>", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdGreaterThan(String value) {
            addCriterion("flow_node_id >", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdGreaterThanOrEqualTo(String value) {
            addCriterion("flow_node_id >=", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdLessThan(String value) {
            addCriterion("flow_node_id <", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdLessThanOrEqualTo(String value) {
            addCriterion("flow_node_id <=", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdLike(String value) {
            addCriterion("flow_node_id like", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdNotLike(String value) {
            addCriterion("flow_node_id not like", value, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdIn(List<String> values) {
            addCriterion("flow_node_id in", values, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdNotIn(List<String> values) {
            addCriterion("flow_node_id not in", values, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdBetween(String value1, String value2) {
            addCriterion("flow_node_id between", value1, value2, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andFlowNodeIdNotBetween(String value1, String value2) {
            addCriterion("flow_node_id not between", value1, value2, "flowNodeId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdIsNull() {
            addCriterion("business_flow_id is null");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdIsNotNull() {
            addCriterion("business_flow_id is not null");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdEqualTo(String value) {
            addCriterion("business_flow_id =", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdNotEqualTo(String value) {
            addCriterion("business_flow_id <>", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdGreaterThan(String value) {
            addCriterion("business_flow_id >", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdGreaterThanOrEqualTo(String value) {
            addCriterion("business_flow_id >=", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdLessThan(String value) {
            addCriterion("business_flow_id <", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdLessThanOrEqualTo(String value) {
            addCriterion("business_flow_id <=", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdLike(String value) {
            addCriterion("business_flow_id like", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdNotLike(String value) {
            addCriterion("business_flow_id not like", value, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdIn(List<String> values) {
            addCriterion("business_flow_id in", values, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdNotIn(List<String> values) {
            addCriterion("business_flow_id not in", values, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdBetween(String value1, String value2) {
            addCriterion("business_flow_id between", value1, value2, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andBusinessFlowIdNotBetween(String value1, String value2) {
            addCriterion("business_flow_id not between", value1, value2, "businessFlowId");
            return (Criteria) this;
        }

        public Criteria andNodeOrderIsNull() {
            addCriterion("node_order is null");
            return (Criteria) this;
        }

        public Criteria andNodeOrderIsNotNull() {
            addCriterion("node_order is not null");
            return (Criteria) this;
        }

        public Criteria andNodeOrderEqualTo(Integer value) {
            addCriterion("node_order =", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderNotEqualTo(Integer value) {
            addCriterion("node_order <>", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderGreaterThan(Integer value) {
            addCriterion("node_order >", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("node_order >=", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderLessThan(Integer value) {
            addCriterion("node_order <", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderLessThanOrEqualTo(Integer value) {
            addCriterion("node_order <=", value, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderIn(List<Integer> values) {
            addCriterion("node_order in", values, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderNotIn(List<Integer> values) {
            addCriterion("node_order not in", values, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderBetween(Integer value1, Integer value2) {
            addCriterion("node_order between", value1, value2, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodeOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("node_order not between", value1, value2, "nodeOrder");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgIsNull() {
            addCriterion("node_pre_cfg is null");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgIsNotNull() {
            addCriterion("node_pre_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgEqualTo(String value) {
            addCriterion("node_pre_cfg =", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgNotEqualTo(String value) {
            addCriterion("node_pre_cfg <>", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgGreaterThan(String value) {
            addCriterion("node_pre_cfg >", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgGreaterThanOrEqualTo(String value) {
            addCriterion("node_pre_cfg >=", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgLessThan(String value) {
            addCriterion("node_pre_cfg <", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgLessThanOrEqualTo(String value) {
            addCriterion("node_pre_cfg <=", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgLike(String value) {
            addCriterion("node_pre_cfg like", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgNotLike(String value) {
            addCriterion("node_pre_cfg not like", value, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgIn(List<String> values) {
            addCriterion("node_pre_cfg in", values, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgNotIn(List<String> values) {
            addCriterion("node_pre_cfg not in", values, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgBetween(String value1, String value2) {
            addCriterion("node_pre_cfg between", value1, value2, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodePreCfgNotBetween(String value1, String value2) {
            addCriterion("node_pre_cfg not between", value1, value2, "nodePreCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgIsNull() {
            addCriterion("node_cfg is null");
            return (Criteria) this;
        }

        public Criteria andNodeCfgIsNotNull() {
            addCriterion("node_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andNodeCfgEqualTo(String value) {
            addCriterion("node_cfg =", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgNotEqualTo(String value) {
            addCriterion("node_cfg <>", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgGreaterThan(String value) {
            addCriterion("node_cfg >", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgGreaterThanOrEqualTo(String value) {
            addCriterion("node_cfg >=", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgLessThan(String value) {
            addCriterion("node_cfg <", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgLessThanOrEqualTo(String value) {
            addCriterion("node_cfg <=", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgLike(String value) {
            addCriterion("node_cfg like", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgNotLike(String value) {
            addCriterion("node_cfg not like", value, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgIn(List<String> values) {
            addCriterion("node_cfg in", values, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgNotIn(List<String> values) {
            addCriterion("node_cfg not in", values, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgBetween(String value1, String value2) {
            addCriterion("node_cfg between", value1, value2, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodeCfgNotBetween(String value1, String value2) {
            addCriterion("node_cfg not between", value1, value2, "nodeCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgIsNull() {
            addCriterion("node_post_cfg is null");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgIsNotNull() {
            addCriterion("node_post_cfg is not null");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgEqualTo(String value) {
            addCriterion("node_post_cfg =", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgNotEqualTo(String value) {
            addCriterion("node_post_cfg <>", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgGreaterThan(String value) {
            addCriterion("node_post_cfg >", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgGreaterThanOrEqualTo(String value) {
            addCriterion("node_post_cfg >=", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgLessThan(String value) {
            addCriterion("node_post_cfg <", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgLessThanOrEqualTo(String value) {
            addCriterion("node_post_cfg <=", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgLike(String value) {
            addCriterion("node_post_cfg like", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgNotLike(String value) {
            addCriterion("node_post_cfg not like", value, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgIn(List<String> values) {
            addCriterion("node_post_cfg in", values, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgNotIn(List<String> values) {
            addCriterion("node_post_cfg not in", values, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgBetween(String value1, String value2) {
            addCriterion("node_post_cfg between", value1, value2, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodePostCfgNotBetween(String value1, String value2) {
            addCriterion("node_post_cfg not between", value1, value2, "nodePostCfg");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1IsNull() {
            addCriterion("node_reserved_cfg1 is null");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1IsNotNull() {
            addCriterion("node_reserved_cfg1 is not null");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1EqualTo(String value) {
            addCriterion("node_reserved_cfg1 =", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1NotEqualTo(String value) {
            addCriterion("node_reserved_cfg1 <>", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1GreaterThan(String value) {
            addCriterion("node_reserved_cfg1 >", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1GreaterThanOrEqualTo(String value) {
            addCriterion("node_reserved_cfg1 >=", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1LessThan(String value) {
            addCriterion("node_reserved_cfg1 <", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1LessThanOrEqualTo(String value) {
            addCriterion("node_reserved_cfg1 <=", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1Like(String value) {
            addCriterion("node_reserved_cfg1 like", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1NotLike(String value) {
            addCriterion("node_reserved_cfg1 not like", value, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1In(List<String> values) {
            addCriterion("node_reserved_cfg1 in", values, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1NotIn(List<String> values) {
            addCriterion("node_reserved_cfg1 not in", values, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1Between(String value1, String value2) {
            addCriterion("node_reserved_cfg1 between", value1, value2, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg1NotBetween(String value1, String value2) {
            addCriterion("node_reserved_cfg1 not between", value1, value2, "nodeReservedCfg1");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2IsNull() {
            addCriterion("node_reserved_cfg2 is null");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2IsNotNull() {
            addCriterion("node_reserved_cfg2 is not null");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2EqualTo(String value) {
            addCriterion("node_reserved_cfg2 =", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2NotEqualTo(String value) {
            addCriterion("node_reserved_cfg2 <>", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2GreaterThan(String value) {
            addCriterion("node_reserved_cfg2 >", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2GreaterThanOrEqualTo(String value) {
            addCriterion("node_reserved_cfg2 >=", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2LessThan(String value) {
            addCriterion("node_reserved_cfg2 <", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2LessThanOrEqualTo(String value) {
            addCriterion("node_reserved_cfg2 <=", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2Like(String value) {
            addCriterion("node_reserved_cfg2 like", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2NotLike(String value) {
            addCriterion("node_reserved_cfg2 not like", value, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2In(List<String> values) {
            addCriterion("node_reserved_cfg2 in", values, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2NotIn(List<String> values) {
            addCriterion("node_reserved_cfg2 not in", values, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2Between(String value1, String value2) {
            addCriterion("node_reserved_cfg2 between", value1, value2, "nodeReservedCfg2");
            return (Criteria) this;
        }

        public Criteria andNodeReservedCfg2NotBetween(String value1, String value2) {
            addCriterion("node_reserved_cfg2 not between", value1, value2, "nodeReservedCfg2");
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

        public Criteria andBpmnNodeIdIsNull() {
            addCriterion("bpmn_node_id is null");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdIsNotNull() {
            addCriterion("bpmn_node_id is not null");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdEqualTo(String value) {
            addCriterion("bpmn_node_id =", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdNotEqualTo(String value) {
            addCriterion("bpmn_node_id <>", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdGreaterThan(String value) {
            addCriterion("bpmn_node_id >", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdGreaterThanOrEqualTo(String value) {
            addCriterion("bpmn_node_id >=", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdLessThan(String value) {
            addCriterion("bpmn_node_id <", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdLessThanOrEqualTo(String value) {
            addCriterion("bpmn_node_id <=", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdLike(String value) {
            addCriterion("bpmn_node_id like", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdNotLike(String value) {
            addCriterion("bpmn_node_id not like", value, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdIn(List<String> values) {
            addCriterion("bpmn_node_id in", values, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdNotIn(List<String> values) {
            addCriterion("bpmn_node_id not in", values, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdBetween(String value1, String value2) {
            addCriterion("bpmn_node_id between", value1, value2, "bpmnNodeId");
            return (Criteria) this;
        }

        public Criteria andBpmnNodeIdNotBetween(String value1, String value2) {
            addCriterion("bpmn_node_id not between", value1, value2, "bpmnNodeId");
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