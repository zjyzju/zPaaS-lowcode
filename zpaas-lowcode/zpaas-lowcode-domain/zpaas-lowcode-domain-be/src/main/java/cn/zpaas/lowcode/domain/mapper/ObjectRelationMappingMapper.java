package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMappingExample;

public interface ObjectRelationMappingMapper {
    long countByExample(ObjectRelationMappingExample example);

    int deleteByExample(ObjectRelationMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(ObjectRelationMapping record);

    int insertSelective(ObjectRelationMapping record);

    List<ObjectRelationMapping> selectByExample(ObjectRelationMappingExample example);

    ObjectRelationMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ObjectRelationMapping record, @Param("example") ObjectRelationMappingExample example);

    int updateByExample(@Param("record") ObjectRelationMapping record, @Param("example") ObjectRelationMappingExample example);

    int updateByPrimaryKeySelective(ObjectRelationMapping record);

    int updateByPrimaryKey(ObjectRelationMapping record);
}