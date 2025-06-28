package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.executor.XxlJobExecutor;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.CronJob;

/**
 * @author zjy XxlJob定时任务执行能力
 */
public class XxlJobTaskAbility {

	private static Logger logger = LoggerFactory.getLogger(XxlJobTaskAbility.class);

	// XxlJobTaskAbility
	private static XxlJobTaskAbility instance = null;
	// CronJob的缓存对象，第一层Key是systemId，第二层Key是CronJobId
	private Map<String, Map<String, CronJob>> cronJobMap = new HashMap<>();
	
	// 私有化构造函数
	private XxlJobTaskAbility() {

	}

	public static XxlJobTaskAbility getInstance() {
		return instance;
	}

	/**
	 * XxlJobTaskAbility
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache QuartzTask resource to Map...");
		}
		// 初始化KafkaMessageSendAbility实例
		XxlJobTaskAbility repository = new XxlJobTaskAbility();
		
		// 加载CronJob数据
		List<CronJob> cronJobs = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			cronJobs = initService.listCronJobs(CronJob.JOB_TYPE_XXLJOB);
		} else {// 加载指定业务系统的数据
			cronJobs = initService.listCronJobs(systemId, CronJob.JOB_TYPE_XXLJOB);
		}

		if (!CollectionUtils.isEmpty(cronJobs)) {
			// 循环处理每条cronJob数据
			for (CronJob cronJob : cronJobs) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(cronJob.getSystemId())) {
					continue;
				}
				// 将cronJob对象加入缓存
				Map<String, CronJob> cronJobMap = repository.cronJobMap.get(cronJob.getSystemId());
				if (cronJobMap == null) {
					cronJobMap = new HashMap<>();
					repository.cronJobMap.put(cronJob.getSystemId(), cronJobMap);
				}
				cronJobMap.put(cronJob.getId(), cronJob);
				
				//创建低代码XxlJob任务执行类
				LowCodeXxlJob job = new LowCodeXxlJob();
				job.setCronJob(cronJob);
				//注册jobHandler，名称为systemId.code
				XxlJobExecutor.registJobHandler(cronJob.getSystemId()+"."+cronJob.getCode(), job);
			}
		} else {
			logger.info("no valid xxl cron job.");
		}

		
		// 初始化完成，赋值给属性instance
		instance = repository;
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * KafkaMessageConsumeAbility类的缓存重新加载方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache CronJob resource to Map...");
		}
		if(StringUtils.isBlank(systemId)) {
            logger.error("systemId is null!");
            return;
        }
		Map<String, CronJob> jobMap = instance.cronJobMap.get(systemId);
		if(!MapUtils.isEmpty(jobMap)) {
			// for(CronJob cronJob : jobMap.values()) {
			// 	XxlJobExecutor.registJobHandler(cronJob.getSystemId()+"."+cronJob.getCode(), null);
			// }
			jobMap.clear();
		}
		
		// 加载CronJob数据
		List<CronJob> cronJobs = initService.listCronJobs(systemId, CronJob.JOB_TYPE_QUARTZ);;
		
		if (!CollectionUtils.isEmpty(cronJobs)) {
			// 循环处理每条cronJob数据
			for (CronJob cronJob : cronJobs) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(cronJob.getSystemId())) {
					continue;
				}
				// 将cronJob对象加入缓存
				jobMap.put(cronJob.getId(), cronJob);
				//创建低代码XxlJob任务执行类
				LowCodeXxlJob job = new LowCodeXxlJob();
				job.setCronJob(cronJob);
				//注册jobHandler，名称为systemId.code
				XxlJobExecutor.registJobHandler(SpringContextUtils.getBean(FrontController.class).getBusinessSystem(cronJob.getSystemId())+"."+cronJob.getCode(), job);
			}
		} else {
			logger.info("no valid quartz cron job.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
}