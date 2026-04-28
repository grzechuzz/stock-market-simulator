package com.example.stock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChaosController {

    @PostMapping("/chaos")
    public ResponseEntity<Void> killCurrentInstance() {
        new Thread(this::shutdown, "chaos-shutdown").start();
        return ResponseEntity.ok().build();
    }

    private void shutdown() {
        try {
            Thread.sleep(250);
            System.exit(0);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
