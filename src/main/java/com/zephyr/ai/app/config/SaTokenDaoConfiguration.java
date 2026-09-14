package com.zephyr.ai.app.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 持久层与 Redisson 集成配置。
 * <p>
 * 复用 {@code redissonClientDb1}（database 1）存储 token / session / 权限数据，
 * 与 db0 业务缓存隔离。仅在 {@code app.redis.enabled=true} 时生效，
 * 与 {@link RedissonConfiguration} 的装配条件保持一致。
 * <p>
 * {@link SaTokenDaoForRedisson} 默认使用 StringCodec，与业务 RedissonClient
 * 的全局 codec 隔离；依赖 Redis 6.0+ 的 {@code SET KEEPTTL} 特性。
 */
@Configuration
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class SaTokenDaoConfiguration {

    /**
     * 注册基于 Redisson 的 {@link SaTokenDao} 实现。
     *
     * @param redissonClient 指定使用 db1 的 Redisson 客户端
     * @return 基于 Redisson 的 SaTokenDao 实现
     */
    @Bean
    public SaTokenDao saTokenDao(@Qualifier("redissonClientDb1") RedissonClient redissonClient) {
        return new SaTokenDaoForRedisson(redissonClient);
    }
}
