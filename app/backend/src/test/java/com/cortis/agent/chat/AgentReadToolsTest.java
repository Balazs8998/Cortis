package com.cortis.agent.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AgentReadToolsTest {
    private static Authentication user(String... permissions) {
        return UsernamePasswordAuthenticationToken.authenticated("unit-test", null,
                java.util.Arrays.stream(permissions).map(SimpleGrantedAuthority::new).toList());
    }

    @Test void noReadPermissionsMeansNoAdvertisedTools() {
        AgentReadTools tools = new AgentReadTools(null, null, null, new ObjectMapper());
        assertTrue(tools.definitions(user()).isEmpty());
        assertFalse(AgentReadTools.allowed(user(), "CORTIS_INVENTORY_READ"));
    }

    @Test void engineeringOnlyAdvertisedWithBothPermissions() {
        AgentReadTools tools = new AgentReadTools(null, null, null, new ObjectMapper());
        assertEquals(1, tools.definitions(user("CORTIS_INVENTORY_READ")).size());
        var definitions = tools.definitions(user("CORTIS_ENGINEERING_READ", "CORTIS_INVENTORY_READ"));
        assertEquals(5, definitions.size());
        assertTrue(CortisAgentService.permittedName(definitions, "machine_setup"));
        assertFalse(CortisAgentService.permittedName(definitions, "delete_inventory"));
    }

    @Test void rejectsInvalidBusinessCodesAndArguments() throws Exception {
        assertEquals("W000001", AgentReadTools.normalizedCode("w000001"));
        assertEquals("M1", AgentReadTools.normalizedCode("m1"));
        assertThrows(IllegalArgumentException.class, () -> AgentReadTools.normalizedCode("W1"));
        assertThrows(IllegalArgumentException.class, () -> AgentReadTools.normalizedCode("W000001 OR 1=1"));
        assertThrows(IllegalArgumentException.class, () -> AgentReadTools.argument(
                new ObjectMapper().readTree("{\"code\": 42}"), "code", true, 24));
        assertEquals("", AgentReadTools.argument(new ObjectMapper().readTree("{}"), "query", false, 60));
    }
}
