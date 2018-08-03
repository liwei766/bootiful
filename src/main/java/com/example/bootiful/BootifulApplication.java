package com.example.bootiful;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class BootifulApplication {

	@Bean
	HealthIndicator healthIndicator() {
		return () -> Health.status("I <3 Production!").build();
	}

	@Bean
	RouterFunction<ServerResponse> routes(CustomerRepository cr) {
		return RouterFunctions.route(GET("/customers"), serverRequest -> ok().body(cr.findAll(), Customer.class));
	}

	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}
}

@Component
class DataWriter implements ApplicationRunner {
	private final CustomerRepository customerRepository;

	DataWriter(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Flux.just("Mia", "Bob", "Onia", "Tim").flatMap(name -> customerRepository.save(new Customer(null, name)))
				.subscribe(System.out::println);

	}
}

interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

}

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
class Customer {
	private String id, name;
}
