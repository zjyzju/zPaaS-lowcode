package cn.zpaas.lowcode.be.engine.cache;

import org.springframework.stereotype.Component;

import cn.zpaas.lowcode.be.engine.ability.WorkflowAbility;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;

/**
 * 工作流能力缓存加载类
 *
 * @author zjy
 * createTime 2025年04月13日-09:26:42
 */
@Component
public class WorkflowAbilityCacheLoader implements AbilityCacheLoader {

    @Override
    public void loadCache(String systemId, String tenantId, EngineInitService initService) {
        WorkflowAbility.init(systemId, tenantId, initService);
    }

    @Override
    public void reloadCache(String systemId, String tenantId, EngineInitService initService) {
        WorkflowAbility.reloadCache(systemId, tenantId, initService);
    }
    
}
