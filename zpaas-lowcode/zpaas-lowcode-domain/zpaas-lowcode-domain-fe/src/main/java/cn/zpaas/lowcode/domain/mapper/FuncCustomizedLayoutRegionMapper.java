package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayoutRegion;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayoutRegionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FuncCustomizedLayoutRegionMapper {
    long countByExample(FuncCustomizedLayoutRegionExample example);

    int deleteByExample(FuncCustomizedLayoutRegionExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncCustomizedLayoutRegion record);

    int insertSelective(FuncCustomizedLayoutRegion record);

    List<FuncCustomizedLayoutRegion> selectByExample(FuncCustomizedLayoutRegionExample example);

    FuncCustomizedLayoutRegion selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncCustomizedLayoutRegion record, @Param("example") FuncCustomizedLayoutRegionExample example);

    int updateByExample(@Param("record") FuncCustomizedLayoutRegion record, @Param("example") FuncCustomizedLayoutRegionExample example);

    int updateByPrimaryKeySelective(FuncCustomizedLayoutRegion record);

    int updateByPrimaryKey(FuncCustomizedLayoutRegion record);
}