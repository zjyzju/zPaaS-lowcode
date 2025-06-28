package cn.zpaas.lowcode.be.engine.utils;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.UploadObjectArgs;
import io.minio.http.Method;

/**
 * MinIO客户端能力封装类
 *
 * @author zjy
 * createTime 2025年04月27日-17:32:00
 */
public class MinIOClient {
    public static Logger logger = LoggerFactory.getLogger(MinIOClient.class);
	
	 //minio参数
    private String endPoint;
    private String accessKey;
    private String secretKey;


    //桶占位符
    private static final String BUCKET_PARAM = "${bucket}";
    //bucket权限-只读
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    //bucket权限-只读
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    //bucket权限-读写
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";

    public MinIOClient(String endPoint, String accessKey, String secretKey) {
    	this.endPoint = endPoint;
    	this.accessKey = accessKey;
    	this.secretKey = secretKey;
    }
    
    /**
     * 获取文件url前半段
     * @param bucket 桶
     * @return 前半段
     */
    private String getObjectPrefixUrl(String bucket) {
        return String.format("%s/%s/", endPoint, bucket);
    }

    /**
     * 创建桶
     * @param bucket 桶
     */
    public void makeBucket(String bucket) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        // 判断桶是否存在
        boolean isExist = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!isExist) {
            // 新建桶
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 更新桶权限策略
     * @param bucket 桶
     * @param policy 权限
     */
    public void setBucketPolicy(String bucket, String policy) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        switch (policy) {
            case "read-only":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "write-only":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "read-write":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "none":
            default:
                break;
        }
    }

    /**
     * 上传本地文件
     * @param bucket    桶
     * @param objectKey 文件key
     * @param filePath  文件路径
     * @param contentType 文件类型
     * @return 文件url
     */
    public String uploadFile(String bucket, String objectKey, String filePath, String contentType) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        client.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectKey).filename(filePath).contentType(contentType).build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 流式上传文件
     * @param bucket      桶
     * @param objectKey   文件key
     * @param inputStream 文件输入流
     * @return 文件url
     */
    public String uploadInputStream(String bucket, String objectKey, InputStream inputStream, String contentType) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(objectKey).stream(inputStream, inputStream.available(), -1).contentType(contentType).build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 下载文件
     * @param bucket    桶
     * @param objectKey 文件key
     * @return 文件流
     */
    public InputStream download(String bucket, String objectKey) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        return client.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    /**
     * 文件复制
     * @param sourceBucket    源桶
     * @param sourceObjectKey 源文件key
     * @param bucket          桶
     * @param objectKey       文件key
     * @return 新文件url
     */
    public String copyFile(String sourceBucket, String sourceObjectKey, String bucket, String objectKey) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        CopySource source = CopySource.builder().bucket(sourceBucket).object(sourceObjectKey).build();
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(objectKey).source(source).build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 删除文件
     * @param bucket    桶
     * @param objectKey 文件key
     */
    public void deleteFile(String bucket, String objectKey) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    /**
     * 获取文件签名url
     * @param bucket    桶
     * @param objectKey 文件key
     * @param expires   签名有效时间  单位秒
     * @return 文件签名地址
     */
    public String getSignedUrl(String bucket, String objectKey, int expires) throws Exception {
        MinioClient client = MinioClient.builder().endpoint(this.endPoint).credentials(this.accessKey, this.secretKey).build();
        return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(objectKey).expiry(expires).build());
    }

/*
    public static void main(String[] args) {
        String bucket = "public";
        String objectKey = "demo/123.jpg";
        MinIOClient client = new MinIOClient("http://192.168.203.128:9000", "admin", "minio@123z");
        try {
            // 创建桶
        	client.makeBucket(bucket);
            // 设置桶读写权限
        	client. setBucketPolicy(bucket, "read-write");
            // 上传本地文件
            String url1 = client.uploadFile(bucket, objectKey, "/Users/zjy/Downloads/bridge.jpg", ContentType.IMAGE_JPEG.getMimeType());
            // 输出  http://127.0.0.1:9000/public/demo/123.png
            System.out.println(url1);
            // 上传文件流
            String url2 = client.uploadInputStream(bucket, objectKey, new FileInputStream(new File("/Users/zjy/Downloads/bridge.jpg")), ContentType.IMAGE_JPEG.getMimeType());
            // 输出  http://127.0.0.1:9000/public/demo/123.png
            System.out.println(url2);
            // 下载文件
            InputStream inputStream = client.download(bucket, objectKey);
            // 文件复制
            String url3 = client.copyFile(bucket, objectKey, "test", "test/abc.jpg");
            // 输出  http://127.0.0.1:9000/test/test/abc.png
            System.out.println(url3);
            // 删除文件
            client.deleteFile("test", "test/abc.jpg");
            // 获取文件签名url
            String sign = client.getSignedUrl(bucket, objectKey, 300);
            // 该路径可访问文件五分钟
            System.out.println(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
