package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.ValueObjectExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ValueObjectMapper {
    long countByExample(ValueObjectExample example);

    int deleteByExample(ValueObjectExample example);

    int deleteByPrimaryKey(String id);

    int insert(ValueObject record);

    int insertSelective(ValueObject record);

    List<ValueObject> selectByExample(ValueObjectExample example);

    ValueObject selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ValueObject record, @Param("example") ValueObjectExample example);

    int updateByExample(@Param("record") ValueObject record, @Param("example") ValueObjectExample example);

    int updateByPrimaryKeySelective(ValueObject record);

    int updateByPrimaryKey(ValueObject record);
}