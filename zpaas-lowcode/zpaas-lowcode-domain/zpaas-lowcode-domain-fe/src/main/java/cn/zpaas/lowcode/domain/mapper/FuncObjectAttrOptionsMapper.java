package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptionsExample;

public interface FuncObjectAttrOptionsMapper {
    long countByExample(FuncObjectAttrOptionsExample example);

    int deleteByExample(FuncObjectAttrOptionsExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncObjectAttrOptions record);

    int insertSelective(FuncObjectAttrOptions record);

    List<FuncObjectAttrOptions> selectByExample(FuncObjectAttrOptionsExample example);

    FuncObjectAttrOptions selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncObjectAttrOptions record, @Param("example") FuncObjectAttrOptionsExample example);

    int updateByExample(@Param("record") FuncObjectAttrOptions record, @Param("example") FuncObjectAttrOptionsExample example);

    int updateByPrimaryKeySelective(FuncObjectAttrOptions record);

    int updateByPrimaryKey(FuncObjectAttrOptions record);
}