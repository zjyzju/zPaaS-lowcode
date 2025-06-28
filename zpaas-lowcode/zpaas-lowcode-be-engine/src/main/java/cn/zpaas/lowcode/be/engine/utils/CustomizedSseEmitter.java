package cn.zpaas.lowcode.be.engine.utils;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 自定义SseEmitter类
 *
 * @author zjy
 * createTime 2025年4月11日-17:46:42
 */
public class CustomizedSseEmitter extends SseEmitter{

    public CustomizedSseEmitter(Long timeout) {
        super(timeout);
    }

    /**
     * 设置字符器
     */
    @Override
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);
        outputMessage.getHeaders().setContentType(new MediaType(MediaType.TEXT_EVENT_STREAM, StandardCharsets.UTF_8));;
    }
    
}
