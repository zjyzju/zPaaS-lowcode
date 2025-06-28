package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecurityKeyResourceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SecurityKeyResourceExample() {
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

        public Criteria andSecurityKeyIsNull() {
            addCriterion("security_key is null");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyIsNotNull() {
            addCriterion("security_key is not null");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyEqualTo(String value) {
            addCriterion("security_key =", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyNotEqualTo(String value) {
            addCriterion("security_key <>", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyGreaterThan(String value) {
            addCriterion("security_key >", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyGreaterThanOrEqualTo(String value) {
            addCriterion("security_key >=", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyLessThan(String value) {
            addCriterion("security_key <", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyLessThanOrEqualTo(String value) {
            addCriterion("security_key <=", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyLike(String value) {
            addCriterion("security_key like", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyNotLike(String value) {
            addCriterion("security_key not like", value, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyIn(List<String> values) {
            addCriterion("security_key in", values, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyNotIn(List<String> values) {
            addCriterion("security_key not in", values, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyBetween(String value1, String value2) {
            addCriterion("security_key between", value1, value2, "securityKey");
            return (Criteria) this;
        }

        public Criteria andSecurityKeyNotBetween(String value1, String value2) {
            addCriterion("security_key not between", value1, value2, "securityKey");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmIsNull() {
            addCriterion("cipher_algorithm is null");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmIsNotNull() {
            addCriterion("cipher_algorithm is not null");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmEqualTo(String value) {
            addCriterion("cipher_algorithm =", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmNotEqualTo(String value) {
            addCriterion("cipher_algorithm <>", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmGreaterThan(String value) {
            addCriterion("cipher_algorithm >", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmGreaterThanOrEqualTo(String value) {
            addCriterion("cipher_algorithm >=", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmLessThan(String value) {
            addCriterion("cipher_algorithm <", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmLessThanOrEqualTo(String value) {
            addCriterion("cipher_algorithm <=", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmLike(String value) {
            addCriterion("cipher_algorithm like", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmNotLike(String value) {
            addCriterion("cipher_algorithm not like", value, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmIn(List<String> values) {
            addCriterion("cipher_algorithm in", values, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmNotIn(List<String> values) {
            addCriterion("cipher_algorithm not in", values, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmBetween(String value1, String value2) {
            addCriterion("cipher_algorithm between", value1, value2, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andCipherAlgorithmNotBetween(String value1, String value2) {
            addCriterion("cipher_algorithm not between", value1, value2, "cipherAlgorithm");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameIsNull() {
            addCriterion("private_key_file_name is null");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameIsNotNull() {
            addCriterion("private_key_file_name is not null");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameEqualTo(String value) {
            addCriterion("private_key_file_name =", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameNotEqualTo(String value) {
            addCriterion("private_key_file_name <>", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameGreaterThan(String value) {
            addCriterion("private_key_file_name >", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameGreaterThanOrEqualTo(String value) {
            addCriterion("private_key_file_name >=", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameLessThan(String value) {
            addCriterion("private_key_file_name <", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameLessThanOrEqualTo(String value) {
            addCriterion("private_key_file_name <=", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameLike(String value) {
            addCriterion("private_key_file_name like", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameNotLike(String value) {
            addCriterion("private_key_file_name not like", value, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameIn(List<String> values) {
            addCriterion("private_key_file_name in", values, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameNotIn(List<String> values) {
            addCriterion("private_key_file_name not in", values, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameBetween(String value1, String value2) {
            addCriterion("private_key_file_name between", value1, value2, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyFileNameNotBetween(String value1, String value2) {
            addCriterion("private_key_file_name not between", value1, value2, "privateKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameIsNull() {
            addCriterion("public_key_file_name is null");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameIsNotNull() {
            addCriterion("public_key_file_name is not null");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameEqualTo(String value) {
            addCriterion("public_key_file_name =", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameNotEqualTo(String value) {
            addCriterion("public_key_file_name <>", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameGreaterThan(String value) {
            addCriterion("public_key_file_name >", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameGreaterThanOrEqualTo(String value) {
            addCriterion("public_key_file_name >=", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameLessThan(String value) {
            addCriterion("public_key_file_name <", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameLessThanOrEqualTo(String value) {
            addCriterion("public_key_file_name <=", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameLike(String value) {
            addCriterion("public_key_file_name like", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameNotLike(String value) {
            addCriterion("public_key_file_name not like", value, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameIn(List<String> values) {
            addCriterion("public_key_file_name in", values, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameNotIn(List<String> values) {
            addCriterion("public_key_file_name not in", values, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameBetween(String value1, String value2) {
            addCriterion("public_key_file_name between", value1, value2, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPublicKeyFileNameNotBetween(String value1, String value2) {
            addCriterion("public_key_file_name not between", value1, value2, "publicKeyFileName");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdIsNull() {
            addCriterion("private_key_pwd is null");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdIsNotNull() {
            addCriterion("private_key_pwd is not null");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdEqualTo(String value) {
            addCriterion("private_key_pwd =", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdNotEqualTo(String value) {
            addCriterion("private_key_pwd <>", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdGreaterThan(String value) {
            addCriterion("private_key_pwd >", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdGreaterThanOrEqualTo(String value) {
            addCriterion("private_key_pwd >=", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdLessThan(String value) {
            addCriterion("private_key_pwd <", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdLessThanOrEqualTo(String value) {
            addCriterion("private_key_pwd <=", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdLike(String value) {
            addCriterion("private_key_pwd like", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdNotLike(String value) {
            addCriterion("private_key_pwd not like", value, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdIn(List<String> values) {
            addCriterion("private_key_pwd in", values, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdNotIn(List<String> values) {
            addCriterion("private_key_pwd not in", values, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdBetween(String value1, String value2) {
            addCriterion("private_key_pwd between", value1, value2, "privateKeyPwd");
            return (Criteria) this;
        }

        public Criteria andPrivateKeyPwdNotBetween(String value1, String value2) {
            addCriterion("private_key_pwd not between", value1, value2, "privateKeyPwd");
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