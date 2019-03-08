package de.schauderhaft.reactiverelational;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ReactiveRelationalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveRelationalApplication.class, args);
	}

}


@Configuration
class BricksConfiguration extends AbstractR2dbcConfiguration {


	@Override
	public ConnectionFactory connectionFactory() {

		return new PostgresqlConnectionFactory(
				PostgresqlConnectionConfiguration.builder()
						.host("localhost")
						.username("postgres")
						.password("")
						.build());

	}
}

@Component
class Demo implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello World");
	}
}