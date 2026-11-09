package com.cortis.agent.chat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AgentLocaleDirectiveTest {
    @Test void onlySupportedLanguagesAreAccepted() {
        assertTrue(CortisOllamaGateway.languageDirective("hu").contains("Hungarian"));
        assertTrue(CortisOllamaGateway.languageDirective("de-DE").contains("German"));
        assertTrue(CortisOllamaGateway.languageDirective("EN-us").contains("English"));
        assertTrue(CortisOllamaGateway.languageDirective("<script>").contains("Hungarian"));
    }
}
