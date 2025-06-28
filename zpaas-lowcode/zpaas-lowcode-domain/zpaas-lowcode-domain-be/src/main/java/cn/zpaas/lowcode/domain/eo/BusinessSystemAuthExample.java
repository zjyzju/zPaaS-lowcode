package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessSystemAuthExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BusinessSystemAuthExample() {
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

        public Criteria andAuthTypeIsNull() {
            addCriterion("auth_type is null");
            return (Criteria) this;
        }

        public Criteria andAuthTypeIsNotNull() {
            addCriterion("auth_type is not null");
            return (Criteria) this;
        }

        public Criteria andAuthTypeEqualTo(String value) {
            addCriterion("auth_type =", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeNotEqualTo(String value) {
            addCriterion("auth_type <>", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeGreaterThan(String value) {
            addCriterion("auth_type >", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeGreaterThanOrEqualTo(String value) {
            addCriterion("auth_type >=", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeLessThan(String value) {
            addCriterion("auth_type <", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeLessThanOrEqualTo(String value) {
            addCriterion("auth_type <=", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeLike(String value) {
            addCriterion("auth_type like", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeNotLike(String value) {
            addCriterion("auth_type not like", value, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeIn(List<String> values) {
            addCriterion("auth_type in", values, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeNotIn(List<String> values) {
            addCriterion("auth_type not in", values, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeBetween(String value1, String value2) {
            addCriterion("auth_type between", value1, value2, "authType");
            return (Criteria) this;
        }

        public Criteria andAuthTypeNotBetween(String value1, String value2) {
            addCriterion("auth_type not between", value1, value2, "authType");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyIsNull() {
            addCriterion("user_info_key is null");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyIsNotNull() {
            addCriterion("user_info_key is not null");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyEqualTo(String value) {
            addCriterion("user_info_key =", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyNotEqualTo(String value) {
            addCriterion("user_info_key <>", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyGreaterThan(String value) {
            addCriterion("user_info_key >", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyGreaterThanOrEqualTo(String value) {
            addCriterion("user_info_key >=", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyLessThan(String value) {
            addCriterion("user_info_key <", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyLessThanOrEqualTo(String value) {
            addCriterion("user_info_key <=", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyLike(String value) {
            addCriterion("user_info_key like", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyNotLike(String value) {
            addCriterion("user_info_key not like", value, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyIn(List<String> values) {
            addCriterion("user_info_key in", values, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyNotIn(List<String> values) {
            addCriterion("user_info_key not in", values, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyBetween(String value1, String value2) {
            addCriterion("user_info_key between", value1, value2, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserInfoKeyNotBetween(String value1, String value2) {
            addCriterion("user_info_key not between", value1, value2, "userInfoKey");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrIsNull() {
            addCriterion("user_id_attr is null");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrIsNotNull() {
            addCriterion("user_id_attr is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrEqualTo(String value) {
            addCriterion("user_id_attr =", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrNotEqualTo(String value) {
            addCriterion("user_id_attr <>", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrGreaterThan(String value) {
            addCriterion("user_id_attr >", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrGreaterThanOrEqualTo(String value) {
            addCriterion("user_id_attr >=", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrLessThan(String value) {
            addCriterion("user_id_attr <", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrLessThanOrEqualTo(String value) {
            addCriterion("user_id_attr <=", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrLike(String value) {
            addCriterion("user_id_attr like", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrNotLike(String value) {
            addCriterion("user_id_attr not like", value, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrIn(List<String> values) {
            addCriterion("user_id_attr in", values, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrNotIn(List<String> values) {
            addCriterion("user_id_attr not in", values, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrBetween(String value1, String value2) {
            addCriterion("user_id_attr between", value1, value2, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserIdAttrNotBetween(String value1, String value2) {
            addCriterion("user_id_attr not between", value1, value2, "userIdAttr");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceIsNull() {
            addCriterion("user_info_service is null");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceIsNotNull() {
            addCriterion("user_info_service is not null");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceEqualTo(String value) {
            addCriterion("user_info_service =", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceNotEqualTo(String value) {
            addCriterion("user_info_service <>", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceGreaterThan(String value) {
            addCriterion("user_info_service >", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceGreaterThanOrEqualTo(String value) {
            addCriterion("user_info_service >=", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceLessThan(String value) {
            addCriterion("user_info_service <", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceLessThanOrEqualTo(String value) {
            addCriterion("user_info_service <=", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceLike(String value) {
            addCriterion("user_info_service like", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceNotLike(String value) {
            addCriterion("user_info_service not like", value, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceIn(List<String> values) {
            addCriterion("user_info_service in", values, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceNotIn(List<String> values) {
            addCriterion("user_info_service not in", values, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceBetween(String value1, String value2) {
            addCriterion("user_info_service between", value1, value2, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoServiceNotBetween(String value1, String value2) {
            addCriterion("user_info_service not between", value1, value2, "userInfoService");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodIsNull() {
            addCriterion("user_info_method is null");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodIsNotNull() {
            addCriterion("user_info_method is not null");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodEqualTo(String value) {
            addCriterion("user_info_method =", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodNotEqualTo(String value) {
            addCriterion("user_info_method <>", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodGreaterThan(String value) {
            addCriterion("user_info_method >", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodGreaterThanOrEqualTo(String value) {
            addCriterion("user_info_method >=", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodLessThan(String value) {
            addCriterion("user_info_method <", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodLessThanOrEqualTo(String value) {
            addCriterion("user_info_method <=", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodLike(String value) {
            addCriterion("user_info_method like", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodNotLike(String value) {
            addCriterion("user_info_method not like", value, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodIn(List<String> values) {
            addCriterion("user_info_method in", values, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodNotIn(List<String> values) {
            addCriterion("user_info_method not in", values, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodBetween(String value1, String value2) {
            addCriterion("user_info_method between", value1, value2, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andUserInfoMethodNotBetween(String value1, String value2) {
            addCriterion("user_info_method not between", value1, value2, "userInfoMethod");
            return (Criteria) this;
        }

        public Criteria andAuthConfigIsNull() {
            addCriterion("auth_config is null");
            return (Criteria) this;
        }

        public Criteria andAuthConfigIsNotNull() {
            addCriterion("auth_config is not null");
            return (Criteria) this;
        }

        public Criteria andAuthConfigEqualTo(String value) {
            addCriterion("auth_config =", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigNotEqualTo(String value) {
            addCriterion("auth_config <>", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigGreaterThan(String value) {
            addCriterion("auth_config >", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigGreaterThanOrEqualTo(String value) {
            addCriterion("auth_config >=", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigLessThan(String value) {
            addCriterion("auth_config <", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigLessThanOrEqualTo(String value) {
            addCriterion("auth_config <=", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigLike(String value) {
            addCriterion("auth_config like", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigNotLike(String value) {
            addCriterion("auth_config not like", value, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigIn(List<String> values) {
            addCriterion("auth_config in", values, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigNotIn(List<String> values) {
            addCriterion("auth_config not in", values, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigBetween(String value1, String value2) {
            addCriterion("auth_config between", value1, value2, "authConfig");
            return (Criteria) this;
        }

        public Criteria andAuthConfigNotBetween(String value1, String value2) {
            addCriterion("auth_config not between", value1, value2, "authConfig");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsIsNull() {
            addCriterion("ignore_urls is null");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsIsNotNull() {
            addCriterion("ignore_urls is not null");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsEqualTo(String value) {
            addCriterion("ignore_urls =", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsNotEqualTo(String value) {
            addCriterion("ignore_urls <>", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsGreaterThan(String value) {
            addCriterion("ignore_urls >", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsGreaterThanOrEqualTo(String value) {
            addCriterion("ignore_urls >=", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsLessThan(String value) {
            addCriterion("ignore_urls <", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsLessThanOrEqualTo(String value) {
            addCriterion("ignore_urls <=", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsLike(String value) {
            addCriterion("ignore_urls like", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsNotLike(String value) {
            addCriterion("ignore_urls not like", value, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsIn(List<String> values) {
            addCriterion("ignore_urls in", values, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsNotIn(List<String> values) {
            addCriterion("ignore_urls not in", values, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsBetween(String value1, String value2) {
            addCriterion("ignore_urls between", value1, value2, "ignoreUrls");
            return (Criteria) this;
        }

        public Criteria andIgnoreUrlsNotBetween(String value1, String value2) {
            addCriterion("ignore_urls not between", value1, value2, "ignoreUrls");
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