package com.authorizationServer.authorizationServer.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminDataController {

    @GetMapping("/api/admin/data")
    public Map<String, String> adminData() {
        return Map.of("message", "admin-data");
    }
}
