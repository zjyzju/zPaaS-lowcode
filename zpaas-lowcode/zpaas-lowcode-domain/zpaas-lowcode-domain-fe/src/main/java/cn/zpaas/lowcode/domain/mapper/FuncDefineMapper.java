package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncDefineExample;

public interface FuncDefineMapper {
    long countByExample(FuncDefineExample example);

    int deleteByExample(FuncDefineExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncDefine record);

    int insertSelective(FuncDefine record);

    List<FuncDefine> selectByExample(FuncDefineExample example);

    FuncDefine selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncDefine record, @Param("example") FuncDefineExample example);

    int updateByExample(@Param("record") FuncDefine record, @Param("example") FuncDefineExample example);

    int updateByPrimaryKeySelective(FuncDefine record);

    int updateByPrimaryKey(FuncDefine record);
}