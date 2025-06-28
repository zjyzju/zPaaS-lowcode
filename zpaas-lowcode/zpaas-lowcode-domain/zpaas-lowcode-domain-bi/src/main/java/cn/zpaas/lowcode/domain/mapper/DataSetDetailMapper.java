package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataSetDetail;
import cn.zpaas.lowcode.domain.eo.DataSetDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSetDetailMapper {
    long countByExample(DataSetDetailExample example);

    int deleteByExample(DataSetDetailExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataSetDetail record);

    int insertSelective(DataSetDetail record);

    List<DataSetDetail> selectByExample(DataSetDetailExample example);

    DataSetDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataSetDetail record, @Param("example") DataSetDetailExample example);

    int updateByExample(@Param("record") DataSetDetail record, @Param("example") DataSetDetailExample example);

    int updateByPrimaryKeySelective(DataSetDetail record);

    int updateByPrimaryKey(DataSetDetail record);
}