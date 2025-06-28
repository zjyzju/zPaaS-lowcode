package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionOperationExample;

public interface FuncTemplateRegionOperationMapper {
    long countByExample(FuncTemplateRegionOperationExample example);

    int deleteByExample(FuncTemplateRegionOperationExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncTemplateRegionOperation record);

    int insertSelective(FuncTemplateRegionOperation record);

    List<FuncTemplateRegionOperation> selectByExample(FuncTemplateRegionOperationExample example);

    FuncTemplateRegionOperation selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncTemplateRegionOperation record, @Param("example") FuncTemplateRegionOperationExample example);

    int updateByExample(@Param("record") FuncTemplateRegionOperation record, @Param("example") FuncTemplateRegionOperationExample example);

    int updateByPrimaryKeySelective(FuncTemplateRegionOperation record);

    int updateByPrimaryKey(FuncTemplateRegionOperation record);
}