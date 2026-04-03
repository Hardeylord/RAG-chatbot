package com.geminiAi.geminiAi2.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class RateLimitService {

    private final JedisBasedProxyManager<byte[]> proxyManager;
    int REQUEST_PER_MINUTE=2;

    public RateLimitService(JedisBasedProxyManager<byte[]> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public Bucket rateLimit(String key) {
        byte[] bucketKey = key.getBytes(StandardCharsets.UTF_8);

       var limit = Bandwidth.builder().capacity(REQUEST_PER_MINUTE)
               .refillIntervally(REQUEST_PER_MINUTE, Duration.ofMinutes(10)).build();
       var bucketConfig = BucketConfiguration.builder().addLimit(limit).build();

        return proxyManager.builder().build(bucketKey, bucketConfig);
    }
}
