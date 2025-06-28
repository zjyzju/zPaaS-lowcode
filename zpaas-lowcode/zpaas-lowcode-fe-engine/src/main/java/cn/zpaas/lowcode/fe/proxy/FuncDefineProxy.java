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
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.service.FeInitService;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;



/**
 * 功能定义代理类，用于初始化加载缓存功能定义相关的数据
 *
 * @author zjy
 * createTime 2025年04月30日-14:11:06
 */
public class FuncDefineProxy {
    private static Logger logger = LoggerFactory.getLogger(FuncDefineProxy.class);

    //是否已经完成实例的初始化，当初始化完成时设置，如果未初始化完成则拒绝服务
	private static boolean initialized = false;

    //静态属性，用于实现单例模式
    private static FuncDefineProxy instance = null;
    
    //功能定义对象map，用于存放管理的功能定义对象，Key为功能标识
	private Map<String, FuncDefineVo> funcDefineMap = null;
    //功能定义+模板对象map
    private Map<String, FuncDefineVo> funcDefineAndTplMap = null;
    //方法缓存, key为operationId
    private Map<String, OperationVo> operationMap = null;
    //绑定对象选项信息缓存, key为optionsId
    private Map<String, FuncObjectAttrOptions> optionsMap = null;
    
    /**
	 * FuncDefineProxy
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
        FuncDefineProxy funcDefineProxy = new FuncDefineProxy();

        //缓存功能定义信息
		List<FuncDefineVo> funcDefines = initService.cacheFuncDefines(systemId);
        if(!CollectionUtils.isEmpty(funcDefines)) {
			funcDefineProxy.funcDefineMap = funcDefines.stream().collect(Collectors.toMap(FuncDefineVo::getId, t->t));
		}else {
            funcDefineProxy.funcDefineMap = new HashMap<>();
        }

        //缓存功能定义信息
		List<FuncDefineVo> funcDefineAndTpls = initService.cacheFuncDefineAndTpl(systemId);
        if(!CollectionUtils.isEmpty(funcDefineAndTpls)) {
			funcDefineProxy.funcDefineAndTplMap = funcDefineAndTpls.stream().collect(Collectors.toMap(FuncDefineVo::getId, t->t));
		}else {
            funcDefineProxy.funcDefineAndTplMap = new HashMap<>();
        }

        //缓存方法信息
        List<OperationVo> operationVos = initService.cacheOperations(systemId);
        if(!CollectionUtils.isEmpty(operationVos)) {
			funcDefineProxy.operationMap = operationVos.stream().collect(Collectors.toMap(OperationVo::getId, t->t));
		}else {
            funcDefineProxy.operationMap = new HashMap<>();
        }

        //加载绑定对象属性选项信息
		List<FuncObjectAttrOptions> funcObjectAttrOptionss = initService.cacheFuncObjectAttrOptions(systemId);
		if(!CollectionUtils.isEmpty(funcObjectAttrOptionss)) {
			funcDefineProxy.optionsMap = funcObjectAttrOptionss.stream().collect(Collectors.toMap(FuncObjectAttrOptions::getId, t->t));
		}else {
            funcDefineProxy.optionsMap = new HashMap<>();
        }
		

        //初始化完成
        instance = funcDefineProxy;
		initialized = true;
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

    /**
	 * 缓存重新加载
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

        //重新加载当前业务系统:缓存功能定义信息
		List<FuncDefineVo> funcDefines = initService.cacheFuncDefines(systemId);
        HashSet<String> funcDefineIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(funcDefines)) {
            for(FuncDefineVo vo : funcDefines) {
                instance.funcDefineMap.put(vo.getId(), vo);
                funcDefineIdSet.add(vo.getId());
            }
		}
        //清理已经失效的缓存
        for(FuncDefineVo vo : instance.funcDefineMap.values()) {
            if(systemId.equals(vo.getSystemId()) && !funcDefineIdSet.contains(vo.getId())) {//已经失效的数据
                instance.funcDefineMap.remove(vo.getId());
            }
        }


        //重新加载当前业务系统:缓存功能定义信息
		List<FuncDefineVo> funcDefineAndTpls = initService.cacheFuncDefineAndTpl(systemId);
        funcDefineIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(funcDefineAndTpls)) {
            for(FuncDefineVo vo : funcDefineAndTpls) {
                instance.funcDefineAndTplMap.put(vo.getId(), vo);
                funcDefineIdSet.add(vo.getId());
            }
		}
        //清理已经失效的缓存
        for(FuncDefineVo vo : instance.funcDefineAndTplMap.values()) {
            if(systemId.equals(vo.getSystemId()) && !funcDefineIdSet.contains(vo.getId())) {//已经失效的数据
                instance.funcDefineAndTplMap.remove(vo.getId());
            }
        }
        

        //缓存方法信息
        List<OperationVo> operationVos = initService.cacheOperations(systemId);
        // HashSet<String> operationIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(operationVos)) {
            for(OperationVo vo : operationVos) {
                instance.operationMap.put(vo.getId(), vo);
                // operationIdSet.add(vo.getId());
            }
		}
        //清理已经失效的缓存
        // for(OperationVo vo : instance.operationMap.values()) {
        //     if(systemId.equals(vo.getSystemId) && !operationIdSet.contains(vo.getId())) {//已经失效的数据
        //         instance.operationMap.remove(vo.getId());
        //     }
        // }

        //加载绑定对象属性选项信息
		List<FuncObjectAttrOptions> funcObjectAttrOptionss = initService.cacheFuncObjectAttrOptions(systemId);
        HashSet<String> optionsIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(funcObjectAttrOptionss)) {
            for(FuncObjectAttrOptions options : funcObjectAttrOptionss) {
                instance.optionsMap.put(options.getId(), options);
                optionsIdSet.add(options.getId());
            }
		}
        //清理已经失效的缓存
        for(FuncObjectAttrOptions options : instance.optionsMap.values()) {
            if(systemId.equals(options.getSystemId()) && !optionsIdSet.contains(options.getId())) {//已经失效的数据
                instance.optionsMap.remove(options.getId());
            }
        }
        
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

    /**
     * 私有化构造函数
     */
    private FuncDefineProxy() {

    }

