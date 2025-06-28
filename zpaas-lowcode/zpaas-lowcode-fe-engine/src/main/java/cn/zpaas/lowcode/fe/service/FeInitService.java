package cn.zpaas.lowcode.fe.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.fe.domain.service.ComboFuncDefineService;
import cn.zpaas.lowcode.fe.domain.service.FuncDefineService;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;

/**
 * 前端领域初始化服务
 *
 * @author zjy
 * createTime 2025年04月30日-14:27:07
 */
@Service
public class FeInitService {
    
    @Autowired
    FuncDefineService funcDefineService;

    @Autowired
    ComboFuncDefineService comboFuncDefineService;

    /**
	 * 缓存功能定义信息
	 * @param systemId
	 */
    public List<FuncDefineVo> cacheFuncDefines(String systemId) {
        return this.funcDefineService.cacheFuncDefines(systemId);
    }

    /**
	 * 缓存绑定对象属性选项设置信息
	 * @param systemId
	 * @return
	 */
    public List<FuncObjectAttrOptions> cacheFuncObjectAttrOptions(String systemId){
        return this.funcDefineService.cacheFuncObjectAttrOptions(systemId);
    }

    /**
	 * 缓存功能定义信息
	 * @param systemId
	 */
    public List<FuncDefineVo> cacheFuncDefineAndTpl(String systemId) {
        return this.funcDefineService.cacheFuncDefineAndTpl(systemId);
    }

    /**
	 * 缓存组合功能定义信息
	 * @param systemId
	 */
    public List<ComboFuncDefineVo> cacheComboFuncDefines(String systemId) {
        return this.comboFuncDefineService.cacheComboFuncDefines(systemId);
    }

    /**
	 * 缓存方法信息
	 * @param systemId
	 */
    public List<OperationVo> cacheOperations(String systemId) {
        return this.funcDefineService.cacheOperations(systemId);
    }
}
