package com.scankart.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan
public class ScanKartApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScanKartApplication.class, args);
	}

	   @Bean
	   public RestTemplate getRestTemplate() {
	      return new RestTemplate();
	   }

}
