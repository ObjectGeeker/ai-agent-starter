package com.zephyr.ai.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Redis 连接配置。
 *
 * <p>两个 Redisson 客户端共用连接参数，仅使用不同的 Redis logical database。</p>
 */
@Data
@Validated
@ConfigurationProperties(prefix = "app.redis")
public class RedisConfigurationProperties {

    /** 是否启用 Redisson 客户端。 */
    private boolean enabled = false;

    /** Redis 主机地址，可以是域名、IPv4 或 IPv6 地址。 */
    @NotBlank
    private String host = "127.0.0.1";

    /** Redis 服务端口。 */
    @Min(1)
    @Max(65535)
    private int port = 6379;

    /** Redis 6 及以上版本的 ACL 用户名。 */
    private String username;

    /** Redis 密码。 */
    private String password;

    /** 第一个客户端使用的 Redis logical database。 */
    @Min(0)
    private int database0 = 0;

    /** 第二个客户端使用的 Redis logical database。 */
    @Min(0)
    private int database1 = 1;

    /** Redis 命令发送失败时的最大重试次数。 */
    @Min(0)
    private int maxRetry = 3;

    /** Redis 命令重试间隔。 */
    @NotNull
    private Duration retryInterval = Duration.ofMillis(1500);

    /** Redis 命令超时时间。 */
    @NotNull
    private Duration timeout = Duration.ofSeconds(3);

    /** 建立 Redis 连接的超时时间。 */
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(10);

    /** 空闲连接超时时间。 */
    @NotNull
    private Duration idleConnectionTimeout = Duration.ofSeconds(10);

    /** 普通 Redis 命令连接池大小。 */
    @Min(1)
    private int connectionPoolSize = 64;

    /** 普通 Redis 命令连接池最小空闲连接数。 */
    @Min(0)
    private int connectionMinimumIdleSize = 24;

    /** 发布订阅连接池大小。 */
    @Min(1)
    private int subscriptionConnectionPoolSize = 50;

    /** 发布订阅连接池最小空闲连接数。 */
    @Min(0)
    private int subscriptionConnectionMinimumIdleSize = 1;

    /** 连接健康检查间隔，设置为 0 可关闭 PING 检查。 */
    @NotNull
    private Duration pingConnectionInterval = Duration.ofSeconds(30);

    /** 是否启用 TCP keep-alive。 */
    private boolean keepAlive = true;

    /** 是否启用 TCP no-delay。 */
    private boolean tcpNoDelay = true;

    /** Redisson 客户端名称。 */
    private String clientName = "ai-agent-starter";
}
