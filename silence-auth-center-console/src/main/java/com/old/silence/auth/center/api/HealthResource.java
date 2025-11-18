package com.old.silence.auth.center.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthResource {



    @GetMapping("/health")
    public String health() {
        return "health";
    }

} 