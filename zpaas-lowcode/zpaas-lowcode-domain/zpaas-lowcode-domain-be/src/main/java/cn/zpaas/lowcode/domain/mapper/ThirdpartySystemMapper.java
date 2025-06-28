package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ThirdpartySystem;
import cn.zpaas.lowcode.domain.eo.ThirdpartySystemExample;

public interface ThirdpartySystemMapper {
    long countByExample(ThirdpartySystemExample example);

    int deleteByExample(ThirdpartySystemExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdpartySystem record);

    int insertSelective(ThirdpartySystem record);

    List<ThirdpartySystem> selectByExample(ThirdpartySystemExample example);

    ThirdpartySystem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdpartySystem record, @Param("example") ThirdpartySystemExample example);

    int updateByExample(@Param("record") ThirdpartySystem record, @Param("example") ThirdpartySystemExample example);

    int updateByPrimaryKeySelective(ThirdpartySystem record);

    int updateByPrimaryKey(ThirdpartySystem record);
}