    public static FuncDefineProxy getInstance() {
        return instance;
    }

    /**
     * 从缓存中获取功能定义信息（全量）
     * @param funcId
     * @return
     */
    public FuncDefineVo getFuncDefine(String funcId) throws EngineException{
        if(!initialized) {
            throw new EngineException(ResultStatus.BAD_REQUEST, "not initialized!");
        }
        if(StringUtils.isBlank(funcId)) {
            return null;
        }
        return funcDefineMap.get(funcId);
    }

    /**
     * 从缓存中获取功能定义信息+模板
     * @param funcId
     * @return
     */
    public FuncDefineVo getFuncDefineAndTpl(String funcId) {
        if(StringUtils.isBlank(funcId)) {
            return null;
        }
        return funcDefineAndTplMap.get(funcId);
    }

    /**
     * 从缓存中获取方法信息（全量）
     * @param operationId
     * @return
     */
    public OperationVo getOperation(String operationId) throws EngineException{
        if(!initialized) {
            throw new EngineException(ResultStatus.BAD_REQUEST, "not initialized!");
        }
        if(StringUtils.isBlank(operationId)) {
            return null;
        }
        return operationMap.get(operationId);
    }

    /**
     * 从缓存中获取绑定属性选项信息
     * @param optionsId
     * @return
     */
    public FuncObjectAttrOptions getAttrOptions(String optionsId){
        if(!initialized) {
            return null;
        }
        if(StringUtils.isBlank(optionsId)) {
            return null;
        }
        return optionsMap.get(optionsId);
    }
}
