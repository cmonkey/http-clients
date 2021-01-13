package org.excavator.boot.httpClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class HttpClientsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpClientsApplication.class, args);
	}

	@Bean
	WebClient webClient(WebClient.Builder builder){
		return builder.filter((clientRequest, exchangeFunction) -> {
			return exchangeFunction.exchange(clientRequest)
					.doOnNext(clientResponse -> {
						System.out.println("got a WebClient response: " + clientResponse);
					});
		}).build();
	}

}
