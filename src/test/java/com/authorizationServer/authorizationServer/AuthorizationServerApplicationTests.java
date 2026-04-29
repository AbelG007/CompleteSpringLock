package com.authorizationServer.authorizationServer;

import com.authorizationServer.authorizationServer.config.AppSecurityProperties;
import com.authorizationServer.authorizationServer.config.SecurityMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationServerApplicationTests {

	@Autowired
	private AppSecurityProperties appSecurityProperties;

	@Test
	void contextLoads() {
	}

	@Test
	void appSecurityPropertiesBind() {
		assertThat(appSecurityProperties.isEnabled()).isTrue();
		assertThat(appSecurityProperties.getMode()).isEqualTo(SecurityMode.AUTHORIZATION_SERVER);
	}

}
