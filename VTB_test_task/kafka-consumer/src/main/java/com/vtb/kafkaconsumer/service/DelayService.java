package com.vtb.kafkaconsumer.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class DelayService {
    private final AtomicLong processingDelay = new AtomicLong(1000);

    public long getCurrentDelay() {
        return processingDelay.get();
    }

    public void setProcessingDelay(long delayMs) {
        if (delayMs < 0 || delayMs > 30000) {
            throw new IllegalArgumentException("Delay must be between 0 and 30000 ms");
        }
        processingDelay.set(delayMs);
    }
}
