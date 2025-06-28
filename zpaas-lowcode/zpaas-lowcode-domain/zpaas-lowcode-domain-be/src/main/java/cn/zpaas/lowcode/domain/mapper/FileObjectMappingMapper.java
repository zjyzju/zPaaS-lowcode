package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.FileObjectMapping;
import cn.zpaas.lowcode.domain.eo.FileObjectMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FileObjectMappingMapper {
    long countByExample(FileObjectMappingExample example);

    int deleteByExample(FileObjectMappingExample example);

    int deleteByPrimaryKey(String id);

    int insert(FileObjectMapping record);

    int insertSelective(FileObjectMapping record);

    List<FileObjectMapping> selectByExample(FileObjectMappingExample example);

    FileObjectMapping selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FileObjectMapping record, @Param("example") FileObjectMappingExample example);

    int updateByExample(@Param("record") FileObjectMapping record, @Param("example") FileObjectMappingExample example);

    int updateByPrimaryKeySelective(FileObjectMapping record);

    int updateByPrimaryKey(FileObjectMapping record);
}