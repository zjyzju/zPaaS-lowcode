package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.FuncCustomizedPage;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedPageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FuncCustomizedPageMapper {
    long countByExample(FuncCustomizedPageExample example);

    int deleteByExample(FuncCustomizedPageExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncCustomizedPage record);

    int insertSelective(FuncCustomizedPage record);

    List<FuncCustomizedPage> selectByExample(FuncCustomizedPageExample example);

    FuncCustomizedPage selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncCustomizedPage record, @Param("example") FuncCustomizedPageExample example);

    int updateByExample(@Param("record") FuncCustomizedPage record, @Param("example") FuncCustomizedPageExample example);

    int updateByPrimaryKeySelective(FuncCustomizedPage record);

    int updateByPrimaryKey(FuncCustomizedPage record);
}