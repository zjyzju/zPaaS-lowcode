package cn.zpaas.lowcode.be.engine.utils;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.commands.ProtocolCommand;

/**
 * Redis访问封装类
 *
 * @author zjy
 * createTime 2025年04月27日-17:06:23
 */
public class RedisClient {
    private static Logger logger = LoggerFactory.getLogger(RedisClient.class);
	
	private static final String SERVER_MODE_KEY = "serverMode";//服务模式：S：单机；M：主备（sentinel）；C：集群
	private static final String MASTER_NAME_KEY = "masterName";//主备sentinel模式时对应的masterName
	private static final String DATABASE_KEY = "database";//redis的数据库id
	private static final String CONNECT_TIMEOUT_KEY = "connectTimeout";// Jedis的connect timeout值
	private static final String SO_TIMEOUT_KEY = "soTimeout";// Jedis的so timeout值
	private static final String MAX_ATTEMPTS_KEY = "maxAttempts";// Jedis的maxAttempts值
	private static final String MAX_TOTAL_KEY = "maxTotal";// Jedis的maxTotal值
	private static final String MAX_IDLE_KEY = "maxIdle";// Jedis的maxIdle值
	private static final String MIN_IDLE_KEY = "minIdle";// Jedis的minIdle值
	private static final String MAX_WAIT_MILLIS_KEY = "maxWaitMillis";// Jedis的maxWaitMillis值
	private static final String TEST_ON_BORROW_KEY = "testOnBorrow";// Jedis的testOnBorrow值
	private static final String TEST_ON_RETURN_KEY = "testOnReturn";// Jedis的testOnReturn值
	private static final String TEST_WHILE_IDLE_KEY = "testWhileIdle";// Jedis的testWhileIdle值
	private static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS_KEY = "timeBetweenEvictionRunsMillis";//Jedis的timeBetweenEvictionRunsMillis值
	
	
	private static final String SERVER_MODE_SINGLETON = "S";//单机模式，包括非sentinel的主备模式
	private static final String SERVER_MODE_MASTERSLAVER = "M";//主备sentinel模式
	private static final String SERVER_MODE_CLUSTER = "C";//集群模式
	
	private static final String SEPARATE_COLON = ":";//冒号
	private static final String SEPARATE_SEMICOLON = ";";//分号
	
	private static final String REDIS_CLIENT_NAME = "lowcode_managed_client";//clientName
	private static final String REDIS_DEFAULT_USER = "default";//default user

    //redis 数据源访问信息
	private String username;
    private String password;
    private String url;
    private String serverCfg;
    private String serverMode = SERVER_MODE_SINGLETON;//默认值为单机模式
    
    private int database = 0;//redis的数据库id
    private int maxAttempts = 5;//maxAttempts 参数来定义失败情况下的重试次数，默认值为 5
    private int connectTimeout = 2000;//Jedis的connect timeout值，单位毫秒
    private int soTimeout = 2000;//Jedis的so timeout值，单位毫秒
    private String masterName = null;//主备sentinel模式时对应的masterName
    
    
    private JedisPool jedisPool = null;
    private JedisSentinelPool jedisSentinelPool = null;
    private JedisCluster jedisCluster = null;
    
