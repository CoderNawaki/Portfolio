package com.codernawaki.portfolio;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final ProxyManager<String> proxyManager;
    private final Map<String, Bucket> localCache = new ConcurrentHashMap<>();

    public RateLimitService(@Autowired(required = false) ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public Bucket resolveBucket(String key) {
        if (proxyManager == null) {
            return localCache.computeIfAbsent(key, k -> Bucket.builder()
                    .addLimit(limit -> limit.capacity(3).refillIntervally(3, Duration.ofHours(1)))
                    .build());
        }

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofHours(1))))
                .build();
        
        return proxyManager.builder().build(key, configSupplier);
    }
}
