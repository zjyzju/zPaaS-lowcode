package cn.zpaas.lowcode.constant;

/**
 * 服务器资源类型的常量定义
 *
 * @author zjy
 * createTime 2025年04月21日-17:44:20
 */
public class ServerResourceType {
    public static final String RESOURCE_TYPE_ES = "E"; //elasticsearch
	public static final String RESOURCE_TYPE_FTP = "F"; //FTP
	public static final String RESOURCE_TYPE_SFTP = "S"; //SFTP
	public static final String RESOURCE_TYPE_REDIS = "R"; //REDIS
	public static final String RESOURCE_TYPE_KAFKA_PRODUCER = "K"; //Kafka-producer
	public static final String RESOURCE_TYPE_KAFKA_CONSUMER = "C"; //Kafka-consumer
	public static final String RESOURCE_TYPE_WORKFLOW = "W"; //Workflow
	public static final String RESOURCE_TYPE_DATABASE = "D"; //database
	public static final String RESOURCE_TYPE_MINIO = "M"; //minio
	public static final String RESOURCE_TYPE_SMTP = "P"; //SMTP
	public static final String RESOURCE_TYPE_SMS_GATEWAY = "G"; //短信网关
	public static final String RESOURCE_TYPE_XXLJOB = "X"; //XXL-JOB
	public static final String RESOURCE_TYPE_AIGC = "A"; //Aigc

	private ServerResourceType() {
        
    }
}
