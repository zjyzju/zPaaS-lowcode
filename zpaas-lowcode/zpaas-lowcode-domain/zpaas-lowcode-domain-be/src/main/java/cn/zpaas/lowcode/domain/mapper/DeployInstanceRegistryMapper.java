package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.DeployInstanceRegistry;
import cn.zpaas.lowcode.domain.eo.DeployInstanceRegistryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeployInstanceRegistryMapper {
    long countByExample(DeployInstanceRegistryExample example);

    int deleteByExample(DeployInstanceRegistryExample example);

    int deleteByPrimaryKey(String id);

    int insert(DeployInstanceRegistry record);

    int insertSelective(DeployInstanceRegistry record);

    List<DeployInstanceRegistry> selectByExample(DeployInstanceRegistryExample example);

    DeployInstanceRegistry selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") DeployInstanceRegistry record, @Param("example") DeployInstanceRegistryExample example);

    int updateByExample(@Param("record") DeployInstanceRegistry record, @Param("example") DeployInstanceRegistryExample example);

    int updateByPrimaryKeySelective(DeployInstanceRegistry record);

    int updateByPrimaryKey(DeployInstanceRegistry record);
}