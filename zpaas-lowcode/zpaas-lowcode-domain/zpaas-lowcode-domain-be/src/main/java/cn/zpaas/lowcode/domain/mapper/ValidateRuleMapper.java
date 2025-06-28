package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.domain.eo.ValidateRuleExample;

public interface ValidateRuleMapper {
    long countByExample(ValidateRuleExample example);

    int deleteByExample(ValidateRuleExample example);

    int deleteByPrimaryKey(String id);

    int insert(ValidateRule record);

    int insertSelective(ValidateRule record);

    List<ValidateRule> selectByExample(ValidateRuleExample example);

    ValidateRule selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ValidateRule record, @Param("example") ValidateRuleExample example);

    int updateByExample(@Param("record") ValidateRule record, @Param("example") ValidateRuleExample example);

    int updateByPrimaryKeySelective(ValidateRule record);

    int updateByPrimaryKey(ValidateRule record);
}