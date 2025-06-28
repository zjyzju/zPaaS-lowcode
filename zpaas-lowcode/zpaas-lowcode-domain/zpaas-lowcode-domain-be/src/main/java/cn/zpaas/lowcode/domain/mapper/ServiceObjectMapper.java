package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.domain.eo.ServiceObjectExample;

public interface ServiceObjectMapper {
    long countByExample(ServiceObjectExample example);

    int deleteByExample(ServiceObjectExample example);

    int deleteByPrimaryKey(String id);

    int insert(ServiceObject record);

    int insertSelective(ServiceObject record);

    List<ServiceObject> selectByExample(ServiceObjectExample example);

    ServiceObject selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ServiceObject record, @Param("example") ServiceObjectExample example);

    int updateByExample(@Param("record") ServiceObject record, @Param("example") ServiceObjectExample example);

    int updateByPrimaryKeySelective(ServiceObject record);

    int updateByPrimaryKey(ServiceObject record);
}