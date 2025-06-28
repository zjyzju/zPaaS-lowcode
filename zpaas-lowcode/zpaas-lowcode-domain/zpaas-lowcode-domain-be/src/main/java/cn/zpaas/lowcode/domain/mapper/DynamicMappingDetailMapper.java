package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DynamicMappingDetail;
import cn.zpaas.lowcode.domain.eo.DynamicMappingDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DynamicMappingDetailMapper {
    long countByExample(DynamicMappingDetailExample example);

    int deleteByExample(DynamicMappingDetailExample example);

    int deleteByPrimaryKey(String id);

    int insert(DynamicMappingDetail record);

    int insertSelective(DynamicMappingDetail record);

    List<DynamicMappingDetail> selectByExample(DynamicMappingDetailExample example);

    DynamicMappingDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DynamicMappingDetail record, @Param("example") DynamicMappingDetailExample example);

    int updateByExample(@Param("record") DynamicMappingDetail record, @Param("example") DynamicMappingDetailExample example);

    int updateByPrimaryKeySelective(DynamicMappingDetail record);

    int updateByPrimaryKey(DynamicMappingDetail record);
}