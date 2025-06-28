package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssignExample;

public interface FuncObjectAssignMapper {
    long countByExample(FuncObjectAssignExample example);

    int deleteByExample(FuncObjectAssignExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncObjectAssign record);

    int insertSelective(FuncObjectAssign record);

    List<FuncObjectAssign> selectByExample(FuncObjectAssignExample example);

    FuncObjectAssign selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncObjectAssign record, @Param("example") FuncObjectAssignExample example);

    int updateByExample(@Param("record") FuncObjectAssign record, @Param("example") FuncObjectAssignExample example);

    int updateByPrimaryKeySelective(FuncObjectAssign record);

    int updateByPrimaryKey(FuncObjectAssign record);
}