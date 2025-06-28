package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssignExample;

public interface FuncRegionAttrAssignMapper {
    long countByExample(FuncRegionAttrAssignExample example);

    int deleteByExample(FuncRegionAttrAssignExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncRegionAttrAssign record);

    int insertSelective(FuncRegionAttrAssign record);

    List<FuncRegionAttrAssign> selectByExample(FuncRegionAttrAssignExample example);

    FuncRegionAttrAssign selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncRegionAttrAssign record, @Param("example") FuncRegionAttrAssignExample example);

    int updateByExample(@Param("record") FuncRegionAttrAssign record, @Param("example") FuncRegionAttrAssignExample example);

    int updateByPrimaryKeySelective(FuncRegionAttrAssign record);

    int updateByPrimaryKey(FuncRegionAttrAssign record);
}