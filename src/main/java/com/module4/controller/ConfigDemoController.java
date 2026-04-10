package com.module4.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigDemoController {

    @Value("${user.service.message:default}")
    private String message;

    @Value("${user.service.notification-topic:default-topic}")
    private String topic;

    @GetMapping("/config-demo")
    public String showConfig() {
        return String.format("Message: %s, Topic: %s", message, topic);
    }

    @GetMapping("/config-check")
    public String check() {
        return "Config loaded from Config Server!";
    }
}
