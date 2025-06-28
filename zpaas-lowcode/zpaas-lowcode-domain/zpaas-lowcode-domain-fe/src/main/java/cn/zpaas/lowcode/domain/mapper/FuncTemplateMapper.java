package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncTemplateExample;

public interface FuncTemplateMapper {
    long countByExample(FuncTemplateExample example);

    int deleteByExample(FuncTemplateExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncTemplate record);

    int insertSelective(FuncTemplate record);

    List<FuncTemplate> selectByExample(FuncTemplateExample example);

    FuncTemplate selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncTemplate record, @Param("example") FuncTemplateExample example);

    int updateByExample(@Param("record") FuncTemplate record, @Param("example") FuncTemplateExample example);

    int updateByPrimaryKeySelective(FuncTemplate record);

    int updateByPrimaryKey(FuncTemplate record);
}