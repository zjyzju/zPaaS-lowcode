package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperationExample;

public interface FuncRegionOperationMapper {
    long countByExample(FuncRegionOperationExample example);

    int deleteByExample(FuncRegionOperationExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncRegionOperation record);

    int insertSelective(FuncRegionOperation record);

    List<FuncRegionOperation> selectByExample(FuncRegionOperationExample example);

    FuncRegionOperation selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncRegionOperation record, @Param("example") FuncRegionOperationExample example);

    int updateByExample(@Param("record") FuncRegionOperation record, @Param("example") FuncRegionOperationExample example);

    int updateByPrimaryKeySelective(FuncRegionOperation record);

    int updateByPrimaryKey(FuncRegionOperation record);
}