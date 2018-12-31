package com.kambaa.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = { "com.kambaa" })
public class MysqlBackupApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlBackupApplication.class, args);
	}

}

