package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.UserBusinessSystem;
import cn.zpaas.lowcode.domain.eo.UserBusinessSystemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserBusinessSystemMapper {
    long countByExample(UserBusinessSystemExample example);

    int deleteByExample(UserBusinessSystemExample example);

    int deleteByPrimaryKey(String id);

    int insert(UserBusinessSystem record);

    int insertSelective(UserBusinessSystem record);

    List<UserBusinessSystem> selectByExample(UserBusinessSystemExample example);

    UserBusinessSystem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") UserBusinessSystem record, @Param("example") UserBusinessSystemExample example);

    int updateByExample(@Param("record") UserBusinessSystem record, @Param("example") UserBusinessSystemExample example);

    int updateByPrimaryKeySelective(UserBusinessSystem record);

    int updateByPrimaryKey(UserBusinessSystem record);
}