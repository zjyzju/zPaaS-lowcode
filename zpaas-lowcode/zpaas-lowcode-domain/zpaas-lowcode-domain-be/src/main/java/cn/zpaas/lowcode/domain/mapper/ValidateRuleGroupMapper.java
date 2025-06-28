package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroupExample;

public interface ValidateRuleGroupMapper {
    long countByExample(ValidateRuleGroupExample example);

    int deleteByExample(ValidateRuleGroupExample example);

    int deleteByPrimaryKey(String id);

    int insert(ValidateRuleGroup record);

    int insertSelective(ValidateRuleGroup record);

    List<ValidateRuleGroup> selectByExample(ValidateRuleGroupExample example);

    ValidateRuleGroup selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ValidateRuleGroup record, @Param("example") ValidateRuleGroupExample example);

    int updateByExample(@Param("record") ValidateRuleGroup record, @Param("example") ValidateRuleGroupExample example);

    int updateByPrimaryKeySelective(ValidateRuleGroup record);

    int updateByPrimaryKey(ValidateRuleGroup record);
}