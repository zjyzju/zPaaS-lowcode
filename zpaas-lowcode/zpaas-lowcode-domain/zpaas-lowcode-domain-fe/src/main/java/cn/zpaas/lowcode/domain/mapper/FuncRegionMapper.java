package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncRegion;
import cn.zpaas.lowcode.domain.eo.FuncRegionExample;

public interface FuncRegionMapper {
    long countByExample(FuncRegionExample example);

    int deleteByExample(FuncRegionExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncRegion record);

    int insertSelective(FuncRegion record);

    List<FuncRegion> selectByExample(FuncRegionExample example);

    FuncRegion selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncRegion record, @Param("example") FuncRegionExample example);

    int updateByExample(@Param("record") FuncRegion record, @Param("example") FuncRegionExample example);

    int updateByPrimaryKeySelective(FuncRegion record);

    int updateByPrimaryKey(FuncRegion record);
}