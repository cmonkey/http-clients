package org.excavator.boot.httpClients;

import lombok.SneakyThrows;
import org.excavator.boot.httpClients.entity.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.InputStreamReader;

@SpringBootApplication
public class BootifualApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootifualApplication.class, args);
    }

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    @SneakyThrows
    private String loadSql() {
        var resource = new ClassPathResource("/initialization.sql");
        try(var r = new InputStreamReader(resource.getInputStream())){
            return FileCopyUtils.copyToString(r);
        }
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(DataSource dataSource){
        return event -> {
            var sql = loadSql();
            var names = new String[]{"Spencer", "Violetta",
            "Madhoura", "Yuxin", "Stephane", "Dr, Syer"};

            var template = new JdbcTemplate(dataSource);
            template.execute(sql);
            for(var name : names){
                template.update("insert into Customer(name) values(?)", name);
            }
            var results = template.query("select * from customer",
                    (resultSet, i)-> new Customer(resultSet.getInt("id"), resultSet.getString("name")) );
            results.forEach(System.out::println);
        };
    }
}
