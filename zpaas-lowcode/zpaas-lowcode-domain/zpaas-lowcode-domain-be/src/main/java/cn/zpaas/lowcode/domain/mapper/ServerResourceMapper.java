package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.ServerResourceExample;

public interface ServerResourceMapper {
    long countByExample(ServerResourceExample example);

    int deleteByExample(ServerResourceExample example);

    int deleteByPrimaryKey(String id);

    int insert(ServerResource record);

    int insertSelective(ServerResource record);

    List<ServerResource> selectByExample(ServerResourceExample example);

    ServerResource selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ServerResource record, @Param("example") ServerResourceExample example);

    int updateByExample(@Param("record") ServerResource record, @Param("example") ServerResourceExample example);

    int updateByPrimaryKeySelective(ServerResource record);

    int updateByPrimaryKey(ServerResource record);
}