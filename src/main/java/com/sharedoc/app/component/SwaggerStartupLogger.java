package com.sharedoc.app.component;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class SwaggerStartupLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            String port = System.getProperty("server.port", "8080");
            System.out.println("Swagger UI: http://" + host + ":" + port + "/swagger-ui.html");
        } catch (UnknownHostException e) {
            System.out.println("Could not determine Swagger URL: " + e.getMessage());
        }
    }
}
