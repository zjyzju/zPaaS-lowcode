package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.BusinessDomainExample;

public interface BusinessDomainMapper {
    long countByExample(BusinessDomainExample example);

    int deleteByExample(BusinessDomainExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessDomain record);

    int insertSelective(BusinessDomain record);

    List<BusinessDomain> selectByExample(BusinessDomainExample example);

    BusinessDomain selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessDomain record, @Param("example") BusinessDomainExample example);

    int updateByExample(@Param("record") BusinessDomain record, @Param("example") BusinessDomainExample example);

    int updateByPrimaryKeySelective(BusinessDomain record);

    int updateByPrimaryKey(BusinessDomain record);
}