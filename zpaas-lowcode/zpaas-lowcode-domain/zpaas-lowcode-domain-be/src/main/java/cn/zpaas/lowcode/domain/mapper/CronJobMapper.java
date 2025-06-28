package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.CronJob;
import cn.zpaas.lowcode.domain.eo.CronJobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CronJobMapper {
    long countByExample(CronJobExample example);

    int deleteByExample(CronJobExample example);

    int deleteByPrimaryKey(String id);

    int insert(CronJob record);

    int insertSelective(CronJob record);

    List<CronJob> selectByExample(CronJobExample example);

    CronJob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") CronJob record, @Param("example") CronJobExample example);

    int updateByExample(@Param("record") CronJob record, @Param("example") CronJobExample example);

    int updateByPrimaryKeySelective(CronJob record);

    int updateByPrimaryKey(CronJob record);
}