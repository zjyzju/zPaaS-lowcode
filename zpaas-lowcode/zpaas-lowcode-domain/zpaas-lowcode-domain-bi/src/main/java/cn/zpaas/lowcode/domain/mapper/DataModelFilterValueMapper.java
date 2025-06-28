package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModelFilterValue;
import cn.zpaas.lowcode.domain.eo.DataModelFilterValueExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelFilterValueMapper {
    long countByExample(DataModelFilterValueExample example);

    int deleteByExample(DataModelFilterValueExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModelFilterValue record);

    int insertSelective(DataModelFilterValue record);

    List<DataModelFilterValue> selectByExample(DataModelFilterValueExample example);

    DataModelFilterValue selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModelFilterValue record, @Param("example") DataModelFilterValueExample example);

    int updateByExample(@Param("record") DataModelFilterValue record, @Param("example") DataModelFilterValueExample example);

    int updateByPrimaryKeySelective(DataModelFilterValue record);

    int updateByPrimaryKey(DataModelFilterValue record);
}