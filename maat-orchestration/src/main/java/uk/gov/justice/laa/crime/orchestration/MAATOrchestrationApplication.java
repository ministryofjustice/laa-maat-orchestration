package uk.gov.justice.laa.crime.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MAATOrchestrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MAATOrchestrationApplication.class, args);
	}

}
