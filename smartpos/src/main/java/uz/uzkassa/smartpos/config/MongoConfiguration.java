package uz.uzkassa.smartpos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "uz.uzkassa.smartpos.repository.mongo")
public class MongoConfiguration {
}
