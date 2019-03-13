package de.schauderhaft.r2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableR2dbcRepositories
public class R2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(R2dbcApplication.class, args);
	}

}

@Component
class DbInitializer implements CommandLineRunner {

	@Autowired
	DatabaseClient client;

	@Override
	public void run(String... args) throws Exception {

		demoDatabaseClient();

	}

	private void demoDatabaseClient() {

		System.out.println("Initializing Database");

		createTable()
				.then()
				.block();

		System.out.println("Done");

	}


	private Mono<Void> createTable() {

		return client.execute()
				.sql("DROP TABLE IF EXISTS ROW; " +
						"CREATE TABLE ROW( ID SERIAL PRIMARY KEY, CONTENT VARCHAR(2000))")
				.then();
	}


}