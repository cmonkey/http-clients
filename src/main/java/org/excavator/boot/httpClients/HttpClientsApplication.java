package org.excavator.boot.httpClients;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE;

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

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(@Value("file://${user.home}/Downloads/httpClients.zip") Path output,WebClient webClient){
		return event -> {
			// initialize a new Spring Boot project .zip archive
			var db = webClient.get()
					.uri(URI.create("https://start.spring.io/starter.zip"))
					.accept(MediaType.APPLICATION_OCTET_STREAM)
					.retrieve()
					.bodyToMono(DataBuffer.class);

			var write = DataBufferUtils.write(db, output, CREATE).thenReturn(true);

			var json = webClient.get()
					.uri(URI.create("https://spring.io/api/projects"))
					.retrieve()
					.bodyToMono(ProjectsResponse.class);
			Mono.zip(write, json).subscribe(tuple -> enumerate(tuple.getT2()));
		};
	}

	private void enumerate(ProjectsResponse pr){
	    pr._embedded.projects.stream()
				.filter(p -> p.status.equalsIgnoreCase("active"))
				.forEach(project -> System.out.println(project.toString()));
	}

}

@ToString
class ProjectsResponse{
	public Embedded _embedded = new Embedded();

	@ToString
	public static class Project{
		public String name, slug, status, repositoryUrl;
	}

	@ToString
	public static class Embedded{
		public Collection<Project> projects = new ArrayList<>();
	}

}