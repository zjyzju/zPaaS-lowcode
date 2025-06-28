package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DataTransferMethod;
import cn.zpaas.lowcode.domain.eo.DataTransferMethodExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataTransferMethodMapper {
    long countByExample(DataTransferMethodExample example);

    int deleteByExample(DataTransferMethodExample example);

    int deleteByPrimaryKey(String id);

    int insert(DataTransferMethod record);

    int insertSelective(DataTransferMethod record);

    List<DataTransferMethod> selectByExample(DataTransferMethodExample example);

    DataTransferMethod selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DataTransferMethod record, @Param("example") DataTransferMethodExample example);

    int updateByExample(@Param("record") DataTransferMethod record, @Param("example") DataTransferMethodExample example);

    int updateByPrimaryKeySelective(DataTransferMethod record);

    int updateByPrimaryKey(DataTransferMethod record);
}