package com.zephyr.ai.app.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token Web 层配置：注册拦截器以开启注解式鉴权。
 * <p>
 * 注册 {@link SaInterceptor} 并显式开启 {@code isAnnotation} 后，即可在 Controller
 * 类或方法上使用 {@code @SaCheckLogin}、{@code @SaCheckRole}、{@code @SaCheckPermission}
 * 等注解进行权限校验。此处采用无参构造（不传入全局 auth 函数），仅开启注解鉴权，
 * 不附加全局路由登录校验，避免对未标注注解的接口（如静态资源、健康检查）造成误拦截。
 * <p>
 * 拦截路径 {@code /**} 相对于 {@code server.servlet.context-path=/api} 生效，覆盖全部业务接口。
 * 注解鉴权本身不依赖 Redis，故本配置不受 {@code app.redis.enabled} 条件约束。
 */
@Configuration
public class SaTokenWebConfiguration implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 注解鉴权拦截器。
     *
     * @param registry Spring MVC 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor().isAnnotation(true))
                .addPathPatterns("/**");
    }
}
