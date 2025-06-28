package cn.zpaas.lowcode.be.engine.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.KeyGenerate;
import cn.zpaas.lowcode.utils.StringUtils;


/**
 * Aigc客户端能力封装类
 *
 * @author zjy
 * createTime 2025年04月27日-17:32:00
 */
public class AigcClient {
    public static Logger logger = LoggerFactory.getLogger(AigcClient.class);
	
	 //aigc客户端参数
    private String url;
    private String generateUrl;
    private String chatUrl;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    public static final String RESPONSE_KEY = "response";
    public static final String MESSAGES_KEY = "messages";
    public static final String CONTENT_PATH_KEY = "message.content";
    public static final String CONTENT_KEY = "content";
    public static final String ROLE_KEY = "role";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String PROMPT_KEY = "prompt";
    public static final String MODEL_KEY = "model";
    public static final String STREAM_KEY = "stream";
    public static final String OPTIONS_KEY = "options";

    public static final String DEFAULT_GENERATE_PATH = "/api/generate";
    public static final String DEFAULT_CHAT_PATH = "/api/chat";

    public AigcClient(String url, String generatePath, String chatPath) {
    	this.url = url;
        if(!StringUtils.isBlank(generatePath)) {
            this.generateUrl = this.url + generatePath;
        }else {
            this.generateUrl = this.url + DEFAULT_GENERATE_PATH;
        }
    	if(!StringUtils.isBlank(chatPath)) {
    	    this.chatUrl = this.url + chatPath;
        }else {
            this.chatUrl = this.url + DEFAULT_CHAT_PATH;
        }
    }

    public AigcClient(String url) {
    	this.url = url;
        this.generateUrl = this.url + DEFAULT_GENERATE_PATH;
        this.chatUrl = this.url + DEFAULT_CHAT_PATH;
    }
    
