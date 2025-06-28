package cn.zpaas.lowcode.fe.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.eo.ComboFuncDefine;
import cn.zpaas.lowcode.domain.eo.ComboFuncTab;
import cn.zpaas.lowcode.fe.proxy.FuncDefineProxy;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;
import cn.zpaas.lowcode.fe.vo.ComboFuncTabVo;

/**
 * 组合功能领域服务
 *
 * @author zjy
 * createTime 2025年04月04日-15:50:58
 */
@Service
public class ComboFuncDefineService {
    @Autowired
	private ComboFuncDefine comboFuncDefineSV;

    @Autowired
    private ComboFuncTab comboFuncTabSV;

    public List<ComboFuncDefineVo> cacheComboFuncDefines(String systemId){
        //加载组合功能列表
        List<ComboFuncDefine> comboFuncDefines = null;
        if(StringUtils.isBlank(systemId)) {
            comboFuncDefines = comboFuncDefineSV.list();
        }else {
            comboFuncDefines = comboFuncDefineSV.list(systemId);
        }
        if(CollectionUtils.isEmpty(comboFuncDefines)) {
            return null;
        }

        //加载组合功能标签列表
        List<ComboFuncTab> comboFuncTabs = null;
        if(StringUtils.isBlank(systemId)) {
            comboFuncTabs = comboFuncTabSV.list();
        }else {
            comboFuncTabs = comboFuncTabSV.listBySystem(systemId);
        }
        List<ComboFuncTabVo> comboFuncTabVos = this.cloneComboFuncTabVos(comboFuncTabs);
        Map<String, List<ComboFuncTabVo>> comboFuncTabMap = null;
        if(!CollectionUtils.isEmpty(comboFuncTabVos)) {
            comboFuncTabMap = comboFuncTabVos.stream().collect(Collectors.groupingBy(ComboFuncTabVo::getComboFuncId));
        }else {
            comboFuncTabMap = new HashMap<>();
        }

        List<ComboFuncDefineVo> comboFuncDefineVos = new ArrayList<>();
        ComboFuncDefineVo comboFuncDefineVo = null;
        for(ComboFuncDefine comboFuncDefine : comboFuncDefines) {
            comboFuncDefineVo = this.cloneComboFuncDefine(comboFuncDefine);
            comboFuncDefineVo.setComboFuncTabs(comboFuncTabMap.get(comboFuncDefineVo.getId()));
            comboFuncDefineVos.add(comboFuncDefineVo);
        }
        return comboFuncDefineVos;
    }

    private ComboFuncDefineVo cloneComboFuncDefine(ComboFuncDefine comboFuncDefine) {
        if(comboFuncDefine == null) {
            return null;
        }
        ComboFuncDefineVo comboFuncDefineVo = new ComboFuncDefineVo();
        comboFuncDefineVo.setComboTemplateId(comboFuncDefine.getComboTemplateId());
        comboFuncDefineVo.setId(comboFuncDefine.getId());
        comboFuncDefineVo.setName(comboFuncDefine.getName());
        comboFuncDefineVo.setSystemId(comboFuncDefine.getSystemId());
        comboFuncDefineVo.setTenantId(comboFuncDefine.getTenantId());
        return comboFuncDefineVo;
    }

    private List<ComboFuncTabVo> cloneComboFuncTabVos(List<ComboFuncTab> comboFuncTabs) {
        if(CollectionUtils.isEmpty(comboFuncTabs)) {
            return null;
        }
        List<ComboFuncTabVo> comboFuncTabVos = new ArrayList<>();
        for(ComboFuncTab comboFuncTab : comboFuncTabs) {
            comboFuncTabVos.add(this.cloneComboFuncTab(comboFuncTab));
        }
        return comboFuncTabVos;
    }

    private ComboFuncTabVo cloneComboFuncTab(ComboFuncTab comboFuncTab) {
        if(comboFuncTab == null) {
            return null;
        }
        ComboFuncTabVo comboFuncTabVo = new ComboFuncTabVo();
        comboFuncTabVo.setComboFuncId(comboFuncTab.getComboFuncId());
        comboFuncTabVo.setDisplayOrder(comboFuncTab.getDisplayOrder());
        comboFuncTabVo.setId(comboFuncTab.getId());
        comboFuncTabVo.setName(comboFuncTab.getName());
        comboFuncTabVo.setTabFuncId(comboFuncTab.getTabFuncId());
        comboFuncTabVo.setTabParams(comboFuncTab.getTabParams());
        if(!StringUtils.isBlank(comboFuncTabVo.getTabFuncId())) {
            comboFuncTabVo.setFuncDefine(FuncDefineProxy.getInstance().getFuncDefineAndTpl(comboFuncTabVo.getTabFuncId()));
        }
        return comboFuncTabVo;
    }
}
