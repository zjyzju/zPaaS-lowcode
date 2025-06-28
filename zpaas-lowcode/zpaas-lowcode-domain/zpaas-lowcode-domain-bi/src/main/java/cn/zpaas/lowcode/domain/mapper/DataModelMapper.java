package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModel;
import cn.zpaas.lowcode.domain.eo.DataModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelMapper {
    long countByExample(DataModelExample example);

    int deleteByExample(DataModelExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModel record);

    int insertSelective(DataModel record);

    List<DataModel> selectByExample(DataModelExample example);

    DataModel selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModel record, @Param("example") DataModelExample example);

    int updateByExample(@Param("record") DataModel record, @Param("example") DataModelExample example);

    int updateByPrimaryKeySelective(DataModel record);

    int updateByPrimaryKey(DataModel record);
}