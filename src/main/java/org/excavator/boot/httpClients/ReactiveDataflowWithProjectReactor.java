package org.excavator.boot.httpClients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SpringBootApplication
public class ReactiveDataflowWithProjectReactor {

    CompletableFuture<String> returnCompletableFuture(int counter){
        return CompletableFuture.supplyAsync(() -> {
            var start = System.currentTimeMillis();
            try{
                Thread.sleep((long)(Math.max((Math.random() * 10), 5) * 1000));
            }catch(InterruptedException e){
            }

            var stop = System.currentTimeMillis();
            var delta = stop - start;
            return "(" + Thread.currentThread().getName() + ") Hello, #" + counter + "! (after " + delta + "ms.)";
        });

    }

    Stream<Integer> returnStream(){
        return Stream.iterate(0, integer -> integer + 1);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> begin(){
        return event -> {
            Flux<String> count = Flux.fromStream(this.returnStream())
                    .take(10)
                    .flatMap(c -> Flux.zip(Mono.just(c),
                            Mono.fromCompletionStage(this.returnCompletableFuture(c))))
                    .map(tuple -> tuple.getT2() + "#" + tuple.getT1());
            count.subscribe(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveDataflowWithProjectReactor.class, args);
    }

}
