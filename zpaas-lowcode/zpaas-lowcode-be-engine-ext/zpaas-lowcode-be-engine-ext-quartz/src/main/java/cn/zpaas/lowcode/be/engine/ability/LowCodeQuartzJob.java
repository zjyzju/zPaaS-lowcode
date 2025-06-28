package cn.zpaas.lowcode.be.engine.ability;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.domain.eo.CronJob;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月21日-15:00:23
 */
public class LowCodeQuartzJob implements Job{
    public static final String JOB_CONTEXT_KEY = "jobContext"; //jobContext

    public static final Logger logger = LoggerFactory.getLogger(LowCodeQuartzJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Quartz job {}:{} starting ...", jobExecutionContext.getJobDetail().getKey().getGroup(), jobExecutionContext.getJobDetail().getKey().getName());
        try {
            //获取任务数据Map
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            //获取定时任务配置信息
            JsonObject cronJobJson = JsonUtils.toJsonObject(jobDataMap.getString(CronJob.CRON_JOB_KEY));
            //初始化服务方法调用参数类
            ServiceCommand command = new ServiceCommand();
            command.setServiceId(JsonUtils.getString(cronJobJson, CronJob.JOB_HANDLER_SERVICE_KEY));//serviceId
            command.setOperationId(JsonUtils.getString(cronJobJson, CronJob.JOB_HANDLER_METHOD_KEY));//operationId
            command.setSystemId(JsonUtils.getString(cronJobJson, CronJob.SYSTEM_ID_KEY));//systemId
            command.setTenantId(JsonUtils.getString(cronJobJson, CronJob.TENANT_ID_KEY));//tenantId
            JsonObject jobContext = null;
            //jobCfg作为方法参数jobContext的默认数据信息
            String jobCfg = JsonUtils.getString(cronJobJson, CronJob.JOB_CFG_KEY);
            if(!StringUtils.isBlank(jobCfg)) {
                jobContext = JsonUtils.toJsonObject(jobCfg);
            }else {
                jobContext = new JsonObject();
            }
            //额外往jobContext中设计定时任务编码和名称
            jobContext.addProperty(CronJob.JOB_CODE_KEY, JsonUtils.getString(cronJobJson, CronJob.JOB_CODE_KEY));
            jobContext.addProperty(CronJob.JOB_NAME_KEY, JsonUtils.getString(cronJobJson, CronJob.JOB_NAME_KEY));
            JsonObject param = new JsonObject();
            param.add(JOB_CONTEXT_KEY, jobContext);
            command.setReqData(param);
            logger.info("Quartz job {}:{} invoke method {}.{}", jobExecutionContext.getJobDetail().getKey().getGroup(), jobExecutionContext.getJobDetail().getKey().getName(), command.getServiceId(), command.getOperationId());
            //调用编排的服务
            ServiceProxy.getProxy().execute(command, null);
            logger.info("Quartz job {}:{} finished!", jobExecutionContext.getJobDetail().getKey().getGroup(), jobExecutionContext.getJobDetail().getKey().getName());
        } catch (Exception e) {
           logger.error("execute job method failed!", e);
        }
    }
}
