package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuthExample;

public interface BusinessSystemAuthMapper {
    long countByExample(BusinessSystemAuthExample example);

    int deleteByExample(BusinessSystemAuthExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessSystemAuth record);

    int insertSelective(BusinessSystemAuth record);

    List<BusinessSystemAuth> selectByExample(BusinessSystemAuthExample example);

    BusinessSystemAuth selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessSystemAuth record, @Param("example") BusinessSystemAuthExample example);

    int updateByExample(@Param("record") BusinessSystemAuth record, @Param("example") BusinessSystemAuthExample example);

    int updateByPrimaryKeySelective(BusinessSystemAuth record);

    int updateByPrimaryKey(BusinessSystemAuth record);
}