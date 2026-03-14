package fr.matteofierquin.springchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
            .simpDestMatchers("/app/message.send.**").authenticated()
            .simpDestMatchers("/app/typing.**").authenticated()
            .simpSubscribeDestMatchers("/user/**").authenticated()
            .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()
            .anyMessage().permitAll();

        return messages.build();
    }
}