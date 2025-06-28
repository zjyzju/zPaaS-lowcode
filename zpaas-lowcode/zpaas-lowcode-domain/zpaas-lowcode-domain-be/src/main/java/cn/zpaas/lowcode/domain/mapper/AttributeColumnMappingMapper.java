package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.AttributeColumnMapping;
import cn.zpaas.lowcode.domain.eo.AttributeColumnMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AttributeColumnMappingMapper {
    long countByExample(AttributeColumnMappingExample example);

    int deleteByExample(AttributeColumnMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(AttributeColumnMapping record);

    int insertSelective(AttributeColumnMapping record);

    List<AttributeColumnMapping> selectByExample(AttributeColumnMappingExample example);

    AttributeColumnMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AttributeColumnMapping record, @Param("example") AttributeColumnMappingExample example);

    int updateByExample(@Param("record") AttributeColumnMapping record, @Param("example") AttributeColumnMappingExample example);

    int updateByPrimaryKeySelective(AttributeColumnMapping record);

    int updateByPrimaryKey(AttributeColumnMapping record);
}