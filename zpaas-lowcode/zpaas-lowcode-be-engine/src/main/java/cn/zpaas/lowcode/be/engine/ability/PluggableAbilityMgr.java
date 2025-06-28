package cn.zpaas.lowcode.be.engine.ability;

import java.util.Map;

import cn.zpaas.lowcode.be.engine.cache.AbilityCacheLoader;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;

/**
 * 可插拔能力管理类
 *
 * @author zjy
 * createTime 2025年04月13日-09:35:45
 */
public class PluggableAbilityMgr {
    // private static final Logger logger = LoggerFactory.getLogger(PluggableAbilityMgr.class);

    public static final String CFG_RESOURCE_ID_KEY = "resourceId"; //resourceId存放的Key
    /**
     * 启动时初始化
     * @param systemId
     * @param tenantId
     * @param initService
     */
    public static void init(String systemId, String tenantId, EngineInitService initService) {
        Map<String, AbilityCacheLoader> abilityCacheLoaderMap = SpringContextUtils.getBeansOfType(AbilityCacheLoader.class);
        if(!MapUtils.isEmpty(abilityCacheLoaderMap)) {
            abilityCacheLoaderMap.forEach((ability, abilityCacheLoader) -> {
                abilityCacheLoader.loadCache(systemId, tenantId, initService);
            });
        }
    }

    /**
     * 缓存刷新
     * @param systemId
     * @param tenantId
     * @param initService
     */
    public static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
        Map<String, AbilityCacheLoader> abilityCacheLoaderMap = SpringContextUtils.getBeansOfType(AbilityCacheLoader.class);
        if(!MapUtils.isEmpty(abilityCacheLoaderMap)) {
            abilityCacheLoaderMap.forEach((ability, abilityCacheLoader) -> {
                abilityCacheLoader.reloadCache(systemId, tenantId, initService);
            });
        }
    }
}
