package com.authorizationServer.dto;

import java.util.List;

public record CustomerResponse(
        long id,
        String name,
        String email,
        String mobileNumber,
        String role,
        List<String> authorities
) {
}
