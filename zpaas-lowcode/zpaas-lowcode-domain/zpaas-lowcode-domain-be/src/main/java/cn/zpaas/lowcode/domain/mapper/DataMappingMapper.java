package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DataMappingExample;

public interface DataMappingMapper {
    long countByExample(DataMappingExample example);

    int deleteByExample(DataMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataMapping record);

    int insertSelective(DataMapping record);

    List<DataMapping> selectByExample(DataMappingExample example);

    DataMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataMapping record, @Param("example") DataMappingExample example);

    int updateByExample(@Param("record") DataMapping record, @Param("example") DataMappingExample example);

    int updateByPrimaryKeySelective(DataMapping record);

    int updateByPrimaryKey(DataMapping record);
}