package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ComboFuncTab;
import cn.zpaas.lowcode.domain.eo.ComboFuncTabExample;

public interface ComboFuncTabMapper {
    long countByExample(ComboFuncTabExample example);

    int deleteByExample(ComboFuncTabExample example);

    int deleteByPrimaryKey(String id);

    int insert(ComboFuncTab record);

    int insertSelective(ComboFuncTab record);

    List<ComboFuncTab> selectByExample(ComboFuncTabExample example);

    ComboFuncTab selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ComboFuncTab record, @Param("example") ComboFuncTabExample example);

    int updateByExample(@Param("record") ComboFuncTab record, @Param("example") ComboFuncTabExample example);

    int updateByPrimaryKeySelective(ComboFuncTab record);

    int updateByPrimaryKey(ComboFuncTab record);
}