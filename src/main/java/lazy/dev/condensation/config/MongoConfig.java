package lazy.dev.condensation.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lazy.dev.condensation.Generator;
import lazy.dev.condensation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoClient mongoClient(){
        return new MongoClient(host,port);
    }
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient){
        return mongoClient.getDatabase(database);
    }

    @Bean
    public Validator validator() {
        HashMap<Validator.Setting, Boolean> settings = new HashMap<>();
            settings.put(Validator.Setting.FAIL_FAST, false);

        return new Validator().withSettings(settings);
    }

    @Bean
    public Generator generator(Validator validator){
        HashMap<Generator.Setting,Boolean> settings = new HashMap<>();
            settings.put(Generator.Setting.TRUNCATE_LISTS,false);

        return new Generator().withSettings(settings).withValidator(validator);
    }

}
