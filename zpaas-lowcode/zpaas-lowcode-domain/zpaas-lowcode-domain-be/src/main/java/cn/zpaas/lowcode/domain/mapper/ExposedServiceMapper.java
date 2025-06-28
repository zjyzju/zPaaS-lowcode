package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.ExposedServiceExample;

public interface ExposedServiceMapper {
    long countByExample(ExposedServiceExample example);

    int deleteByExample(ExposedServiceExample example);

    int deleteByPrimaryKey(String id);

    int insert(ExposedService record);

    int insertSelective(ExposedService record);

    List<ExposedService> selectByExample(ExposedServiceExample example);

    ExposedService selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ExposedService record, @Param("example") ExposedServiceExample example);

    int updateByExample(@Param("record") ExposedService record, @Param("example") ExposedServiceExample example);

    int updateByPrimaryKeySelective(ExposedService record);

    int updateByPrimaryKey(ExposedService record);
}