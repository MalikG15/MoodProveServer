package moodprove.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import moodprove.application.MoodProveCronJob;

@EnableJpaRepositories(basePackages = "moodprove")
@ComponentScan(basePackages = "moodprove")
@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
public class Application implements CommandLineRunner {
	
	 @Autowired
	 MoodProveCronJob moodProveCronJob;

	public static void main(final String[] args) {
	   SpringApplication.run(Application.class, args);
	}
	  
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
