package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResourceExample;

public interface WorkflowProcessResourceMapper {
    long countByExample(WorkflowProcessResourceExample example);

    int deleteByExample(WorkflowProcessResourceExample example);

    int deleteByPrimaryKey(String id);

    int insert(WorkflowProcessResource record);

    int insertSelective(WorkflowProcessResource record);

    List<WorkflowProcessResource> selectByExampleWithBLOBs(WorkflowProcessResourceExample example);

    List<WorkflowProcessResource> selectByExample(WorkflowProcessResourceExample example);

    WorkflowProcessResource selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") WorkflowProcessResource record, @Param("example") WorkflowProcessResourceExample example);

    int updateByExampleWithBLOBs(@Param("record") WorkflowProcessResource record, @Param("example") WorkflowProcessResourceExample example);

    int updateByExample(@Param("record") WorkflowProcessResource record, @Param("example") WorkflowProcessResourceExample example);

    int updateByPrimaryKeySelective(WorkflowProcessResource record);

    int updateByPrimaryKeyWithBLOBs(WorkflowProcessResource record);

    int updateByPrimaryKey(WorkflowProcessResource record);
}