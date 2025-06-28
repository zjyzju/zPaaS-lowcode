package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.CronJobMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class CronJob {
    public static final String JOB_TYPE_QUARTZ = "Q"; //quartz任务
    public static final String JOB_TYPE_XXLJOB = "X"; //xxljob任务

    public static final String JOB_HANDLER_SERVICE_KEY = "jobHandlerService"; //jobHandlerService
    public static final String JOB_HANDLER_METHOD_KEY = "jobHandlerMethod"; //jobHandlerMethod
    public static final String SYSTEM_ID_KEY = "systemId"; //systemId
    public static final String TENANT_ID_KEY = "tenantId"; //tenantId
    public static final String CRON_JOB_KEY = "cronJob"; //cronJob
    public static final String JOB_CFG_KEY = "jobCfg"; //jobCfg
    public static final String JOB_CODE_KEY = "code"; //code
    public static final String JOB_NAME_KEY = "name"; //name

    private String id;

    private String name;

    private String code;

    private String jobType;

    private String jobSchedule;

    private String jobHandlerService;

    private String jobHandlerMethod;

    private String jobCfg;

    private String description;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private CronJobMapper mapper;

    public CronJob byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int add(CronJob cronJob) {
        if(StringUtils.isBlank(cronJob.getId())) {
            cronJob.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        cronJob.setCreateTime(nowDate);
        cronJob.setUpdateTime(nowDate);
        return mapper.insert(cronJob);
    }

    public int save(CronJob cronJob) {
        cronJob.setUpdateTime(new Date());
        return mapper.updateByPrimaryKey(cronJob);
    }

    public int delete(String cronJobId) {
        return mapper.deleteByPrimaryKey(cronJobId);
    }

    public List<CronJob> list() {
        return mapper.selectByExample(null);
    }

    public List<CronJob> listBySystem(String systemId) {
        CronJobExample criteria = new CronJobExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExample(criteria);
    }

    public List<CronJob> list(String jobType) {
        CronJobExample criteria = new CronJobExample();
        criteria.createCriteria().andJobTypeEqualTo(jobType);
        return mapper.selectByExample(criteria);
    }

    public List<CronJob> listBySystem(String systemId, String jobType) {
        CronJobExample criteria = new CronJobExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andJobTypeEqualTo(jobType);
        return mapper.selectByExample(criteria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType == null ? null : jobType.trim();
    }

    public String getJobSchedule() {
        return jobSchedule;
    }

    public void setJobSchedule(String jobSchedule) {
        this.jobSchedule = jobSchedule == null ? null : jobSchedule.trim();
    }

    public String getJobHandlerService() {
        return jobHandlerService;
    }

    public void setJobHandlerService(String jobHandlerService) {
        this.jobHandlerService = jobHandlerService == null ? null : jobHandlerService.trim();
    }

    public String getJobHandlerMethod() {
        return jobHandlerMethod;
    }

    public void setJobHandlerMethod(String jobHandlerMethod) {
        this.jobHandlerMethod = jobHandlerMethod == null ? null : jobHandlerMethod.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getJobCfg() {
        return jobCfg;
    }

    public void setJobCfg(String jobCfg) {
        this.jobCfg = jobCfg;
    }
}