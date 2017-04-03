package org.bol.game.portal.mancala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.bol.game.portal")
public class Mancala {

    public static void main(String[] args) {
        SpringApplication.run(Mancala.class, args);
    }
}
