package com.authorizationServer.authorizationServer.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app-security")
public class AppSecurityProperties {

    private boolean enabled = true;

    private SecurityMode mode = SecurityMode.AUTHORIZATION_SERVER;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SecurityMode getMode() {
        return mode;
    }

    public void setMode(SecurityMode mode) {
        this.mode = mode;
    }

    public boolean isMode(SecurityMode securityMode) {
        return enabled && mode == securityMode;
    }
}
