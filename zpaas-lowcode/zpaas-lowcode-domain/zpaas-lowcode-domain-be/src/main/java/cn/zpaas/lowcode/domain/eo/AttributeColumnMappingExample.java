package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttributeColumnMappingExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AttributeColumnMappingExample() {
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

        public Criteria andFileObjectMappingIdIsNull() {
            addCriterion("file_object_mapping_id is null");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdIsNotNull() {
            addCriterion("file_object_mapping_id is not null");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdEqualTo(String value) {
            addCriterion("file_object_mapping_id =", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdNotEqualTo(String value) {
            addCriterion("file_object_mapping_id <>", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdGreaterThan(String value) {
            addCriterion("file_object_mapping_id >", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdGreaterThanOrEqualTo(String value) {
            addCriterion("file_object_mapping_id >=", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdLessThan(String value) {
            addCriterion("file_object_mapping_id <", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdLessThanOrEqualTo(String value) {
            addCriterion("file_object_mapping_id <=", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdLike(String value) {
            addCriterion("file_object_mapping_id like", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdNotLike(String value) {
            addCriterion("file_object_mapping_id not like", value, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdIn(List<String> values) {
            addCriterion("file_object_mapping_id in", values, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdNotIn(List<String> values) {
            addCriterion("file_object_mapping_id not in", values, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdBetween(String value1, String value2) {
            addCriterion("file_object_mapping_id between", value1, value2, "fileObjectMappingId");
            return (Criteria) this;
        }

        public Criteria andFileObjectMappingIdNotBetween(String value1, String value2) {
            addCriterion("file_object_mapping_id not between", value1, value2, "fileObjectMappingId");
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

        public Criteria andColumnIndexIsNull() {
            addCriterion("column_index is null");
            return (Criteria) this;
        }

        public Criteria andColumnIndexIsNotNull() {
            addCriterion("column_index is not null");
            return (Criteria) this;
        }

        public Criteria andColumnIndexEqualTo(String value) {
            addCriterion("column_index =", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexNotEqualTo(String value) {
            addCriterion("column_index <>", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexGreaterThan(String value) {
            addCriterion("column_index >", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexGreaterThanOrEqualTo(String value) {
            addCriterion("column_index >=", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexLessThan(String value) {
            addCriterion("column_index <", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexLessThanOrEqualTo(String value) {
            addCriterion("column_index <=", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexLike(String value) {
            addCriterion("column_index like", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexNotLike(String value) {
            addCriterion("column_index not like", value, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexIn(List<String> values) {
            addCriterion("column_index in", values, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexNotIn(List<String> values) {
            addCriterion("column_index not in", values, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexBetween(String value1, String value2) {
            addCriterion("column_index between", value1, value2, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andColumnIndexNotBetween(String value1, String value2) {
            addCriterion("column_index not between", value1, value2, "columnIndex");
            return (Criteria) this;
        }

        public Criteria andHeaderNameIsNull() {
            addCriterion("header_name is null");
            return (Criteria) this;
        }

        public Criteria andHeaderNameIsNotNull() {
            addCriterion("header_name is not null");
            return (Criteria) this;
        }

        public Criteria andHeaderNameEqualTo(String value) {
            addCriterion("header_name =", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameNotEqualTo(String value) {
            addCriterion("header_name <>", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameGreaterThan(String value) {
            addCriterion("header_name >", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameGreaterThanOrEqualTo(String value) {
            addCriterion("header_name >=", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameLessThan(String value) {
            addCriterion("header_name <", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameLessThanOrEqualTo(String value) {
            addCriterion("header_name <=", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameLike(String value) {
            addCriterion("header_name like", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameNotLike(String value) {
            addCriterion("header_name not like", value, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameIn(List<String> values) {
            addCriterion("header_name in", values, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameNotIn(List<String> values) {
            addCriterion("header_name not in", values, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameBetween(String value1, String value2) {
            addCriterion("header_name between", value1, value2, "headerName");
            return (Criteria) this;
        }

        public Criteria andHeaderNameNotBetween(String value1, String value2) {
            addCriterion("header_name not between", value1, value2, "headerName");
            return (Criteria) this;
        }

        public Criteria andNeedMergeIsNull() {
            addCriterion("need_merge is null");
            return (Criteria) this;
        }

        public Criteria andNeedMergeIsNotNull() {
            addCriterion("need_merge is not null");
            return (Criteria) this;
        }

        public Criteria andNeedMergeEqualTo(String value) {
            addCriterion("need_merge =", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeNotEqualTo(String value) {
            addCriterion("need_merge <>", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeGreaterThan(String value) {
            addCriterion("need_merge >", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeGreaterThanOrEqualTo(String value) {
            addCriterion("need_merge >=", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeLessThan(String value) {
            addCriterion("need_merge <", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeLessThanOrEqualTo(String value) {
            addCriterion("need_merge <=", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeLike(String value) {
            addCriterion("need_merge like", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeNotLike(String value) {
            addCriterion("need_merge not like", value, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeIn(List<String> values) {
            addCriterion("need_merge in", values, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeNotIn(List<String> values) {
            addCriterion("need_merge not in", values, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeBetween(String value1, String value2) {
            addCriterion("need_merge between", value1, value2, "needMerge");
            return (Criteria) this;
        }

        public Criteria andNeedMergeNotBetween(String value1, String value2) {
            addCriterion("need_merge not between", value1, value2, "needMerge");
            return (Criteria) this;
        }

        public Criteria andIsSumIsNull() {
            addCriterion("is_sum is null");
            return (Criteria) this;
        }

        public Criteria andIsSumIsNotNull() {
            addCriterion("is_sum is not null");
            return (Criteria) this;
        }

        public Criteria andIsSumEqualTo(String value) {
            addCriterion("is_sum =", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumNotEqualTo(String value) {
            addCriterion("is_sum <>", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumGreaterThan(String value) {
            addCriterion("is_sum >", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumGreaterThanOrEqualTo(String value) {
            addCriterion("is_sum >=", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumLessThan(String value) {
            addCriterion("is_sum <", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumLessThanOrEqualTo(String value) {
            addCriterion("is_sum <=", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumLike(String value) {
            addCriterion("is_sum like", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumNotLike(String value) {
            addCriterion("is_sum not like", value, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumIn(List<String> values) {
            addCriterion("is_sum in", values, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumNotIn(List<String> values) {
            addCriterion("is_sum not in", values, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumBetween(String value1, String value2) {
            addCriterion("is_sum between", value1, value2, "isSum");
            return (Criteria) this;
        }

        public Criteria andIsSumNotBetween(String value1, String value2) {
            addCriterion("is_sum not between", value1, value2, "isSum");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexIsNull() {
            addCriterion("sum_by_column_index is null");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexIsNotNull() {
            addCriterion("sum_by_column_index is not null");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexEqualTo(String value) {
            addCriterion("sum_by_column_index =", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexNotEqualTo(String value) {
            addCriterion("sum_by_column_index <>", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexGreaterThan(String value) {
            addCriterion("sum_by_column_index >", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexGreaterThanOrEqualTo(String value) {
            addCriterion("sum_by_column_index >=", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexLessThan(String value) {
            addCriterion("sum_by_column_index <", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexLessThanOrEqualTo(String value) {
            addCriterion("sum_by_column_index <=", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexLike(String value) {
            addCriterion("sum_by_column_index like", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexNotLike(String value) {
            addCriterion("sum_by_column_index not like", value, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexIn(List<String> values) {
            addCriterion("sum_by_column_index in", values, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexNotIn(List<String> values) {
            addCriterion("sum_by_column_index not in", values, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexBetween(String value1, String value2) {
            addCriterion("sum_by_column_index between", value1, value2, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumByColumnIndexNotBetween(String value1, String value2) {
            addCriterion("sum_by_column_index not between", value1, value2, "sumByColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexIsNull() {
            addCriterion("sum_column_index is null");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexIsNotNull() {
            addCriterion("sum_column_index is not null");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexEqualTo(String value) {
            addCriterion("sum_column_index =", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexNotEqualTo(String value) {
            addCriterion("sum_column_index <>", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexGreaterThan(String value) {
            addCriterion("sum_column_index >", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexGreaterThanOrEqualTo(String value) {
            addCriterion("sum_column_index >=", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexLessThan(String value) {
            addCriterion("sum_column_index <", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexLessThanOrEqualTo(String value) {
            addCriterion("sum_column_index <=", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexLike(String value) {
            addCriterion("sum_column_index like", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexNotLike(String value) {
            addCriterion("sum_column_index not like", value, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexIn(List<String> values) {
            addCriterion("sum_column_index in", values, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexNotIn(List<String> values) {
            addCriterion("sum_column_index not in", values, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexBetween(String value1, String value2) {
            addCriterion("sum_column_index between", value1, value2, "sumColumnIndex");
            return (Criteria) this;
        }

        public Criteria andSumColumnIndexNotBetween(String value1, String value2) {
            addCriterion("sum_column_index not between", value1, value2, "sumColumnIndex");
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