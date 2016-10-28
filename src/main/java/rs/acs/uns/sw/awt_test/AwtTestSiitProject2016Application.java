package rs.acs.uns.sw.awt_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("rs.acs.uns.sw.awt_test")
@SpringBootApplication
public class AwtTestSiitProject2016Application {

	public static void main(String[] args) {
		SpringApplication.run(AwtTestSiitProject2016Application.class, args);
	}
}
