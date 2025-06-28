package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.DbColumn;
import cn.zpaas.lowcode.domain.eo.DbColumnExample;

public interface DbColumnMapper {
    long countByExample(DbColumnExample example);

    int deleteByExample(DbColumnExample example);

    int deleteByPrimaryKey(String id);

    int insert(DbColumn record);

    int insertSelective(DbColumn record);

    List<DbColumn> selectByExample(DbColumnExample example);

    DbColumn selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DbColumn record, @Param("example") DbColumnExample example);

    int updateByExample(@Param("record") DbColumn record, @Param("example") DbColumnExample example);

    int updateByPrimaryKeySelective(DbColumn record);

    int updateByPrimaryKey(DbColumn record);
}