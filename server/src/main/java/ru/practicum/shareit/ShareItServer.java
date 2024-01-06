package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ShareItServer {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ShareItServer.class, args);
	}

}