    /**
     * 构造方法
     * @param url
     * @param password
     * @param serverCfg
     * @throws EngineException
     */
    public RedisClient(String url, String username, String password, String serverCfg) throws EngineException{
    	this.url = url;
    	this.username = username;
    	this.password = password;
    	this.serverCfg = serverCfg;
    	
    	if(this.username == null || this.username.trim().length() == 0) {
    		this.username = REDIS_DEFAULT_USER;
    	}
    	
    	JsonObject cfg = null;
        //当数据源配置中配置了版本号，则使用配置的版本号
        if(this.serverCfg != null && this.serverCfg.trim().length() > 0) {
        	cfg = JsonUtils.toJsonObject(this.serverCfg);
        	String mode = JsonUtils.getString(cfg, SERVER_MODE_KEY);//redis服务模式
        	if(mode != null && mode.trim().length() > 0) {
        		if(!SERVER_MODE_SINGLETON.equals(mode) && !SERVER_MODE_MASTERSLAVER.equals(mode) && !SERVER_MODE_CLUSTER.equals(mode)) {
        			logger.error("invalid redis serverMode {}", mode);
        			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid redis serverMode");
        		}else {
        			this.serverMode = mode;
        		}
        	}
        	if(cfg.get(MASTER_NAME_KEY) != null) {//获取timeout参数
        		this.masterName = JsonUtils.getString(cfg, MASTER_NAME_KEY);
        	}
        	if(cfg.get(CONNECT_TIMEOUT_KEY) != null) {//获取connectTimeout参数
        		this.connectTimeout = JsonUtils.getInteger(cfg, CONNECT_TIMEOUT_KEY);
        	}
        	if(cfg.get(SO_TIMEOUT_KEY) != null) {//获取soTimeout参数
        		this.soTimeout = JsonUtils.getInteger(cfg, SO_TIMEOUT_KEY);
        	}
        	if(cfg.get(DATABASE_KEY) != null) {//获取database参数
        		this.database = JsonUtils.getInteger(cfg, DATABASE_KEY);
        	}
        	if(cfg.get(MAX_ATTEMPTS_KEY) != null) {//获取maxAttempts参数
        		this.maxAttempts = JsonUtils.getInteger(cfg, MAX_ATTEMPTS_KEY);
        	}
        }
        
        if(this.url == null || this.url.trim().length() == 0) {
        	logger.error("server url can't bu null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "server url can't bu null!");
        }
        if(this.password != null && this.password.trim().length() == 0) {
        	this.password = null;
        }
        
        
        
        if(SERVER_MODE_CLUSTER.equals(this.serverMode)) {//集群模式
        	GenericObjectPoolConfig<Connection> jedisClusterPoolConfig = this.getJedisClusterPoolConfig(cfg);
        	String[] hostPorts = this.url.split(SEPARATE_SEMICOLON);
        	Set<HostAndPort> clusterNodeSet = new HashSet<>();
        	for(String hostPortString : hostPorts) {
        		int colonIndex = hostPortString.lastIndexOf(SEPARATE_COLON);
        		if(colonIndex < 0) {
            		logger.error("invalid server url!{}", this.url);
        			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid server url!");
            	}
        		clusterNodeSet.add(new HostAndPort(hostPortString.substring(0, colonIndex), Integer.valueOf(hostPortString.substring(colonIndex+1))));
        	}
        	jedisCluster = new JedisCluster(clusterNodeSet, connectTimeout, soTimeout, maxAttempts, this.username,this.password, REDIS_CLIENT_NAME, jedisClusterPoolConfig);
        }else if(SERVER_MODE_MASTERSLAVER.equals(this.serverMode)) {//主备sentinel模式
        	GenericObjectPoolConfig<Jedis> jedisSentinelPoolConfig = this.getJedisPoolConfig(cfg);
        	String[] hostPorts = this.url.split(SEPARATE_SEMICOLON);
        	Set<String> sentinelSet = new HashSet<>();
        	for(String hostPort : hostPorts) {
        		sentinelSet.add(hostPort);
        	}
        	jedisSentinelPool = new JedisSentinelPool(this.masterName, sentinelSet, jedisSentinelPoolConfig, this.connectTimeout, this.soTimeout, this.username, this.password, this.database, REDIS_CLIENT_NAME);
        }else {//单机模式
        	GenericObjectPoolConfig<Jedis> jedisPoolConfig = this.getJedisPoolConfig(cfg);
        	String[] hostPort = this.url.split(SEPARATE_SEMICOLON)[0].split(SEPARATE_COLON);
        	if(hostPort == null || hostPort.length != 2) {
        		logger.error("invalid server url!{}", this.url);
    			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid server url!");
        	}
        	jedisPool = new JedisPool(jedisPoolConfig, hostPort[0], Integer.valueOf(hostPort[1]), this.connectTimeout, this.soTimeout, this.username, this.password, this.database, REDIS_CLIENT_NAME);
        }
    }
    
   
    
    /**
     * 初始化JedisPoolConfig信息
     * @param serverCfg
     * @return
     */
    private GenericObjectPoolConfig<Jedis> getJedisPoolConfig(JsonObject serverCfg) {
    	GenericObjectPoolConfig<Jedis> jedisPoolConfig = new GenericObjectPoolConfig<>();
    	
    	if(serverCfg == null) {
    		return jedisPoolConfig;
    	}
    	if(serverCfg.get(MAX_TOTAL_KEY) != null) {
    		jedisPoolConfig.setMaxTotal(JsonUtils.getInteger(serverCfg, MAX_TOTAL_KEY));
    	}
    	if(serverCfg.get(MAX_IDLE_KEY) != null) {
    		jedisPoolConfig.setMaxIdle(JsonUtils.getInteger(serverCfg, MAX_IDLE_KEY));
    	}
    	if(serverCfg.get(MIN_IDLE_KEY) != null) {
    		jedisPoolConfig.setMinIdle(JsonUtils.getInteger(serverCfg, MIN_IDLE_KEY));
    	}
    	if(serverCfg.get(MAX_WAIT_MILLIS_KEY) != null) {
    		jedisPoolConfig.setMaxWait(Duration.ofMillis(JsonUtils.getLong(serverCfg, MAX_WAIT_MILLIS_KEY)));
    	}
    	if(serverCfg.get(TEST_ON_BORROW_KEY) != null) {
    		jedisPoolConfig.setTestOnBorrow(JsonUtils.getBoolean(serverCfg, TEST_ON_BORROW_KEY));
    	}
    	if(serverCfg.get(TEST_ON_RETURN_KEY) != null) {
    		jedisPoolConfig.setTestOnReturn(JsonUtils.getBoolean(serverCfg, TEST_ON_RETURN_KEY));
    	}
    	if(serverCfg.get(TIME_BETWEEN_EVICTION_RUNS_MILLIS_KEY) != null) {
    		jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(JsonUtils.getLong(serverCfg, TIME_BETWEEN_EVICTION_RUNS_MILLIS_KEY)));
    	}
    	if(serverCfg.get(TEST_WHILE_IDLE_KEY) != null) {
    		jedisPoolConfig.setTestWhileIdle(JsonUtils.getBoolean(serverCfg, TEST_WHILE_IDLE_KEY));
    	}
    	return jedisPoolConfig;
    }
    
