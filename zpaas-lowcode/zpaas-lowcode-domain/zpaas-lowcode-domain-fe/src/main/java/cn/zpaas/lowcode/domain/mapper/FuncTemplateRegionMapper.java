package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncTemplateRegion;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionExample;

public interface FuncTemplateRegionMapper {
    long countByExample(FuncTemplateRegionExample example);

    int deleteByExample(FuncTemplateRegionExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncTemplateRegion record);

    int insertSelective(FuncTemplateRegion record);

    List<FuncTemplateRegion> selectByExample(FuncTemplateRegionExample example);

    FuncTemplateRegion selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncTemplateRegion record, @Param("example") FuncTemplateRegionExample example);

    int updateByExample(@Param("record") FuncTemplateRegion record, @Param("example") FuncTemplateRegionExample example);

    int updateByPrimaryKeySelective(FuncTemplateRegion record);

    int updateByPrimaryKey(FuncTemplateRegion record);
}