package com.codernawaki.portfolio;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger redisBackendActive = new AtomicInteger(0);
    private final AtomicInteger inMemoryBackendActive = new AtomicInteger(1);

    public PortfolioMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        Gauge.builder("portfolio.rate.limit.backend", redisBackendActive, AtomicInteger::get)
                .tag("backend", "redis")
                .description("Current active backend for rate limiting")
                .register(meterRegistry);

        Gauge.builder("portfolio.rate.limit.backend", inMemoryBackendActive, AtomicInteger::get)
                .tag("backend", "in_memory")
                .description("Current active backend for rate limiting")
                .register(meterRegistry);
    }

    public Timer.Sample startContactSubmission() {
        return Timer.start(meterRegistry);
    }

    public void stopContactSubmission(Timer.Sample sample, String outcome) {
        sample.stop(Timer.builder("portfolio.contact.submission")
                .description("Time spent processing contact form submissions")
                .tag("outcome", outcome)
                .register(meterRegistry));
    }

    public void recordContactSubmission(String outcome) {
        counter("portfolio.contact.submissions", "outcome", outcome).increment();
    }

    public void recordAdminSubmissionUpdate(String status) {
        counter("portfolio.admin.submission.updates", "status", normalize(status)).increment();
    }

    public void recordRateLimitRequest(String outcome, String backend) {
        setRateLimitBackend(backend);
        counter("portfolio.rate.limit.requests", "outcome", outcome, "backend", backend).increment();
    }

    public void recordRateLimitBackendSwitch(String backend, String reason) {
        setRateLimitBackend(backend);
        counter("portfolio.rate.limit.backend.switches", "backend", backend, "reason", reason).increment();
    }

    private Counter counter(String name, String... tags) {
        return meterRegistry.counter(name, tags);
    }

    private void setRateLimitBackend(String backend) {
        boolean redisActive = "redis".equals(backend);
        redisBackendActive.set(redisActive ? 1 : 0);
        inMemoryBackendActive.set(redisActive ? 0 : 1);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
