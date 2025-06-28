package cn.zpaas.lowcode.be.engine.cache;

import org.springframework.stereotype.Component;

import cn.zpaas.lowcode.be.engine.ability.KafkaMessageConsumeAbility;
import cn.zpaas.lowcode.be.engine.ability.KafkaMessageSendAbility;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;

/**
 * Kafka存取能力缓存加载类
 *
 * @author zjy
 * createTime 2025年04月13日-09:26:42
 */
@Component
public class KafkaAbilityCacheLoader implements AbilityCacheLoader {

    @Override
    public void loadCache(String systemId, String tenantId, EngineInitService initService) {
        KafkaMessageSendAbility.init(systemId, tenantId, initService);
        //初始化Kafka消息消费能力
		KafkaMessageConsumeAbility.init(systemId, tenantId, initService);
		
    }

    @Override
    public void reloadCache(String systemId, String tenantId, EngineInitService initService) {
        KafkaMessageSendAbility.reloadCache(systemId, tenantId, initService);
        KafkaMessageConsumeAbility.reloadCache(systemId, tenantId, initService);
    }
    
}
