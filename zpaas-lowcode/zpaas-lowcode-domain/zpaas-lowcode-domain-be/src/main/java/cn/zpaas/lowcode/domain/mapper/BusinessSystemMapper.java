package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.BusinessSystemExample;

public interface BusinessSystemMapper {
    long countByExample(BusinessSystemExample example);

    int deleteByExample(BusinessSystemExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessSystem record);

    int insertSelective(BusinessSystem record);

    List<BusinessSystem> selectByExample(BusinessSystemExample example);

    BusinessSystem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessSystem record, @Param("example") BusinessSystemExample example);

    int updateByExample(@Param("record") BusinessSystem record, @Param("example") BusinessSystemExample example);

    int updateByPrimaryKeySelective(BusinessSystem record);

    int updateByPrimaryKey(BusinessSystem record);
}