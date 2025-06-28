package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModelFilter;
import cn.zpaas.lowcode.domain.eo.DataModelFilterExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelFilterMapper {
    long countByExample(DataModelFilterExample example);

    int deleteByExample(DataModelFilterExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModelFilter record);

    int insertSelective(DataModelFilter record);

    List<DataModelFilter> selectByExample(DataModelFilterExample example);

    DataModelFilter selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModelFilter record, @Param("example") DataModelFilterExample example);

    int updateByExample(@Param("record") DataModelFilter record, @Param("example") DataModelFilterExample example);

    int updateByPrimaryKeySelective(DataModelFilter record);

    int updateByPrimaryKey(DataModelFilter record);
}