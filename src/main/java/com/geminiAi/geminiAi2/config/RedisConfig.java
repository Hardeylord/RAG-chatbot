package com.geminiAi.geminiAi2.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.cloud.host}")
    private String host;
    @Value("${spring.redis.cloud.port}")
    private int port;
    @Value("${spring.redis.cloud.password}")
    private String password;

    @Bean
    public JedisPooled redisConnect() {

        HostAndPort config = new HostAndPort(host, port);
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .password(password)
                .ssl(false)
                .build();

        return new JedisPooled(config, clientConfig);
    }

    @Bean
    public JedisBasedProxyManager<byte[]> bucketState(JedisPooled redisConn) {

        var expirationStrategy = ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1));
        var clientConfig = ClientSideConfig.getDefault().withExpirationAfterWriteStrategy(expirationStrategy);

        return JedisBasedProxyManager.<byte[]>builderFor(redisConn)
                .withClientSideConfig(clientConfig)
                .build();
    }
}
