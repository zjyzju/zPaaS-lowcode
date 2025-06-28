package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.domain.eo.SqlManagementExample;

public interface SqlManagementMapper {
    long countByExample(SqlManagementExample example);

    int deleteByExample(SqlManagementExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlManagement record);

    int insertSelective(SqlManagement record);

    List<SqlManagement> selectByExample(SqlManagementExample example);

    SqlManagement selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlManagement record, @Param("example") SqlManagementExample example);

    int updateByExample(@Param("record") SqlManagement record, @Param("example") SqlManagementExample example);

    int updateByPrimaryKeySelective(SqlManagement record);

    int updateByPrimaryKey(SqlManagement record);
}