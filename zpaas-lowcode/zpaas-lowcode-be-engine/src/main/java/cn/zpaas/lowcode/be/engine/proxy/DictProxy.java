package cn.zpaas.lowcode.be.engine.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.Dict;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.vo.LabelValueVo;

/**
 * 字典缓存管理类
 *
 * @author zjy
 * createTime 2025年4月21日-10:47:24
 */
public class DictProxy {
    private static Logger logger = LoggerFactory.getLogger(DictProxy.class);

    public static final String MANAGED_OBJECT_TYPE = "MANAGED_OBJECT_TYPE";//管理对象类型的code
    public static final String MANAGED_OBJECT_SUB_TYPE = "MANAGED_OBJECT_SUB_TYPE";//管理对象子类型的code

    public static final String CUSTOMIZED_DESIGN_COMP_CLASS = "CUSTOMIZED_DESIGN_COMP_CLASS";//自定义页面设计控件分类
    public static final String CUSTOMIZED_DESIGN_COMPONENT = "CUSTOMIZED_DESIGN_COMPONENT";//自定义页面设计控件

    public static final String CODE_KEY = "code";//code
    public static final String NAME_KEY = "name";//name
    public static final String TYPE_KEY = "type";//type
    public static final String SUB_COMPONENTS_KEY = "subComponents";//subComponents
    public static final String KEY_DATA_ATTRS_KEY = "keyDataAttrs";//keyDataAttrs
    public static final String PATHS_KEY = "paths";//paths
    public static final String CHART_COMPONENTS = "chartComponents";//chartComponents
	
	//用来缓存Dict对象，Key是dictId
	private static Map<String, Dict> dictMap = null;
    //用来缓存Dict对象，Key是dictCode
	private static Map<String, List<Dict>> dictCodeMap = null;
    //用来缓存Dict对象，第一层Key是dictCode，第二层Key是dictValue
	private static Map<String, Map<String, Dict>> dictCodeValueMap = null;
    //用来缓存Dict对象，Key是dictCode+parentDictCode+parentDictValue
	private static Map<String, List<Dict>> parentDictCodeValueMap = null;
    //用来缓存管理对象类型
    private static JsonArray managedObjectTypes = null;
    //用来缓存自定义页面控件
    private static JsonArray customizedDesignComponents = null;
    //用来缓存图表页面控件
    private static JsonArray chartDesignComponents = null;
	
	/**
	 * 用于初始化DictProxy，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		
		//获取Dict列表，状态为有效的记录
		List<Dict> dicts = initService.listDicts();
		
		//如果获取的列表为空，则直接返回
		if(CollectionUtils.isEmpty(dicts)) {
            dictMap = new HashMap<>();
            dictCodeMap = new HashMap<>();
            dictCodeValueMap = new HashMap<>();
            parentDictCodeValueMap = new HashMap<>();
			logger.info("no Dict is configured. return.");
			return;
		}
		
        dictMap = dicts.stream().collect(Collectors.toMap(Dict::getId, e->e));
        dictCodeMap = dicts.stream().collect(Collectors.groupingBy(Dict::getDictCode));
        dictCodeValueMap = dicts.stream().collect(Collectors.groupingBy(Dict::getDictCode, Collectors.toMap(Dict::getDictValue, e->e)));
        parentDictCodeValueMap = dicts.stream().collect(Collectors.groupingBy(e-> {return e.getDictCode() + "_" + e.getParentDictCode() + "_" + e.getParentDictValue();}));

        List<LabelValueVo> managedObjectTypesTmp = getLabelValues(MANAGED_OBJECT_TYPE);
        if(!CollectionUtils.isEmpty(managedObjectTypesTmp)) {
            managedObjectTypesTmp.forEach(e->  {
                e.setChildren(getLabelValuesByParent(MANAGED_OBJECT_SUB_TYPE, MANAGED_OBJECT_TYPE, e.getValue()));
            });
        }
        managedObjectTypes = JsonUtils.toJsonArray(managedObjectTypesTmp);

        List<Dict> componentClasses = getDicts(CUSTOMIZED_DESIGN_COMP_CLASS);
        if(!CollectionUtils.isEmpty(componentClasses)) {
            customizedDesignComponents = new JsonArray();
            chartDesignComponents = new JsonArray();
            componentClasses.forEach(e->  {
                JsonObject componentClass = new JsonObject();
                componentClass.addProperty(CODE_KEY, e.getDictValue());
                componentClass.addProperty(NAME_KEY, e.getDictValueLabel());
                componentClass.addProperty(TYPE_KEY, e.getDictValue2());
                List<Dict> components = getDictsByParent(CUSTOMIZED_DESIGN_COMPONENT, CUSTOMIZED_DESIGN_COMP_CLASS, e.getDictValue());
                if(!CollectionUtils.isEmpty(components)) {
                    JsonArray subComponents = new JsonArray();
                    components.forEach(sub->{
                        JsonObject subComponent = new JsonObject();
                        if(CHART_COMPONENTS.equals(e.getDictValue())) {
                            subComponent.addProperty(CODE_KEY, sub.getDictValue());
                            subComponent.addProperty(NAME_KEY, sub.getDictValueLabel());
                            subComponent.addProperty(TYPE_KEY, sub.getDictValue());
                            subComponent.add(KEY_DATA_ATTRS_KEY, JsonUtils.toJsonArray(sub.getDictValue2()));
                            subComponent.add(PATHS_KEY, JsonUtils.toJsonArray(sub.getDictValue3()));
                            chartDesignComponents.add(subComponent);
                        }else {
                            subComponent.addProperty(CODE_KEY, sub.getDictValue());
                            subComponent.addProperty(NAME_KEY, sub.getDictValueLabel());
                            subComponent.addProperty(TYPE_KEY, sub.getDictValue2());
                        }
                        subComponents.add(subComponent);
                    });
                    componentClass.add(SUB_COMPONENTS_KEY, subComponents);
                }
                customizedDesignComponents.add(componentClass);
            });
        }
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

    /**
	 * 用于初始化DictProxy，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...  reuse init method");
		}
		
		init(systemId, tenantId, initService);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	
	/**
	 * 根据DictId，从DictMap中获取Dict对象并返回
	 * @param dictId 
	 * @return Dict对象
	 */
	public static Dict getDict(String dictId) {
		return dictMap.get(dictId);
	}