    /**
     * 调用AIGC服务生成内容，一次生成
     * @param message 消息
     * @param chatTemplate 提示词模板
     * @return 返回生成内容
     * @throws EngineException
     */
    public String generate(String message, String chatTemplate) throws EngineException{
        JsonObject chatJson = JsonUtils.toJsonObject(chatTemplate);
        chatJson.addProperty(PROMPT_KEY, message);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.generateUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    String response = JsonUtils.getString(JsonUtils.toJsonObject(line), RESPONSE_KEY);
                    sb.append(response);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            logger.error("generate by aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate by aigc server failed!", e.getMessage(), e);
        }
    }

    /**
     * 调用AIGC服务生成内容，流式生成
     * @param message 消息
     * @param chatTemplate 提示词模板
     * @param sseEmitter 服务器发送事件对象
     * @param userName 用户名
     * @param assistantName 助手名
     * @throws EngineException
     */
    public String generate(String message, String chatTemplate, SseEmitter sseEmitter, String userName, String assitantName) throws EngineException{
        JsonObject chatJson = JsonUtils.toJsonObject(chatTemplate);
        chatJson.addProperty(PROMPT_KEY, message);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.generateUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            if(!StringUtils.isBlank(userName) && !StringUtils.isBlank(assitantName)) {
                String initResMsg = "\n" + userName + "：" + message + "\n" + assitantName + "： "; 
                sseEmitter.send(SseEmitter.event().data(initResMsg).id(KeyGenerate.uuidKey()));
            }
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    String response = JsonUtils.getString(JsonUtils.toJsonObject(line), RESPONSE_KEY);
                    sseEmitter.send(SseEmitter.event().data(response).id(KeyGenerate.uuidKey()));
                    sb.append(response);
                }
                sseEmitter.send(SseEmitter.event().data("\n").id(KeyGenerate.uuidKey()));
                return sb.toString();
            }
        } catch (Exception e) {
            logger.error("generate by aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate by aigc server failed!", e.getMessage(), e);
        }
    }

    /**
     * 调用AIGC服务实现多轮对话，一次生成
     * @param message 消息
     * @param chatTemplate 提示词模板
     * @param messages 前面的多轮对话
     * @return 返回多轮对话，最后一条为最新的返回
     * @throws EngineException
     */
    public JsonArray chat(String message, String chatTemplate, JsonArray messages) throws EngineException{
        JsonObject chatJson = JsonUtils.toJsonObject(chatTemplate);
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty(ROLE_KEY, ROLE_USER);
        messageJson.addProperty(CONTENT_KEY, message);
        if(messages == null) {
            messages = new JsonArray();
        }
        messages.add(messageJson);
        chatJson.add(MESSAGES_KEY, messages);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.chatUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    String response = (String)JsonUtils.eval(JsonUtils.toJsonObject(line), CONTENT_PATH_KEY);
                    sb.append(response);
                }
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty(ROLE_KEY, ROLE_ASSISTANT);
                responseJson.addProperty(CONTENT_KEY, sb.toString());
                messages.add(responseJson);
                return messages;
            }
        } catch (Exception e) {
            logger.error("chat with aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "chat with aigc server failed!", e.getMessage(), e);
        }
    }

    /**
     * 调用AIGC服务实现多轮对话，流式生成
     * @param message 消息
     * @param chatTemplate 提示词模板
     * @param messages 前面的多轮对话
     * @param userName 用户名
     * @param assistantName 助手名
     * @return 返回多轮对话，最后一条为最新的返回
     * @throws EngineException
     */
    public JsonArray chat(String message, String chatTemplate, JsonArray messages, SseEmitter sseEmitter, String userName, String assitantName) throws EngineException{
        JsonObject chatJson = JsonUtils.toJsonObject(chatTemplate);
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty(ROLE_KEY, ROLE_USER);
        messageJson.addProperty(CONTENT_KEY, message);
        if(messages == null) {
            messages = new JsonArray();
        }
        messages.add(messageJson);
        chatJson.add(MESSAGES_KEY, messages);
        try (HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.chatUrl)).header(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(chatJson)))
                .build();
            if(!StringUtils.isBlank(userName) && !StringUtils.isBlank(assitantName)) {
                String initResMsg = "\n" + userName + "：" + message + "\n" + assitantName + "： "; 
                sseEmitter.send(SseEmitter.event().data(initResMsg).id(KeyGenerate.uuidKey()));
            }
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = br.readLine()) != null) {
                    String response = (String)JsonUtils.eval(JsonUtils.toJsonObject(line), CONTENT_PATH_KEY);
                    sseEmitter.send(SseEmitter.event().data(response).id(KeyGenerate.uuidKey()));
                    sb.append(response);
                }
                sseEmitter.send(SseEmitter.event().data("\n").id(KeyGenerate.uuidKey()));
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty(ROLE_KEY, ROLE_ASSISTANT);
                responseJson.addProperty(CONTENT_KEY, sb.toString());
                messages.add(responseJson);
                return messages;
            }
        } catch (Exception e) {
            logger.error("chat with aigc server failed!", e);
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "chat with aigc server failed!", e.getMessage(), e);
        }finally {
            sseEmitter.complete();
        }
    }

    // public static void main(String[] args) {
    //     String url = "http://localhost:11434";
    //     String generatePath = "/api/generate";
    //     String chatPath = "/api/chat";
    //     String generateReq = """
    //             {
    //                 "model": "qwen2.5:14b",
    //                 "stream": true,
    //                 "options": {
    //                     "temperature": 0.7,
    //                     "max_tokens": 100
    //                 }
    //             }
    //             """;

    //     String chatReq = """
    //             {
    //                 "model": "qwen2.5:14b",
    //                 "stream": true,
    //                 "options": {
    //                     "temperature": 0.7,
    //                     "max_tokens": 100
    //                 }
    //             }
    //             """;
    //     try{
    //         AigcClient client = new AigcClient(url, generatePath, chatPath);
    //         HttpClient httpClient = HttpClientUtils.buildeHttpClient(60000l, true);
    //         client.generate("类似ollama的大模型架构还有哪些？", generateReq);
    //         JsonArray messages = client.chat("你好！", chatReq, null);
    //         messages = client.chat("请帮我推荐一个历史文化旅游目的地，只需要一个", chatReq, messages);
    //         messages = client.chat("当地的天气怎么样", chatReq, messages);
    //         messages = client.chat("几月份去的风景最好", chatReq, messages);
    //         messages = client.chat("坐什么交通工具最方便", chatReq, messages);
    //         messages = client.chat("从北京坐火车过去要多长时间", chatReq, messages);
    //         System.out.println(JsonUtils.toJson(messages));
    //         httpClient.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
