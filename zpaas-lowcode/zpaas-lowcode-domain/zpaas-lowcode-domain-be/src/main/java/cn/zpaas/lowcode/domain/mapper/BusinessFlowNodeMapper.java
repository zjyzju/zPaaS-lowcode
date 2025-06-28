package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNodeExample;

public interface BusinessFlowNodeMapper {
    long countByExample(BusinessFlowNodeExample example);

    int deleteByExample(BusinessFlowNodeExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessFlowNode record);

    int insertSelective(BusinessFlowNode record);

    List<BusinessFlowNode> selectByExample(BusinessFlowNodeExample example);

    BusinessFlowNode selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessFlowNode record, @Param("example") BusinessFlowNodeExample example);

    int updateByExample(@Param("record") BusinessFlowNode record, @Param("example") BusinessFlowNodeExample example);

    int updateByPrimaryKeySelective(BusinessFlowNode record);

    int updateByPrimaryKey(BusinessFlowNode record);
}