    /**
     * 根据dictCode和dictValue，获取Dict
     * @param dictCode
     * @param dictValue
     * @return
     */
	public static Dict getDict(String dictCode, String dictValue) {
		Map<String, Dict> map = dictCodeValueMap.get(dictCode);
        if(!MapUtils.isEmpty(map)) {
            return map.get(dictValue);
        }
        return null;
	}

    /**
     * 根据dictCode和dictValue，获取dictName
     * @param dictCode
     * @param dictValue
     * @return
     */
	public static String getDictLabel(String dictCode, String dictValue) {
		Map<String, Dict> map = dictCodeValueMap.get(dictCode);
        if(!MapUtils.isEmpty(map)) {
            Dict dict = map.get(dictValue);
            if(dict != null) {
                return dict.getDictValueLabel();
            }
        }
        return null;
	}

    /**
     * 根据dictCode获取dict列表
     * @param dictCode
     * @return
     */
    public static List<Dict> getDicts(String dictCode) {
        return dictCodeMap.get(dictCode);
    }

    /**
     * 根据dictCode获取LabelValueVo列表
     * @param dictCode
     * @return
     */
    public static List<LabelValueVo> getLabelValues(String dictCode) {
        List<Dict> dicts = dictCodeMap.get(dictCode);
        if(!CollectionUtils.isEmpty(dicts)) {
            return dicts.stream().map(e-> {
                LabelValueVo labelValueVo = new LabelValueVo();
                labelValueVo.setLabel(e.getDictValueLabel());
                labelValueVo.setValue(e.getDictValue());
                labelValueVo.setParentValue(e.getParentDictValue());
                return labelValueVo;
            }).toList();
        }
        return null;
    }

    /**
     * 获取子字典列表
     * @param dictCode
     * @param parentDictCode
     * @param paretnDictValue
     * @return
     */
    public static List<Dict> getDictsByParent(String dictCode, String parentDictCode, String paretnDictValue) {
        return parentDictCodeValueMap.get(dictCode + "_" + parentDictCode + "_" + paretnDictValue);
    }

    /**
     * 获取子字典列表
     * @param dictCode
     * @param parentDictCode
     * @param paretnDictValue
     * @return
     */
    public static List<LabelValueVo> getLabelValuesByParent(String dictCode, String parentDictCode, String paretnDictValue) {
        List<Dict> dicts =  getDictsByParent(dictCode, parentDictCode, paretnDictValue);
        if(!CollectionUtils.isEmpty(dicts)) {
            return dicts.stream().map(e-> {
                LabelValueVo labelValueVo = new LabelValueVo();
                labelValueVo.setLabel(e.getDictValueLabel());
                labelValueVo.setValue(e.getDictValue());
                labelValueVo.setParentValue(e.getParentDictValue());
                return labelValueVo;
            }).toList();
        }
        return null;
    }

    /**
     * 获取管理对象类型-树形结构
     * @return
     */
    public static JsonArray getManagedObjectTypes() {
        return managedObjectTypes;
    }

    /**
     * 获取自定义设计组件
     * @return
     */
    public static JsonArray getCustomizedDesignComponents() {
        return customizedDesignComponents;
    }

    /**
     * 获取图表设计组件
     * @return
     */
    public static JsonArray getChartDesignComponents() {
        return chartDesignComponents;
    }
}
