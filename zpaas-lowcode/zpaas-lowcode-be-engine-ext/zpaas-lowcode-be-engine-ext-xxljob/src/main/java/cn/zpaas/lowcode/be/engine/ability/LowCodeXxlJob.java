package cn.zpaas.lowcode.be.engine.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.CronJob;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月21日-15:00:23
 */
public class LowCodeXxlJob extends IJobHandler{
    public static final String JOB_CONTEXT_KEY = "jobContext"; //jobContext
    public static final String XXL_JOB_ID = "xxlJobId"; //xxlJobId
    public static final String XXL_JOB_PARAM = "xxlJobParam"; //xxlJobParam
    public static final String XXL_JOB_SHARDING_INDEX = "xxlJobShardingIndex"; //xxlJobShardingIndex
    public static final String XXL_JOB_SHARDING_TOTAL = "xxlJobShardingTotal"; //xxlJobShardingTotal

    public static final Logger logger = LoggerFactory.getLogger(LowCodeXxlJob.class);

    private CronJob cronJob;

    public CronJob getCronJob() {
        return cronJob;
    }

    public void setCronJob(CronJob cronJob) {
        this.cronJob = cronJob;
    }

    @Override
    public void execute() throws Exception {
        logger.info("xxl job {} starting ...", XxlJobHelper.getJobId());
        try {
            //创建低代码服务方法调用参数类
            ServiceCommand command = new ServiceCommand();
            command.setServiceId(cronJob.getJobHandlerService());//serviceId
            command.setOperationId(cronJob.getJobHandlerMethod());//operationId
            command.setSystemId(cronJob.getSystemId());//systemId
            command.setTenantId(cronJob.getTenantId());//tenantId
            JsonObject jobContext = null;
            //jobCfg作为方法参数jobContext的默认数据信息
            String jobCfg = cronJob.getJobCfg();
            if(!StringUtils.isBlank(jobCfg)) {
                jobContext = JsonUtils.toJsonObject(jobCfg);
            }else {
                jobContext = new JsonObject();
            }
            //额外往jobContext中设计定时任务编码和名称
            jobContext.addProperty(CronJob.JOB_CODE_KEY, cronJob.getCode());
            jobContext.addProperty(CronJob.JOB_NAME_KEY, cronJob.getName());
            jobContext.addProperty(XXL_JOB_ID, XxlJobHelper.getJobId());//xxljob id
            jobContext.addProperty(XXL_JOB_PARAM, XxlJobHelper.getJobParam());//xxljob上的任务配置信息
            jobContext.addProperty(XXL_JOB_SHARDING_INDEX, XxlJobHelper.getShardIndex());//分片id
            jobContext.addProperty(XXL_JOB_SHARDING_TOTAL, XxlJobHelper.getShardTotal());//分片总数
            JsonObject param = new JsonObject();
            param.add(JOB_CONTEXT_KEY, jobContext);
            command.setReqData(param);
            logger.info("xxl job {} invoke method {}.{}", XxlJobHelper.getJobId(), command.getServiceId(), command.getOperationId());
            //调用编排的服务
            ServiceProxy.getProxy().execute(command, null);
            logger.info("xxl job {} finished!",  XxlJobHelper.getJobId());
        } catch (Exception e) {
           logger.error("execute job method failed!", e);
           throw new EngineException(ResultStatus.INTERNAL_ERROR, "execute job method failed!", e.getMessage(), e);
        }
    }
}
