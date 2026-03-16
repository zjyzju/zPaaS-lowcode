package cn.zpaas.lowcode.be.ide.ability;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.utils.HttpClientUtils;
import cn.zpaas.lowcode.be.ide.utils.BusinessFlowInfoGenerate;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;

/**
 * AI驱动开发能力类
 *
 * @author zjy
 * createTime 2026年1月23日-17:38:40
 */
@Component
public class AiDrivenAbility {
    public static final Logger logger = LoggerFactory.getLogger(AiDrivenAbility.class);

    @Value("${embedded.ai.baseUrl}")
    private String baseUrl;
    @Value("${embedded.ai.apiKey}")
    private String apiKey;
    @Value("${embedded.ai.model}")
    private String model;
    @Value("${embedded.ai.responsePath}")
    private String responsePath;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    public static final String RESPONSE_KEY = "delta";
    public static final String STREAM_KEY = "stream";
    public static final String INPUT_KEY = "input";
    public static final String MODEL_KEY = "model";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    
    /**
     * 调用AIGC服务生成内容，一次生成
     * @param message 消息
     * @return 返回生成内容
     * @throws EngineException
     */
    public String chatWithAi(String message) throws EngineException{
        JsonObject chatJson = new JsonObject();
        chatJson.addProperty(INPUT_KEY, message);
        chatJson.addProperty(MODEL_KEY, this.model);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON).header(AUTHORIZATION, BEARER + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return JsonUtils.eval(JsonUtils.toJsonObject(sb.toString()), this.responsePath).toString();
            }
        } catch (Exception e) {
            logger.error("generate by aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate by aigc server failed!", e.getMessage(), e);
        }
    }

    /**
     * 调用AIGC服务生成内容，流式生成
     * @param message 消息
     * @sseEmitter
     * @return 返回生成内容
     * @throws EngineException
     */
    public String chatWithAiStream(String message, SseEmitter sseEmitter) throws EngineException{
        JsonObject chatJson = new JsonObject();
        chatJson.addProperty(INPUT_KEY, message);
        chatJson.addProperty(MODEL_KEY, this.model);
        chatJson.addProperty(STREAM_KEY, true);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON).header(AUTHORIZATION, BEARER + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            int lineCount = 0;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    if(!line.startsWith("data:")) {
                        continue;
                    }
                    String response = JsonUtils.getString(JsonUtils.toJsonObject(line.substring(5)), RESPONSE_KEY);
                    if(StringUtils.isBlank(response)) {
                        continue;
                    }
                    sseEmitter.send(SseEmitter.event().data(response).id(String.valueOf(lineCount++)));
                    sb.append(response);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            logger.error("generate by aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate by aigc server failed!", e.getMessage(), e);
        } finally {
            sseEmitter.complete();
        }
    }

    /**
     * 根据AI生成的流程节点，生成业务流信息
     * @param flowNodes
     * @param businessFlowId
     * @param tenantId
     * @param systemId
     * @return
     * @throws EngineException
     */
    public BusinessFlowInfo genBusinessFlow(JsonArray flowNodes, String businessFlowId, String tenantId, String systemId) throws EngineException {
        return BusinessFlowInfoGenerate.genBusinessFlowInfo(flowNodes, businessFlowId, tenantId, systemId);
    }
}
