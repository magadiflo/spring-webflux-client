package com.magadiflo.client.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringWebfluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxClientApplication.class, args);
	}

}
