package com.authorizationServer.client.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ClientHomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/me")
    public String me(Authentication authentication, Model model) {
        Object principal = authentication.getPrincipal();
        Map<String, Object> attributes = extractAttributes(principal);
        model.addAttribute("displayName", resolveDisplayName(attributes, authentication.getName()));
        model.addAttribute("principalType", principal.getClass().getSimpleName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("attributes", attributes);
        return "me";
    }

    private Map<String, Object> extractAttributes(Object principal) {
        if (principal instanceof OidcUser oidcUser) {
            return new LinkedHashMap<>(oidcUser.getClaims());
        }
        if (principal instanceof OAuth2User oauth2User) {
            return new LinkedHashMap<>(oauth2User.getAttributes());
        }
        return Map.of();
    }

    private String resolveDisplayName(Map<String, Object> attributes, String fallbackName) {
        Object name = attributes.get("name");
        if (name instanceof String value && !value.isBlank()) {
            return value;
        }

        Object preferredUsername = attributes.get("preferred_username");
        if (preferredUsername instanceof String value && !value.isBlank()) {
            return value;
        }

        return fallbackName;
    }
}
