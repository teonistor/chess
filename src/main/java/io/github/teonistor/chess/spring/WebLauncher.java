package io.github.teonistor.chess.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebLauncher {
    public static void main(final String[] arg) {
        final SpringApplication app = new SpringApplication(WebLauncher.class);
        app.run(arg);
    }
}
