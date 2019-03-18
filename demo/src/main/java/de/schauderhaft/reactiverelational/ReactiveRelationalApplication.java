package de.schauderhaft.reactiverelational;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableR2dbcRepositories
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

	@Autowired
	DatabaseClient client;

	@Autowired
	BrickRepository repo;

	@Override
	public void run(String... args) throws Exception {

		demoDatabaseClient();

		repo.findAll()
				.doOnNext(System.out::println)
				.blockLast();
		System.out.println("done");
	}

	private void demoDatabaseClient() {
		System.out.println("Hello World");

		createTable()
				.then(insert())
				.then(manualInsert())
				.thenMany(
						select()
								.doOnNext(System.out::println)
				).thenMany(
				manualSelect())
				.doOnNext(System.out::println)
				.blockLast();
	}


	private Mono<Void> createTable() {

		return client.execute()
				.sql("DROP TABLE IF EXISTS BRICK; " +
						"CREATE TABLE BRICK( ID INTEGER PRIMARY KEY, NAME VARCHAR(200), HEIGHT INTEGER, WIDTH INTEGER, LENGTH INTEGER)")
				.then();
	}

	private Mono<Void> insert() {

		return client.insert()
				.into("BRICK")
				.value("id", 1)
				.value("name", "normal")
				.value("height", 3)
				.value("width", 2)
				.value("length", 4)
				.then();
	}

	private Mono<Void> manualInsert() {

		return client.execute()
				.sql("insert into brick (id, name, height, width, length) values (:id, :name, :height, :width, :length)")
				.bind("id", 2)
				.bind("name", "small")
				.bind("height", 3)
				.bind("width", 2)
				.bind("length", 2)
				.then();
	}

	private Flux<Brick> select() {

		return client.select()
				.from("BRICK")
				.as(Brick.class)
				.fetch()
				.all();
	}

	private Flux<String> manualSelect() {

		return client.execute()
				.sql("select name || ' (' || width || 'x' || length || ')' as value, height from brick where id = :id")
				.bind("id", 2)
				.fetch().all()
				.map(m -> String.format("Brick: %s (height: %s)", m.get("value"), m.get("height")));
	}

}