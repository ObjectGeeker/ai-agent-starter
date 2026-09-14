package com.zephyr.ai.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域（CORS）配置。
 * <p>
 * 通过注册 {@link CorsFilter} Bean 在过滤器层统一处理跨域，先于 Spring MVC 的
 * {@code DispatcherServlet} 与拦截器（含 Sa-Token 的 {@code SaInterceptor}）执行，
 * 可正确放行 OPTIONS 预检请求，避免与鉴权拦截器产生冲突。
 * <p>
 * 当前为开发/起步阶段的宽松策略：允许任意来源、方法与请求头，并允许携带凭证。
 * 生产环境建议将 {@code allowedOriginPattern} 收敛为可信域名白名单。
 */
@Configuration
public class GlobalCorsConfiguration {

    /**
     * 注册作用于所有路径的全局 CORS 过滤器。
     *
     * @return 全局 {@link CorsFilter} 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许任意来源；使用 OriginPattern 而非 Origin，以兼容 allowCredentials=true
        config.addAllowedOriginPattern("*");
        // 允许携带凭证（Cookie、Authorization 头等）
        config.setAllowCredentials(true);
        // 允许任意请求头
        config.addAllowedHeader("*");
        // 允许任意请求方法（GET/POST/PUT/DELETE/OPTIONS 等）
        config.addAllowedMethod("*");
        // 预检请求结果缓存时间（秒），减少 OPTIONS 预检次数
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
