package cn.zpaas.lowcode.domain.mapper;

import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.MessageConsumerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MessageConsumerMapper {
    long countByExample(MessageConsumerExample example);

    int deleteByExample(MessageConsumerExample example);

    int deleteByPrimaryKey(String id);

    int insert(MessageConsumer record);

    int insertSelective(MessageConsumer record);

    List<MessageConsumer> selectByExample(MessageConsumerExample example);

    MessageConsumer selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MessageConsumer record, @Param("example") MessageConsumerExample example);

    int updateByExample(@Param("record") MessageConsumer record, @Param("example") MessageConsumerExample example);

    int updateByPrimaryKeySelective(MessageConsumer record);

    int updateByPrimaryKey(MessageConsumer record);
}