package com.example.dockerbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockerBeApplication {

	public static void main(String[] args) {
		System.out.println("Application starting...");
		SpringApplication.run(DockerBeApplication.class, args);
	}

}