    /**
     * 初始化JedisPoolConfig信息
     * @param serverCfg
     * @return
     */
    private GenericObjectPoolConfig<Connection> getJedisClusterPoolConfig(JsonObject serverCfg) {
    	GenericObjectPoolConfig<Connection> jedisPoolConfig = new GenericObjectPoolConfig<>();
    	
    	if(serverCfg == null) {
    		return jedisPoolConfig;
    	}
    	if(serverCfg.get(MAX_TOTAL_KEY) != null) {
    		jedisPoolConfig.setMaxTotal(JsonUtils.getInteger(serverCfg, MAX_TOTAL_KEY));
    	}
    	if(serverCfg.get(MAX_IDLE_KEY) != null) {
    		jedisPoolConfig.setMaxIdle(JsonUtils.getInteger(serverCfg, MAX_IDLE_KEY));
    	}
    	if(serverCfg.get(MIN_IDLE_KEY) != null) {
    		jedisPoolConfig.setMinIdle(JsonUtils.getInteger(serverCfg, MIN_IDLE_KEY));
    	}
    	if(serverCfg.get(MAX_WAIT_MILLIS_KEY) != null) {
    		jedisPoolConfig.setMaxWait(Duration.ofMillis(JsonUtils.getLong(serverCfg, MAX_WAIT_MILLIS_KEY)));
    	}
    	if(serverCfg.get(TEST_ON_BORROW_KEY) != null) {
    		jedisPoolConfig.setTestOnBorrow(JsonUtils.getBoolean(serverCfg, TEST_ON_BORROW_KEY));
    	}
    	if(serverCfg.get(TEST_ON_RETURN_KEY) != null) {
    		jedisPoolConfig.setTestOnReturn(JsonUtils.getBoolean(serverCfg, TEST_ON_RETURN_KEY));
    	}
    	if(serverCfg.get(TIME_BETWEEN_EVICTION_RUNS_MILLIS_KEY) != null) {
    		jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(JsonUtils.getLong(serverCfg, TIME_BETWEEN_EVICTION_RUNS_MILLIS_KEY)));
    	}
    	if(serverCfg.get(TEST_WHILE_IDLE_KEY) != null) {
    		jedisPoolConfig.setTestWhileIdle(JsonUtils.getBoolean(serverCfg, TEST_WHILE_IDLE_KEY));
    	}
    	return jedisPoolConfig;
    }
    
    /**
     * 释放资源
     */
    public void close() {
    	if(SERVER_MODE_CLUSTER.equals(this.serverMode)) {//集群模式
    		//do nothing
//        	if(jedisCluster != null) {
//        		jedisCluster.close();
//        	}
    	}else if(SERVER_MODE_MASTERSLAVER.equals(this.serverMode)) {//主备sentinel模式
        	if(jedisSentinelPool != null) {
        		jedisSentinelPool.close();
        	}
        }else {//单机模式
        	if(jedisPool != null) {
        		jedisPool.close();
        	}
        }
    }
    
    /**
     * 执行redis命令
     * @param command
     * @param param
     * @return
     */
    public Object sendCommand(String command, String... param) {
    	if(param == null || param.length == 0) {
    		logger.error("param is empty!");
    		return null;
    	}
    	if(SERVER_MODE_CLUSTER.equals(this.serverMode)) {//集群模式
        	return jedisCluster.sendCommand(param[0], new DefaultProtocolCommand(command), param);
        }else if(SERVER_MODE_MASTERSLAVER.equals(this.serverMode)) {//主备sentinel模式
        	Jedis jedis = jedisSentinelPool.getResource();
        	Object result = jedis.sendCommand(new DefaultProtocolCommand(command), param);
        	jedis.close();
        	return result;
        }else {//单机模式
        	Jedis jedis = jedisPool.getResource();
        	Object result = jedis.sendCommand(new DefaultProtocolCommand(command), param);
        	jedis.close();
        	return result;
        }
    }
    
    /**
     * 执行redis命令
     * @param command
     * @param param
     * @return
     */
    public Object sendCommand(String command, byte[]... param) {
    	if(param == null || param.length == 0) {
    		logger.error("param is empty!");
    		return null;
    	}
    	if(SERVER_MODE_CLUSTER.equals(this.serverMode)) {//集群模式
        	return jedisCluster.sendCommand(param[0], new DefaultProtocolCommand(command), param);
        }else if(SERVER_MODE_MASTERSLAVER.equals(this.serverMode)) {//主备sentinel模式
        	Jedis jedis = jedisSentinelPool.getResource();
        	Object result = jedis.sendCommand(new DefaultProtocolCommand(command), param);
        	jedis.close();
        	return result;
        }else {//单机模式
        	Jedis jedis = jedisPool.getResource();
        	Object result = jedis.sendCommand(new DefaultProtocolCommand(command), param);
        	jedis.close();
        	return result;
        }
    }
