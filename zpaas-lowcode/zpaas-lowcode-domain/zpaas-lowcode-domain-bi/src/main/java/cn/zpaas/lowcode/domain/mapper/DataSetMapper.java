package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataSet;
import cn.zpaas.lowcode.domain.eo.DataSetExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSetMapper {
    long countByExample(DataSetExample example);

    int deleteByExample(DataSetExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataSet record);

    int insertSelective(DataSet record);

    List<DataSet> selectByExample(DataSetExample example);

    DataSet selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataSet record, @Param("example") DataSetExample example);

    int updateByExample(@Param("record") DataSet record, @Param("example") DataSetExample example);

    int updateByPrimaryKeySelective(DataSet record);

    int updateByPrimaryKey(DataSet record);
}