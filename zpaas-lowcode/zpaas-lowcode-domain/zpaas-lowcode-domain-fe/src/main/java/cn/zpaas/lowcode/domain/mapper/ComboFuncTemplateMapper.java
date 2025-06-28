package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ComboFuncTemplate;
import cn.zpaas.lowcode.domain.eo.ComboFuncTemplateExample;

public interface ComboFuncTemplateMapper {
    long countByExample(ComboFuncTemplateExample example);

    int deleteByExample(ComboFuncTemplateExample example);

    int deleteByPrimaryKey(String id);

    int insert(ComboFuncTemplate record);

    int insertSelective(ComboFuncTemplate record);

    List<ComboFuncTemplate> selectByExample(ComboFuncTemplateExample example);

    ComboFuncTemplate selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ComboFuncTemplate record, @Param("example") ComboFuncTemplateExample example);

    int updateByExample(@Param("record") ComboFuncTemplate record, @Param("example") ComboFuncTemplateExample example);

    int updateByPrimaryKeySelective(ComboFuncTemplate record);

    int updateByPrimaryKey(ComboFuncTemplate record);
}