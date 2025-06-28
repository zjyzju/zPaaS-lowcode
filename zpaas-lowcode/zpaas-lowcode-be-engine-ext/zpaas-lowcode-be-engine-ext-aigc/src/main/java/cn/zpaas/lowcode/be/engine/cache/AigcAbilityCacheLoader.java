package cn.zpaas.lowcode.be.engine.cache;

import org.springframework.stereotype.Component;

import cn.zpaas.lowcode.be.engine.ability.AigcAbility;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;

/**
 * Aigc能力缓存加载类
 *
 * @author zjy
 * createTime 2025年04月13日-09:29:25
 */
@Component
public class AigcAbilityCacheLoader implements AbilityCacheLoader{

    @Override
    public void loadCache(String systemId, String tenantId, EngineInitService initService) {
        AigcAbility.init(systemId, tenantId, initService);
    }

    @Override
    public void reloadCache(String systemId, String tenantId, EngineInitService initService) {
        AigcAbility.reloadCache(systemId, tenantId, initService);
    }
    
}
