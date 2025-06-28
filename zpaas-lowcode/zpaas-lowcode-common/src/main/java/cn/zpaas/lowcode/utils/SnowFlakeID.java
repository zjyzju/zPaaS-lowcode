package cn.zpaas.lowcode.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import cn.zpaas.lowcode.exception.CommException;

/**
 * 雪花算法生成ID工具类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class SnowFlakeID {
    //项目开始时间
	private static final long BEGIN_TIME = 1694569534593L;	
	//服务器标识的位长
    private static final long MACHINE_ID_LENGTH = 7L;
    //数据中心标识的位长
    private static final long DATA_CENTER_ID_LENGTH = 3L;
    //服务器标识的最大值
    private static final long MAX_MACHINE_ID = -1L ^ (-1L << MACHINE_ID_LENGTH);
    //数据中心标识的最大值
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_LENGTH);
    //处理时间回拨的序列，防止重复
    private static final long TIME_BACKWARD_SEQUENCE_LENGTH = 2L;
    //序列号位长
    private static final long SEQUENCE_LENGTH = 10L;
    //服务器标识偏移
    private static final long MACHINE_ID_SHIFT = SEQUENCE_LENGTH + TIME_BACKWARD_SEQUENCE_LENGTH;
    //数据中心标识偏移
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_LENGTH + TIME_BACKWARD_SEQUENCE_LENGTH + MACHINE_ID_LENGTH;
    //时间戳左移动
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_LENGTH + TIME_BACKWARD_SEQUENCE_LENGTH + MACHINE_ID_LENGTH + DATA_CENTER_ID_LENGTH;
    //序列号掩码
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_LENGTH);
    //序列号掩码
    private static final long TIME_BACKWARD_SEQUENCE_MASK = -1L ^ (-1L << TIME_BACKWARD_SEQUENCE_LENGTH);
    
    //数据中心标识
    @Value("${snowFlake.dataCenterId:1}")
    private long dataCenterId;
    
    //服务器标识
    @Value("${snowFlake.machineId:1}")
    private long machineId;
    
    //相同时间戳，使用的序列
    private long sequence = 0L;
    
    //处理时间回拨的序列
    private long timeBackwardSequence = 0L;
    
    //记录使用过的最新时间
    private long lastestTimstampUsed = 0L;
    
    //记录上次生成时使用的时间戳
    private long lastTimestamp = -1L;
    
    /**
     * 处理非法配置参数的
     */
    @PostConstruct
    public void init() {
    	//当超过最大值或小于0时，按（绝对值%最大值）处理
        if (this.machineId > MAX_MACHINE_ID || this.machineId < 0) {
            this.machineId = Math.abs(this.machineId) % MAX_MACHINE_ID;
        }
      //当超过最大值或小于0时，按（绝对值%最大值）处理
        if (this.dataCenterId > MAX_DATA_CENTER_ID || this.dataCenterId < 0) {
        	 this.dataCenterId = Math.abs(this.dataCenterId) % MAX_DATA_CENTER_ID;
        }
    }

    /**
     * 生成下一个ID
     * @return SnowflakeId
     */
    public synchronized long nextId() {
    	//获取当前时间戳
        long timestamp = generateTimestamp();
        
        //当前时间小于上一次ID生成的时间戳，timeBackwardSequence加1
        if (timestamp < this.lastTimestamp) {
        	this.timeBackwardSequence = (this.timeBackwardSequence + 1) & TIME_BACKWARD_SEQUENCE_MASK;
        	//记录已经使用过的最新时间
        	if(this.lastTimestamp > this.lastestTimstampUsed) {
        		this.lastestTimstampUsed = this.lastTimestamp;
        	}
        	//在一次使用过的最新时间的过去，往前回拨时间（都没有超过使用过的最新时间）的次数超过上限
            if (this.timeBackwardSequence == 0) {
            	throw new CommException(String.format("time backword count is overflowed! max number is: %d ", TIME_BACKWARD_SEQUENCE_MASK));
            }
        }

        //当时间戳相同时，则进行毫秒内序列
        if (this.lastTimestamp == timestamp) {
            //如果毫秒相同，则从0递增生成序列号
        	this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (sequence == 0) {
            	throw new CommException(String.format("id generated for same moment is overflowed! max number is: %d ", SEQUENCE_MASK));
            }
        }else {//时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        //当时间回拨序列不为0且时间已经恢复正常时，重置时间回拨序列
        if(this.timeBackwardSequence > 0 && timestamp > this.lastestTimstampUsed) {
        	this.timeBackwardSequence = 0L;
        }
        //当使用过的最新时间不为0且时间已经恢复正常时，设置使用过的最新时间为0
        if(this.lastestTimstampUsed > 0 && timestamp > this.lastestTimstampUsed) {
        	this.lastestTimstampUsed = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - BEGIN_TIME) << TIMESTAMP_LEFT_SHIFT) | (this.dataCenterId << DATA_CENTER_ID_SHIFT) 
        		| (this.machineId << MACHINE_ID_SHIFT)| (this.timeBackwardSequence << SEQUENCE_LENGTH)  | sequence;
    }
 
    /**
     * 生成时间戳
     * @return
     */
    protected long generateTimestamp() {
        return System.currentTimeMillis();
    }
}
