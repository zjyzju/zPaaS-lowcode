package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResourceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SecurityKeyResourceMapper {
    long countByExample(SecurityKeyResourceExample example);

    int deleteByExample(SecurityKeyResourceExample example);

    int deleteByPrimaryKey(String id);

    int insert(SecurityKeyResource record);

    int insertSelective(SecurityKeyResource record);

    List<SecurityKeyResource> selectByExampleWithBLOBs(SecurityKeyResourceExample example);

    List<SecurityKeyResource> selectByExample(SecurityKeyResourceExample example);

    SecurityKeyResource selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SecurityKeyResource record, @Param("example") SecurityKeyResourceExample example);

    int updateByExampleWithBLOBs(@Param("record") SecurityKeyResource record, @Param("example") SecurityKeyResourceExample example);

    int updateByExample(@Param("record") SecurityKeyResource record, @Param("example") SecurityKeyResourceExample example);

    int updateByPrimaryKeySelective(SecurityKeyResource record);

    int updateByPrimaryKeyWithBLOBs(SecurityKeyResource record);

    int updateByPrimaryKey(SecurityKeyResource record);
}