package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.DbTableExample;

public interface DbTableMapper {
    long countByExample(DbTableExample example);

    int deleteByExample(DbTableExample example);

    int deleteByPrimaryKey(String id);

    int insert(DbTable record);

    int insertSelective(DbTable record);

    List<DbTable> selectByExample(DbTableExample example);

    DbTable selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DbTable record, @Param("example") DbTableExample example);

    int updateByExample(@Param("record") DbTable record, @Param("example") DbTableExample example);

    int updateByPrimaryKeySelective(DbTable record);

    int updateByPrimaryKey(DbTable record);
}