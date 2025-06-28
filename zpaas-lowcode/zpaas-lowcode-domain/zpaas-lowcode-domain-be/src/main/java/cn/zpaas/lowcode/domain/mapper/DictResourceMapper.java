package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DictResource;
import cn.zpaas.lowcode.domain.eo.DictResourceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DictResourceMapper {
    long countByExample(DictResourceExample example);

    int deleteByExample(DictResourceExample example);

    int deleteByPrimaryKey(String id);

    int insert(DictResource record);

    int insertSelective(DictResource record);

    List<DictResource> selectByExample(DictResourceExample example);

    DictResource selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DictResource record, @Param("example") DictResourceExample example);

    int updateByExample(@Param("record") DictResource record, @Param("example") DictResourceExample example);

    int updateByPrimaryKeySelective(DictResource record);

    int updateByPrimaryKey(DictResource record);
}