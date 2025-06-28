package cn.zpaas.lowcode.fe.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.service.FeInitService;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;



/**
 * 组合功能定义代理类，用于初始化加载缓存功能定义相关的数据
 *
 * @author zjy
 * createTime 2025年04月30日-14:11:06
 */
public class ComboFuncDefineProxy {
    private static Logger logger = LoggerFactory.getLogger(ComboFuncDefineProxy.class);

    //是否已经完成实例的初始化，当初始化完成时设置，如果未初始化完成则拒绝服务
	private static boolean initialized = false;

    //静态属性，用于实现单例模式
    private static ComboFuncDefineProxy instance = null;
    
    //组合功能定义对象map
    private Map<String, ComboFuncDefineVo> comboFuncDefineMap = null;
    
    /**
	 * ComboFuncDefineProxy
	 * 传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, FeInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
        ComboFuncDefineProxy funcDefineProxy = new ComboFuncDefineProxy();

        //缓存组合功能定义信息
		List<ComboFuncDefineVo> comboFuncDefines = initService.cacheComboFuncDefines(systemId);
        if(!CollectionUtils.isEmpty(comboFuncDefines)) {
			funcDefineProxy.comboFuncDefineMap = comboFuncDefines.stream().collect(Collectors.toMap(ComboFuncDefineVo::getId, t->t));
		}else {
            funcDefineProxy.comboFuncDefineMap = new HashMap<>();
        }
		
		//初始化完成
        instance = funcDefineProxy;
		initialized = true;
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

    /**
	 * 重新加载缓存。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, FeInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
        if(StringUtils.isBlank(systemId)) {
            logger.error("systemId is null!");
            return;
        }
        
        //缓存组合功能定义信息
		List<ComboFuncDefineVo> comboFuncDefines = initService.cacheComboFuncDefines(systemId);
        HashSet<String> comboFuncDefineIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(comboFuncDefines)) {
            for(ComboFuncDefineVo vo : comboFuncDefines) {
                instance.comboFuncDefineMap.put(vo.getId(), vo);
                comboFuncDefineIdSet.add(vo.getId());
            }
		}
        //清理已经失效的缓存
        for(ComboFuncDefineVo vo : instance.comboFuncDefineMap.values()) {
            if(systemId.equals(vo.getSystemId()) && !comboFuncDefineIdSet.contains(vo.getId())) {//已经失效的数据
                instance.comboFuncDefineMap.remove(vo.getId());
            }
        }
        
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

    /**
     * 私有化构造函数
     */
    private ComboFuncDefineProxy() {

    }

    public static ComboFuncDefineProxy getInstance() {
        return instance;
    }

    /**
     * 从缓存中获取组合功能定义信息（全量）
     * @param comboFuncId
     * @return
     */
    public ComboFuncDefineVo getComboFuncDefine(String comboFuncId) throws EngineException{
        if(!initialized) {
            throw new EngineException(ResultStatus.BAD_REQUEST, "not initialized!");
        }
        if(StringUtils.isBlank(comboFuncId)) {
            return null;
        }
        return comboFuncDefineMap.get(comboFuncId);
    }
}