//    
//    public static void main(String[] args)throws Exception {
//    	JedisPoolConfig  jedisPoolConfig = new JedisPoolConfig();
//    	JedisCluster jedis = new JedisCluster(new HostAndPort("192.168.203.128", 7001),1000,1000,5,"redis",jedisPoolConfig);
//    	JSONObject jsonObject = new JSONObject();
//    	jsonObject.put("foo", "bar");
//    	Object object = jedis.sendCommand("foo6".getBytes(),new ProtocolCommand() {
//			public byte[] getRaw() {
//				return "zrange".getBytes();
//			}
//		}, "foo6".getBytes(), "0".getBytes(), "5".getBytes());
//    	logger.error("type: {} value: {} hessianValue: {}", object.getClass().getName(), new String(((List<byte[]>)object), 2)), 
//    			"");
//    	
//    	String a = "a1";
//    	logger.error("{}, {}, {}", "foo6".getBytes(), 
//    			HessianSerializer.serialize("foo6"), new String(HessianSerializer.serialize(a)));
//    }
    
    //jedis ProtocolCommand实现类
    class DefaultProtocolCommand implements ProtocolCommand {
    	private String command;//redis命令
    	
		public DefaultProtocolCommand(String command) {
			this.command = command;
		}
		
		@Override
		public byte[] getRaw() {
			return this.command.getBytes();
		}
    }
}
