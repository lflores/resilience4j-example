package com.perficient.resilience4j.client.metrics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientMetrics {
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicInteger timeouts = new AtomicInteger(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxResponseTime = new AtomicLong(0);
    
    private final long startTime;

    public ClientMetrics() {
        this.startTime = System.currentTimeMillis();
    }

    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public void recordSuccess(long responseTime) {
        successfulRequests.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);
        updateMinMax(responseTime);
    }

    public void recordFailure() {
        failedRequests.incrementAndGet();
    }

    public void recordTimeout() {
        timeouts.incrementAndGet();
        recordFailure();
    }

    private void updateMinMax(long responseTime) {
        minResponseTime.updateAndGet(current -> Math.min(current, responseTime));
        maxResponseTime.updateAndGet(current -> Math.max(current, responseTime));
    }

    public void printReport() {
        long uptime = (System.currentTimeMillis() - startTime) / 1000;
        int total = totalRequests.get();
        int success = successfulRequests.get();
        int failed = failedRequests.get();
        int timeoutCount = timeouts.get();
        
        double successRate = total > 0 ? (success * 100.0) / total : 0;
        long avgResponseTime = success > 0 ? totalResponseTime.get() / success : 0;
        long min = minResponseTime.get() == Long.MAX_VALUE ? 0 : minResponseTime.get();
        long max = maxResponseTime.get();
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        System.out.println("\n📈 === METRICS REPORT [" + timestamp + "] ===");
        System.out.println("⏱️  Uptime: " + formatUptime(uptime));
        System.out.println("📊 Total Requests: " + total);
        System.out.println("✅ Successful: " + success);
        System.out.println("❌ Failed: " + failed);
        System.out.println("⏰ Timeouts: " + timeoutCount);
        System.out.println("📈 Success Rate: " + String.format("%.2f%%", successRate));
        System.out.println("⚡ Response Times: avg=" + avgResponseTime + "ms, min=" + min + "ms, max=" + max + "ms");
        if (total > 0) {
            System.out.println("🔄 Requests/min: " + String.format("%.1f", (total * 60.0) / Math.max(uptime, 1)));
        }
        System.out.println("=====================================\n");
    }

    private String formatUptime(long uptimeSeconds) {
        if (uptimeSeconds < 60) {
            return uptimeSeconds + "s";
        } else if (uptimeSeconds < 3600) {
            return (uptimeSeconds / 60) + "m " + (uptimeSeconds % 60) + "s";
        } else {
            long hours = uptimeSeconds / 3600;
            long minutes = (uptimeSeconds % 3600) / 60;
            long seconds = uptimeSeconds % 60;
            return hours + "h " + minutes + "m " + seconds + "s";
        }
    }

    public double getSuccessRate() {
        int total = totalRequests.get();
        int success = successfulRequests.get();
        return total > 0 ? (success * 100.0) / total : 0;
    }

    public long getAverageResponseTime() {
        int success = successfulRequests.get();
        return success > 0 ? totalResponseTime.get() / success : 0;
    }

    // Getters
    public int getTotalRequests() { return totalRequests.get(); }
    public int getSuccessfulRequests() { return successfulRequests.get(); }
    public int getFailedRequests() { return failedRequests.get(); }
    public int getTimeouts() { return timeouts.get(); }
}