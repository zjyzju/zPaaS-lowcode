package cn.zpaas.lowcode.be.engine.cache;

import cn.zpaas.lowcode.be.engine.service.EngineInitService;

/**
 * 缓存加载接口类
 *
 * @author zjy
 * createTime 2025年04月13日-09:23:03
 */
public interface AbilityCacheLoader {
    public void loadCache(String systemId, String tenantId, EngineInitService initService);
    public void reloadCache(String systemId, String tenantId, EngineInitService initService);
}
