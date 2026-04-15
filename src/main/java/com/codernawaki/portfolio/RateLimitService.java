package com.codernawaki.portfolio;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);

    private final RedisClient redisClient;
    private final Map<String, Bucket> localCache = new ConcurrentHashMap<>();
    private volatile ProxyManager<String> proxyManager;
    private volatile boolean redisUnavailable;

    public RateLimitService(@Autowired(required = false) RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public Bucket resolveBucket(String key) {
        ProxyManager<String> activeProxyManager = getProxyManager();

        if (activeProxyManager == null) {
            return localCache.computeIfAbsent(key, k -> Bucket.builder()
                    .addLimit(limit -> limit.capacity(3).refillIntervally(3, Duration.ofHours(1)))
                    .build());
        }

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofHours(1))))
                .build();
        
        return activeProxyManager.builder().build(key, configSupplier);
    }

    private ProxyManager<String> getProxyManager() {
        if (proxyManager != null) {
            return proxyManager;
        }

        if (redisClient == null || redisUnavailable) {
            return null;
        }

        synchronized (this) {
            if (proxyManager != null) {
                return proxyManager;
            }

            if (redisUnavailable) {
                return null;
            }

            try {
                StatefulRedisConnection<String, byte[]> connection = redisClient.connect(
                        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
                proxyManager = LettuceBasedProxyManager.builderFor(connection)
                        .withExpirationStrategy(ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofDays(1)))
                        .build();
                return proxyManager;
            } catch (Exception exception) {
                redisUnavailable = true;
                logger.warn("Redis rate limiting unavailable, falling back to in-memory buckets", exception);
                return null;
            }
        }
    }
}
