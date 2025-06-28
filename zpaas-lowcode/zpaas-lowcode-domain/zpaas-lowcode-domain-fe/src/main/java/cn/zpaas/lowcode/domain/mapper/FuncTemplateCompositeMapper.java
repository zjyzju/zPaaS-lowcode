package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.FuncTemplateComposite;
import cn.zpaas.lowcode.domain.eo.FuncTemplateCompositeExample;

public interface FuncTemplateCompositeMapper {
    long countByExample(FuncTemplateCompositeExample example);

    int deleteByExample(FuncTemplateCompositeExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncTemplateComposite record);

    int insertSelective(FuncTemplateComposite record);

    List<FuncTemplateComposite> selectByExample(FuncTemplateCompositeExample example);

    FuncTemplateComposite selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncTemplateComposite record, @Param("example") FuncTemplateCompositeExample example);

    int updateByExample(@Param("record") FuncTemplateComposite record, @Param("example") FuncTemplateCompositeExample example);

    int updateByPrimaryKeySelective(FuncTemplateComposite record);

    int updateByPrimaryKey(FuncTemplateComposite record);
}