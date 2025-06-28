package cn.zpaas.lowcode.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import cn.zpaas.lowcode.domain.eo.ValidateRuleType;
import cn.zpaas.lowcode.domain.eo.ValidateRuleTypeExample;

public interface ValidateRuleTypeMapper {
    long countByExample(ValidateRuleTypeExample example);

    int deleteByExample(ValidateRuleTypeExample example);

    int deleteByPrimaryKey(String id);

    int insert(ValidateRuleType record);

    int insertSelective(ValidateRuleType record);

    List<ValidateRuleType> selectByExample(ValidateRuleTypeExample example);

    ValidateRuleType selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ValidateRuleType record, @Param("example") ValidateRuleTypeExample example);

    int updateByExample(@Param("record") ValidateRuleType record, @Param("example") ValidateRuleTypeExample example);

    int updateByPrimaryKeySelective(ValidateRuleType record);

    int updateByPrimaryKey(ValidateRuleType record);
}