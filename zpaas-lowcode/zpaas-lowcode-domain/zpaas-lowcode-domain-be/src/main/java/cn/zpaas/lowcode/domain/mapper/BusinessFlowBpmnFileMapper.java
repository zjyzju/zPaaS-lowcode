package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.BusinessFlowBpmnFile;
import cn.zpaas.lowcode.domain.eo.BusinessFlowBpmnFileExample;

public interface BusinessFlowBpmnFileMapper {
    long countByExample(BusinessFlowBpmnFileExample example);

    int deleteByExample(BusinessFlowBpmnFileExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessFlowBpmnFile record);

    int insertSelective(BusinessFlowBpmnFile record);

    List<BusinessFlowBpmnFile> selectByExampleWithBLOBs(BusinessFlowBpmnFileExample example);

    List<BusinessFlowBpmnFile> selectByExample(BusinessFlowBpmnFileExample example);

    BusinessFlowBpmnFile selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessFlowBpmnFile record, @Param("example") BusinessFlowBpmnFileExample example);

    int updateByExampleWithBLOBs(@Param("record") BusinessFlowBpmnFile record, @Param("example") BusinessFlowBpmnFileExample example);

    int updateByExample(@Param("record") BusinessFlowBpmnFile record, @Param("example") BusinessFlowBpmnFileExample example);

    int updateByPrimaryKeySelective(BusinessFlowBpmnFile record);

    int updateByPrimaryKeyWithBLOBs(BusinessFlowBpmnFile record);

    int updateByPrimaryKey(BusinessFlowBpmnFile record);
}