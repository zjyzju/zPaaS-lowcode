package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DbSchemaExample;

public interface DbSchemaMapper {
    long countByExample(DbSchemaExample example);

    int deleteByExample(DbSchemaExample example);

    int deleteByPrimaryKey(String id);

    int insert(DbSchema record);

    int insertSelective(DbSchema record);

    List<DbSchema> selectByExample(DbSchemaExample example);

    DbSchema selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DbSchema record, @Param("example") DbSchemaExample example);

    int updateByExample(@Param("record") DbSchema record, @Param("example") DbSchemaExample example);

    int updateByPrimaryKeySelective(DbSchema record);

    int updateByPrimaryKey(DbSchema record);
}