package cn.zpaas.lowcode.be.engine.registry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.utils.HttpClientUtils;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.DeployInstanceRegistry;
import cn.zpaas.lowcode.exception.EngineException;
import jakarta.servlet.http.Cookie;

/**
 * 服务部署实例注册工具类
 *
 * @author zjy
 * createTime 2025年04月16日-10:18:23
 */
@Service
public class ServiceRegistry {
    public static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    public static final Long connectTimeout = 5000l;

    @Value("${server.address:127.0.0.1}")
    private String address;

    @Value("${server.port:8443}")
    private Integer port;

    @Value("${server.servlet.context-path:lowcode}")
    private String contextPath;

    @Value("${server.ssl.protocol}")
    private String sslProtocol;

    @Value("${service.registry.registryTime:30000}")
    private Long registryTime;

    @Value("${service.registry.registryTimeout:60000}")
    private Long registryTimeout;

    @Autowired
    private DeployInstanceRegistry deployInstanceRegistrySV;

    public void init() {
        Thread registryThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //根据地址和端口尝试查询对象的部署实例
                        DeployInstanceRegistry deployInstanceRegistry = deployInstanceRegistrySV.loadByAddrAndPort(address, port);
                        if (deployInstanceRegistry == null) {//新增的情况
                            deployInstanceRegistry = new DeployInstanceRegistry();
                            deployInstanceRegistry.setAddress(address);
                            deployInstanceRegistry.setPort(port);
                            deployInstanceRegistry.setContextPath(contextPath);
                            deployInstanceRegistry.setIsHttps(StringUtils.isBlank(sslProtocol) ? YesOrNo.NO : YesOrNo.YES);
                            deployInstanceRegistrySV.create(deployInstanceRegistry);
                        } else {//刷新的情况
                            deployInstanceRegistry.setContextPath(contextPath);
                            deployInstanceRegistry.setIsHttps(StringUtils.isBlank(sslProtocol) ? YesOrNo.NO : YesOrNo.YES);
                            deployInstanceRegistrySV.save(deployInstanceRegistry);
                        }
                        logger.info("ServiceRegistry regist succeed!");
                    } catch (Exception e) {
                        logger.error("ServiceRegistry regist failed!", e);
                    }

                    try {
                        Thread.sleep(registryTime);
                    } catch (InterruptedException e) {
                        logger.error("ServiceRegistry thread interrupted!", e);
                    }
                }
            }
        };
        registryThread.start();
    }

    /**
     * 发送刷新缓存方法
     * @param reloadUrl
     * @param cookies
     */
    public void reloadClusterCache(String reloadUrl, Cookie[] cookies) {
        //根据失效时间，加载当前还有效的服务实例列表
        List<DeployInstanceRegistry> deployInstanceRegistries = deployInstanceRegistrySV
                .loadNotTimeout(registryTimeout);
        if (!CollectionUtils.isEmpty(deployInstanceRegistries)) {
            deployInstanceRegistries.forEach((instance) -> {//逐个实例发送请求
                //拼装刷新缓存的url
                String prefix = StringUtils.emptyString();
                if (YesOrNo.YES.equals(instance.getIsHttps())) {
                    prefix = "https://";
                } else {
                    prefix = "http://";
                }
                prefix = prefix + instance.getAddress() + ":" + instance.getPort() + instance.getContextPath();
                //发送请求
                try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(connectTimeout, true, prefix, cookies)) {
                    HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(prefix + reloadUrl)).build();
                    httpClient.sendAsync(httpRequest, BodyHandlers.ofString());
                } catch (EngineException e) {
                    logger.error("reload cache failed!", e);
                }
            });

        }

    }
}
