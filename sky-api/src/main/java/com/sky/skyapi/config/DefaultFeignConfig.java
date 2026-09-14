package com.sky.skyapi.config;

import com.sky.context.BaseContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 注意：不要加 @Configuration，避免被业务服务组件扫描成全局 Bean。
 * 通过 @EnableFeignClients(defaultConfiguration = ...) 或 @FeignClient(configuration = ...) 引用。
 */
public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // 联调可改 FULL
    }

    @Bean
    public RequestInterceptor userInfoInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs == null) {
                    return; // 定时任务、异步等没有入站请求
                }
                HttpServletRequest request = attrs.getRequest();
                String userInfo = request.getHeader("user-info");
                String userType = request.getHeader("user-type");
                if (userInfo != null && !userInfo.isBlank()) {
                    template.header("user-info", userInfo);
                }
                if (userType != null && !userType.isBlank()) {
                    template.header("user-type", userType);
                }
                if (userInfo == null || userInfo.isBlank()) {
                    Long id = BaseContext.getCurrentId();
                    if (id != null) {
                        template.header("user-info", String.valueOf(id));
                    }
                }
            }
        };
    }
}