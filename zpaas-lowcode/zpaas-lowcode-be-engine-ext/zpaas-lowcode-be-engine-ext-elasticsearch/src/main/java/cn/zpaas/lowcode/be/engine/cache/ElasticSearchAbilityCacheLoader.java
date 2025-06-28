package cn.zpaas.lowcode.be.engine.cache;

import org.springframework.stereotype.Component;

import cn.zpaas.lowcode.be.engine.ability.ElasticSearchRepositoryAbility;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;

/**
 * ES能力缓存加载类
 *
 * @author zjy
 * createTime 2025年04月13日-09:32:24
 */
@Component
public class ElasticSearchAbilityCacheLoader implements AbilityCacheLoader{

    @Override
    public void loadCache(String systemId, String tenantId, EngineInitService initService) {
        ElasticSearchRepositoryAbility.init(systemId, tenantId, initService);
    }

    @Override
    public void reloadCache(String systemId, String tenantId, EngineInitService initService) {
        ElasticSearchRepositoryAbility.reloadCache(systemId, tenantId, initService);
    }
    
}
