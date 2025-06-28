package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.DynamicMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DynamicMappingMapper {
    long countByExample(DynamicMappingExample example);

    int deleteByExample(DynamicMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(DynamicMapping record);

    int insertSelective(DynamicMapping record);

    List<DynamicMapping> selectByExample(DynamicMappingExample example);

    DynamicMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DynamicMapping record, @Param("example") DynamicMappingExample example);

    int updateByExample(@Param("record") DynamicMapping record, @Param("example") DynamicMappingExample example);

    int updateByPrimaryKeySelective(DynamicMapping record);

    int updateByPrimaryKey(DynamicMapping record);
}