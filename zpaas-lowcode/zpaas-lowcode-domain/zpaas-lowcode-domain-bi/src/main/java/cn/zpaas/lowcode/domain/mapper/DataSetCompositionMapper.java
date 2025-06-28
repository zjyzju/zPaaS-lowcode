package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataSetComposition;
import cn.zpaas.lowcode.domain.eo.DataSetCompositionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSetCompositionMapper {
    long countByExample(DataSetCompositionExample example);

    int deleteByExample(DataSetCompositionExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataSetComposition record);

    int insertSelective(DataSetComposition record);

    List<DataSetComposition> selectByExample(DataSetCompositionExample example);

    DataSetComposition selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataSetComposition record, @Param("example") DataSetCompositionExample example);

    int updateByExample(@Param("record") DataSetComposition record, @Param("example") DataSetCompositionExample example);

    int updateByPrimaryKeySelective(DataSetComposition record);

    int updateByPrimaryKey(DataSetComposition record);
}