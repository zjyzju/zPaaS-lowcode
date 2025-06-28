package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowExample;

public interface BusinessFlowMapper {
    long countByExample(BusinessFlowExample example);

    int deleteByExample(BusinessFlowExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessFlow record);

    int insertSelective(BusinessFlow record);

    List<BusinessFlow> selectByExample(BusinessFlowExample example);

    BusinessFlow selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessFlow record, @Param("example") BusinessFlowExample example);

    int updateByExample(@Param("record") BusinessFlow record, @Param("example") BusinessFlowExample example);

    int updateByPrimaryKeySelective(BusinessFlow record);

    int updateByPrimaryKey(BusinessFlow record);
}