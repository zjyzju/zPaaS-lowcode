package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ComboFuncDefine;
import cn.zpaas.lowcode.domain.eo.ComboFuncDefineExample;

public interface ComboFuncDefineMapper {
    long countByExample(ComboFuncDefineExample example);

    int deleteByExample(ComboFuncDefineExample example);

    int deleteByPrimaryKey(String id);

    int insert(ComboFuncDefine record);

    int insertSelective(ComboFuncDefine record);

    List<ComboFuncDefine> selectByExample(ComboFuncDefineExample example);

    ComboFuncDefine selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ComboFuncDefine record, @Param("example") ComboFuncDefineExample example);

    int updateByExample(@Param("record") ComboFuncDefine record, @Param("example") ComboFuncDefineExample example);

    int updateByPrimaryKeySelective(ComboFuncDefine record);

    int updateByPrimaryKey(ComboFuncDefine record);
}