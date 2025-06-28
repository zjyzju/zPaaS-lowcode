package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.FlowNodeExample;

public interface FlowNodeMapper {
    long countByExample(FlowNodeExample example);

    int deleteByExample(FlowNodeExample example);

    int deleteByPrimaryKey(String id);

    int insert(FlowNode record);

    int insertSelective(FlowNode record);

    List<FlowNode> selectByExample(FlowNodeExample example);

    FlowNode selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FlowNode record, @Param("example") FlowNodeExample example);

    int updateByExample(@Param("record") FlowNode record, @Param("example") FlowNodeExample example);

    int updateByPrimaryKeySelective(FlowNode record);

    int updateByPrimaryKey(FlowNode record);
}