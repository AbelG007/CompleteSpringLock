package com.authorizationServer.dto;

import java.util.List;

public record CustomerCreateRequest(
        String name,
        String email,
        String mobileNumber,
        String password,
        String role,
        List<String> authorities
) {
}
