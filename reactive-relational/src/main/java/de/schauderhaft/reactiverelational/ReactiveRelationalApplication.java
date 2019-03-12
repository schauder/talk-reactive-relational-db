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

	@Autowired
	DatabaseClient client;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello World");

		client.execute()
				.sql("DROP TABLE IF EXISTS BRICK; " +
						"CREATE TABLE BRICK( ID SERIAL PRIMARY KEY, NAME VARCHAR(200), HEIGHT INTEGER, WIDTH INTEGER, LENGTH INTEGER)")
				.then()
				.doAfterTerminate(() -> System.out.println("create"))
				.block();

		System.out.println("done");

		client.insert()
				.into("BRICK")
				.value("id", 1)
				.value("name", "normal")
				.value("height", 3)
				.value("width", 2)
				.value("length", 4)
				.then()
				.doAfterTerminate(() -> System.out.println("insert"))
				.block();

		System.out.println("done");


		client.execute()
				.sql("insert into brick (id, name, height, width, length) values (:id, :name, :height, :width, :length)")
				.bind("id", 2)
				.bind("name", "small")
				.bind("height", 3)
				.bind("width", 2)
				.bind("length", 2)
				.then()
				.doAfterTerminate(() -> System.out.println("manual insert"))
				.block();

		client.select()
				.from("BRICK")
				.as(Brick.class)
				.fetch()
				.all()
				.doOnNext(System.out::println)
				.blockLast();

		client.execute()
				.sql("select name || ' (' || width || 'x' || length || ')' as value, height from brick where id = :id")
				.bind("id", 2)
				.fetch().all()
				.map(m -> String.format("Brick: %s (height: %s)", m.get("value") , m.get("height")))
				.doOnNext(System.out::println)
				.blockLast();



	}
}