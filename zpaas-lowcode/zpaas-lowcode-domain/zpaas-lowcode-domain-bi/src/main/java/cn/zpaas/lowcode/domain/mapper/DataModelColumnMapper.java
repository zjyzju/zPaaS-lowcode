package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModelColumn;
import cn.zpaas.lowcode.domain.eo.DataModelColumnExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelColumnMapper {
    long countByExample(DataModelColumnExample example);

    int deleteByExample(DataModelColumnExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModelColumn record);

    int insertSelective(DataModelColumn record);

    List<DataModelColumn> selectByExample(DataModelColumnExample example);

    DataModelColumn selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModelColumn record, @Param("example") DataModelColumnExample example);

    int updateByExample(@Param("record") DataModelColumn record, @Param("example") DataModelColumnExample example);

    int updateByPrimaryKeySelective(DataModelColumn record);

    int updateByPrimaryKey(DataModelColumn record);
}