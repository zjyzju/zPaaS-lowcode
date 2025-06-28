package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataModelMetrics;
import cn.zpaas.lowcode.domain.eo.DataModelMetricsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataModelMetricsMapper {
    long countByExample(DataModelMetricsExample example);

    int deleteByExample(DataModelMetricsExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataModelMetrics record);

    int insertSelective(DataModelMetrics record);

    List<DataModelMetrics> selectByExample(DataModelMetricsExample example);

    DataModelMetrics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataModelMetrics record, @Param("example") DataModelMetricsExample example);

    int updateByExample(@Param("record") DataModelMetrics record, @Param("example") DataModelMetricsExample example);

    int updateByPrimaryKeySelective(DataModelMetrics record);

    int updateByPrimaryKey(DataModelMetrics record);
}