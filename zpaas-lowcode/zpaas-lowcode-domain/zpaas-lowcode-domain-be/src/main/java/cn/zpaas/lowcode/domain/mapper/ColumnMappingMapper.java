package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ColumnMapping;
import cn.zpaas.lowcode.domain.eo.ColumnMappingExample;

public interface ColumnMappingMapper {
    long countByExample(ColumnMappingExample example);

    int deleteByExample(ColumnMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(ColumnMapping record);

    int insertSelective(ColumnMapping record);

    List<ColumnMapping> selectByExample(ColumnMappingExample example);

    ColumnMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ColumnMapping record, @Param("example") ColumnMappingExample example);

    int updateByExample(@Param("record") ColumnMapping record, @Param("example") ColumnMappingExample example);

    int updateByPrimaryKeySelective(ColumnMapping record);

    int updateByPrimaryKey(ColumnMapping record);
}