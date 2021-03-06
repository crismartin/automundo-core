package es.upm.miw.tfm.automundo.configuration;

import es.upm.miw.tfm.automundo.TestConfig;
import es.upm.miw.tfm.automundo.infrastructure.api.http_errors.Role;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestConfig
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testCreateToken() {
        String token = jwtService.createToken("$$$$$$$", "adm", Role.ADMIN.name());
        assertFalse(token.isEmpty());
        LogManager.getLogger(this.getClass()).info("token:" + token);
    }
}
