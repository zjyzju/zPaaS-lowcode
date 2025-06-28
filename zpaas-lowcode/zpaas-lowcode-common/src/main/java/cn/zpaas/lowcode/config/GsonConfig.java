package cn.zpaas.lowcode.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import cn.zpaas.lowcode.utils.JsonUtils;

/**
 * 使用Gson替换springboot的jackson
 *
 * @author zjy
 * createTime 2025年04月02日-18:46:22
 */
@Configuration
public class GsonConfig{
    @Bean
    public HttpMessageConverters gsonConvertor() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(JsonUtils.GSON);
        converters.add(gsonHttpMessageConverter);
        return new HttpMessageConverters(true, converters);
    }
}
