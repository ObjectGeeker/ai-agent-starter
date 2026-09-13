package com.zephyr.ai.app.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConstantDelay;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redisson 双库配置。
 *
 * <p>默认不创建 Redis 客户端，启用 {@code app.redis.enabled} 后创建分别连接 0、1 号库的客户端。</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisConfigurationProperties.class)
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class RedissonConfiguration {

    private final RedisConfigurationProperties properties;

    public RedissonConfiguration(RedisConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "redissonClientDb0", destroyMethod = "shutdown")
    public RedissonClient redissonClientDb0() {
        return Redisson.create(buildConfig(properties.getDatabase0()));
    }

    @Bean(name = "redissonClientDb1", destroyMethod = "shutdown")
    public RedissonClient redissonClientDb1() {
        return Redisson.create(buildConfig(properties.getDatabase1()));
    }

    Config buildConfig(int database) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(buildRedisAddress())
                .setDatabase(database)
                .setRetryAttempts(properties.getMaxRetry())
                .setTimeout(toMillis(properties.getTimeout(), "timeout"))
                .setConnectTimeout(toMillis(properties.getConnectTimeout(), "connectTimeout"))
                .setIdleConnectionTimeout(toMillis(properties.getIdleConnectionTimeout(), "idleConnectionTimeout"))
                .setConnectionPoolSize(properties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(properties.getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setPingConnectionInterval(toMillis(properties.getPingConnectionInterval(), "pingConnectionInterval"))
                .setRetryDelay(new ConstantDelay(properties.getRetryInterval()));

        if (StringUtils.hasText(properties.getUsername())) {
            config.setUsername(properties.getUsername().trim());
        }
        if (StringUtils.hasText(properties.getPassword())) {
            config.setPassword(properties.getPassword());
        }
        if (StringUtils.hasText(properties.getClientName())) {
            singleServerConfig.setClientName(properties.getClientName().trim());
        }
        config.setTcpKeepAlive(properties.isKeepAlive());
        config.setTcpNoDelay(properties.isTcpNoDelay());
        return config;
    }

    String buildRedisAddress() {
        if (!StringUtils.hasText(properties.getHost())) {
            throw new IllegalArgumentException("Redis host must not be blank");
        }
        String host = properties.getHost().trim();
        if (properties.getPort() < 1 || properties.getPort() > 65535) {
            throw new IllegalArgumentException("Redis port must be between 1 and 65535");
        }
        if (host.startsWith("[") && host.endsWith("]")) {
            return "redis://" + host + ":" + properties.getPort();
        }
        if (host.indexOf(':') >= 0) {
            return "redis://[" + host + "]:" + properties.getPort();
        }
        return "redis://" + host + ":" + properties.getPort();
    }

    private static int toMillis(Duration duration, String propertyName) {
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException(propertyName + " must not be negative");
        }
        long millis = duration.toMillis();
        if (millis > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(propertyName + " is too large");
        }
        return Math.toIntExact(millis);
    }
}
