package com.vtb.kafkaconsumer.controller;

import com.vtb.kafkaconsumer.service.DelayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
@Slf4j
public class DelayController {
    @Autowired
    private DelayService delayService;

    @PostMapping("/delay")
    public ResponseEntity<String> setDelay(@RequestParam long delayMs) {
        try {
            delayService.setProcessingDelay(delayMs);
            log.info("Processing delay updated to: {} ms", delayMs);
            return ResponseEntity.ok("Delay successfully set to " + delayMs + " ms");

        } catch (IllegalArgumentException e) {
            log.warn("Invalid delay value attempted: {} ms", delayMs);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/delay")
    public ResponseEntity<String> getDelay() {
        log.debug("Getting current delay");
        long currentDelay = delayService.getCurrentDelay();
        String response = String.format(
                "Current delay: %d ms",
                currentDelay
        );
        return ResponseEntity.ok(response);    }
}

