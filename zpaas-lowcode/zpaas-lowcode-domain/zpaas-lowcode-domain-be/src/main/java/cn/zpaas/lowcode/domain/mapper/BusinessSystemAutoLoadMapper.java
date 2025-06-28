package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.BusinessSystemAutoLoad;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAutoLoadExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BusinessSystemAutoLoadMapper {
    long countByExample(BusinessSystemAutoLoadExample example);

    int deleteByExample(BusinessSystemAutoLoadExample example);

    int deleteByPrimaryKey(String id);

    int insert(BusinessSystemAutoLoad record);

    int insertSelective(BusinessSystemAutoLoad record);

    List<BusinessSystemAutoLoad> selectByExample(BusinessSystemAutoLoadExample example);

    BusinessSystemAutoLoad selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BusinessSystemAutoLoad record, @Param("example") BusinessSystemAutoLoadExample example);

    int updateByExample(@Param("record") BusinessSystemAutoLoad record, @Param("example") BusinessSystemAutoLoadExample example);

    int updateByPrimaryKeySelective(BusinessSystemAutoLoad record);

    int updateByPrimaryKey(BusinessSystemAutoLoad record);
}