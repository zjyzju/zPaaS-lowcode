package cn.zpaas.lowcode.bi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.bi.proxy.DataSetProxy;
import cn.zpaas.lowcode.bi.service.BiInitService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月28日-14:53:46
 */
@RestController
@RequestMapping("/bi/api/")
public class BiController {
    public static Logger logger = LoggerFactory.getLogger(BiController.class);

    //从配置文件中读取初始的启动参数
	@Value("${init.param.systemId}")
	private String initParamSystemId;
	@Value("${init.param.tenantId}")
	private String initParamTenantId;

    @Autowired
    private BiInitService initService;

    @RequestMapping("cache/reload/{systemId}")
	public synchronized void reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method for system:{} start...", systemId);
		}
		if(StringUtils.isBlank(systemId)) {
			logger.error("systemId is null!");
			return;
		}

		SpringContextUtils.getBean(FrontController.class).authenticate(systemId, request, "/cache/reload/"+systemId);

		DataSetProxy.reloadCache(systemId, null, initService);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	@PostConstruct
	/**
	 * FdmController类的初始化方法，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 */
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}

		DataSetProxy.init(initParamSystemId, initParamTenantId, initService);
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}
}
