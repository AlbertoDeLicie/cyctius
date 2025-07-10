package com.cyctius.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HealthCheck {
    @GetMapping("/check")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
