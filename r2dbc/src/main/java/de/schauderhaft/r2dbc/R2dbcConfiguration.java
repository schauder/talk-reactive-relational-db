package de.schauderhaft.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.core.MethodExecutionInfo;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;


@Configuration
class R2dbcConfiguration extends AbstractR2dbcConfiguration {


	@Override
	public ConnectionFactory connectionFactory() {

		return createPostgresConnectionFactory();
//		return ProxyConnectionFactory.builder(createPostgresConnectionFactory())
//				.onBeforeMethod(getMethodLogger("b"))
//				.onAfterMethod(getMethodLogger("a"))
//				.onBeforeQuery(getLogger("b"))
//				.onAfterQuery(getLogger("a"))
//				.onEachQueryResult(getLogger("qr"))
//				.build();

	}

	private Consumer<Mono<MethodExecutionInfo>> getMethodLogger(final String prefix) {
		return m -> m.subscribe(qei -> System.out.println(String.format("m%s: \t%s \tN.A. \t %s", prefix, qei.getMethod(), qei.getExecuteDuration())));
	}

	private Consumer<Mono<QueryExecutionInfo>> getLogger(final String prefix) {
		return m -> m.subscribe(qei -> System.out.println(String.format("q%s: \t%s \t%s \t %s", prefix, qei.getMethod(), getQuery(qei), qei.getExecuteDuration())));
	}

	private String getQuery(QueryExecutionInfo qei) {

		List<QueryInfo> queries = qei.getQueries();
		if (queries.isEmpty())
			return "---";
		else
			return String.format("%s (%s)", queries.get(0).getQuery(), queries.size());
	}

	private PostgresqlConnectionFactory createPostgresConnectionFactory() {

		return new PostgresqlConnectionFactory(
				PostgresqlConnectionConfiguration.builder()
						.host("localhost")
						.username("postgres")
						.password("")
						.build());
	}

}

