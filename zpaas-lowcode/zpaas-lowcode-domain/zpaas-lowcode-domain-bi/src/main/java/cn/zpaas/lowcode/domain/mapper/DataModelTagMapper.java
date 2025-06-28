package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModelTag;
import cn.zpaas.lowcode.domain.eo.DataModelTagExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelTagMapper {
    long countByExample(DataModelTagExample example);

    int deleteByExample(DataModelTagExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModelTag record);

    int insertSelective(DataModelTag record);

    List<DataModelTag> selectByExample(DataModelTagExample example);

    DataModelTag selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModelTag record, @Param("example") DataModelTagExample example);

    int updateByExample(@Param("record") DataModelTag record, @Param("example") DataModelTagExample example);

    int updateByPrimaryKeySelective(DataModelTag record);

    int updateByPrimaryKey(DataModelTag record);
}