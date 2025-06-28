package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.AttributeMapping;
import cn.zpaas.lowcode.domain.eo.AttributeMappingExample;

public interface AttributeMappingMapper {
    long countByExample(AttributeMappingExample example);

    int deleteByExample(AttributeMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(AttributeMapping record);

    int insertSelective(AttributeMapping record);

    List<AttributeMapping> selectByExample(AttributeMappingExample example);

    AttributeMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AttributeMapping record, @Param("example") AttributeMappingExample example);

    int updateByExample(@Param("record") AttributeMapping record, @Param("example") AttributeMappingExample example);

    int updateByPrimaryKeySelective(AttributeMapping record);

    int updateByPrimaryKey(AttributeMapping record);
}