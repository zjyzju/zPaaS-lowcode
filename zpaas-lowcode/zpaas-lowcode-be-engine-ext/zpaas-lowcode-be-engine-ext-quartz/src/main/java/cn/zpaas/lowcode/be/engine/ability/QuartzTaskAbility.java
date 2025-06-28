package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.CronJob;

/**
 * @author zjy Quartz定时任务执行能力
 */
public class QuartzTaskAbility {

	private static Logger logger = LoggerFactory.getLogger(QuartzTaskAbility.class);

	// QuartzTaskAbility
	private static QuartzTaskAbility instance = null;
	//SchedulerFactory
	private SchedulerFactory schedulerFactory = null;
	// CronJob的缓存对象，第一层Key是systemId，第二层Key是CronJobId
	private Map<String, Map<String, CronJob>> cronJobMap = new HashMap<>();
	// Scheduler的缓存对象，第一层Key是systemId，第二层Key是CronJobId,用于停止原来已经启动的定时任务
	private Map<String, Map<String, Scheduler>> schedulerMap = new HashMap<>();
	
	// 私有化构造函数
	private QuartzTaskAbility() {

	}

	public static QuartzTaskAbility getInstance() {
		return instance;
	}

	/**
	 * QuartzTaskAbility
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
		QuartzTaskAbility repository = new QuartzTaskAbility();
		
		repository.schedulerFactory = new StdSchedulerFactory();

		// 加载CronJob数据
		List<CronJob> cronJobs = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			cronJobs = initService.listCronJobs(CronJob.JOB_TYPE_QUARTZ);
		} else {// 加载指定业务系统的数据
			cronJobs = initService.listCronJobs(systemId, CronJob.JOB_TYPE_QUARTZ);
		}

		if (!CollectionUtils.isEmpty(cronJobs)) {
			// 循环处理每条cronJob数据
			for (CronJob cronJob : cronJobs) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(cronJob.getSystemId())) {
					continue;
				}
				// 将cronJob对象加入缓存
				Map<String, CronJob> map = repository.cronJobMap.get(cronJob.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.cronJobMap.put(cronJob.getSystemId(), map);
				}
				map.put(cronJob.getId(), cronJob);
				try {
					//获取调度器
					Scheduler scheduler = repository.schedulerFactory.getScheduler();
					//创建任务信息，code+systemId作为唯一标识
					JobDetail jobDetail = JobBuilder.newJob(LowCodeQuartzJob.class)
						.usingJobData(CronJob.CRON_JOB_KEY, JsonUtils.toJson(cronJob))
						.withIdentity(cronJob.getCode(), cronJob.getSystemId()).build();
					//创建定时触发器，code+systemId作为唯一标识
					CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(cronJob.getCode(), cronJob.getSystemId())
						.startNow()//立即生效
						.withSchedule(CronScheduleBuilder.cronSchedule(cronJob.getJobSchedule()))
						.build();
					//设置任务调度，关联任务与定时触发器
					scheduler.scheduleJob(jobDetail, cronTrigger);
					logger.info("start quartz cron job {}:{}", cronJob.getCode(), cronJob.getSystemId());
					//启动任务
					scheduler.start();
					// 将scheduler对象加入缓存，缓存刷新时用来删除原定时任务
					Map<String, Scheduler> smap = repository.schedulerMap.get(cronJob.getSystemId());
					if (smap == null) {
						smap = new HashMap<>();
						repository.schedulerMap.put(cronJob.getSystemId(), smap);
					}
					smap.put(cronJob.getId(), scheduler);
				}catch(Exception ex) {
					logger.error("init quartz cron job failed {}:{}", cronJob.getCode(), cronJob.getSystemId(), ex);
				}
				
			}
		} else {
			logger.info("no valid quartz cron job.");
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
		Map<String, Scheduler> sMap = instance.schedulerMap.get(systemId);
		if(!MapUtils.isEmpty(jobMap)) {
			for(CronJob cronJob : jobMap.values()) {
				Scheduler scheduler = sMap.get(cronJob.getId());
				if(scheduler != null) {
					try {
						logger.info("delete quartz cron job {}:{}", cronJob.getCode(), cronJob.getSystemId());
						//删除原任务
						scheduler.deleteJob(new JobKey(cronJob.getCode(), cronJob.getSystemId()));
					} catch (SchedulerException e) {
						logger.error("delete quartz cron job failed {}:{}", cronJob.getCode(), cronJob.getSystemId(), e);
					}
				}
			}
			//清除原缓存数据
			jobMap.clear();
			sMap.clear();
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
				try {
					//获取定时调度器
					Scheduler scheduler = instance.schedulerFactory.getScheduler();
					//创建新任务，code+systemId作为唯一标识
					JobDetail jobDetail = JobBuilder.newJob(LowCodeQuartzJob.class)
						.usingJobData(CronJob.CRON_JOB_KEY, JsonUtils.toJson(cronJob))
						.withIdentity(cronJob.getCode(), cronJob.getSystemId()).build();
					//创建定时调度器，code+systemId作为唯一标识
					CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(cronJob.getCode(), cronJob.getSystemId())
						.startNow()//立即生效
						.withSchedule(CronScheduleBuilder.cronSchedule(cronJob.getJobSchedule()))
						.build();
					//设置任务调度，关联任务与定时触发器
					scheduler.scheduleJob(jobDetail, cronTrigger);
					logger.info("start quartz cron job {}:{}", cronJob.getCode(), cronJob.getSystemId());
					//启动定时任务
					scheduler.start();
					sMap.put(cronJob.getId(), scheduler);
				}catch(Exception ex) {
					logger.error("init quartz cron job failed", ex);
				}
			}
		} else {
			logger.info("no valid quartz cron job.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	
}

