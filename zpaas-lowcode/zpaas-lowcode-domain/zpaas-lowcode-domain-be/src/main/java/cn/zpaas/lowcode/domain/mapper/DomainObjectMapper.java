package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.DomainObjectExample;

public interface DomainObjectMapper {
    long countByExample(DomainObjectExample example);

    int deleteByExample(DomainObjectExample example);

    int deleteByPrimaryKey(String id);

    int insert(DomainObject record);

    int insertSelective(DomainObject record);

    List<DomainObject> selectByExample(DomainObjectExample example);

    DomainObject selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DomainObject record, @Param("example") DomainObjectExample example);

    int updateByExample(@Param("record") DomainObject record, @Param("example") DomainObjectExample example);

    int updateByPrimaryKeySelective(DomainObject record);

    int updateByPrimaryKey(DomainObject record);
}