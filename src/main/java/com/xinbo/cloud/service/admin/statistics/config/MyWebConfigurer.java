package com.xinbo.cloud.service.admin.statistics.config;

import cn.hutool.core.util.StrUtil;
import com.xinbo.cloud.common.annotations.JwtIgnore;
import com.xinbo.cloud.common.constant.ApiStatus;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.service.admin.statistics.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 汉斯
 * @date 2020/3/32 12:17
 * @desc AOP切面验证JWT
 */
@Slf4j
@Configuration
public class MyWebConfigurer implements WebMvcConfigurer {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private JwtService jwtService;

    class WebInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


            // 忽略带JwtIgnore注解的请求, 不做后续token认证校验
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                JwtIgnore jwtIgnore = handlerMethod.getMethodAnnotation(JwtIgnore.class);
                if (jwtIgnore != null) {
                    return true;
                }
            }

            if (HttpMethod.OPTIONS.equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return true;
            }

            final String token = request.getHeader(webConfig.getHeaderKey());

            if (StrUtil.isEmpty(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // token解析
            ActionResult actionResult = jwtService.parseToken(token);

            //认证失败
            if(actionResult.getCode() != ApiStatus.SUCCESS) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            request.setAttribute("jwtUser", actionResult.getData());
            return true;

        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] ignoreUrls =  StrUtil.split(webConfig.getIgnoreUrl(),",");
        registry.addInterceptor(new WebInterceptor()).excludePathPatterns(ignoreUrls).addPathPatterns("/**");
    }
}
