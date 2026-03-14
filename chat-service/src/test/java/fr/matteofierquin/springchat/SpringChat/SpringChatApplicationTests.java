package fr.matteofierquin.springchat.SpringChat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.cloud.discovery.enabled=false",
    "eureka.client.enabled=false"
})
@ActiveProfiles("test")
class SpringChatApplicationTests {

    @Test
    void contextLoads() {
    }

}
