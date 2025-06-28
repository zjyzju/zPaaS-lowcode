package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataSetCompositionJoin;
import cn.zpaas.lowcode.domain.eo.DataSetCompositionJoinExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSetCompositionJoinMapper {
    long countByExample(DataSetCompositionJoinExample example);

    int deleteByExample(DataSetCompositionJoinExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataSetCompositionJoin record);

    int insertSelective(DataSetCompositionJoin record);

    List<DataSetCompositionJoin> selectByExample(DataSetCompositionJoinExample example);

    DataSetCompositionJoin selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataSetCompositionJoin record, @Param("example") DataSetCompositionJoinExample example);

    int updateByExample(@Param("record") DataSetCompositionJoin record, @Param("example") DataSetCompositionJoinExample example);

    int updateByPrimaryKeySelective(DataSetCompositionJoin record);

    int updateByPrimaryKey(DataSetCompositionJoin record);
}