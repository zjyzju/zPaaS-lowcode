package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayout;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayoutExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FuncCustomizedLayoutMapper {
    long countByExample(FuncCustomizedLayoutExample example);

    int deleteByExample(FuncCustomizedLayoutExample example);

    int deleteByPrimaryKey(String id);

    int insert(FuncCustomizedLayout record);

    int insertSelective(FuncCustomizedLayout record);

    List<FuncCustomizedLayout> selectByExample(FuncCustomizedLayoutExample example);

    FuncCustomizedLayout selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FuncCustomizedLayout record, @Param("example") FuncCustomizedLayoutExample example);

    int updateByExample(@Param("record") FuncCustomizedLayout record, @Param("example") FuncCustomizedLayoutExample example);

    int updateByPrimaryKeySelective(FuncCustomizedLayout record);

    int updateByPrimaryKey(FuncCustomizedLayout record);
}