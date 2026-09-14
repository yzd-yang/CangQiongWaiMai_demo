package com.sky.interceptor;


import com.sky.context.BaseContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 校验Header的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {


    /**
     * 校验Header
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String userInfo = request.getHeader("user-info");  // 必须和网关写入的名字一致
        if (userInfo == null || userInfo.isBlank()) {
            response.setStatus(401);
            return false;
        }
        try {
            Long id = Long.valueOf(userInfo);
            BaseContext.setCurrentId(id);
            return true;
        } catch (NumberFormatException e) {
            response.setStatus(401);
            return false;
        }
    }

    /**
     * 补上清理 ThreadLocal（线程池复用会串用户）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        BaseContext.removeCurrentId();
    }

}
