package lazy.dev.condensation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;


/**
 *
 class org.bson.Document
 class java.lang.String
 class java.lang.Double
 class java.util.ArrayList
 class java.util.Date
 class java.lang.Boolean
 class java.lang.Integer
 null
 */
@SpringBootApplication
public class Condensation implements CommandLineRunner {

    private static Logger logger = Logger.getLogger("functionals");

    @Autowired
    MongoDatabase mongoDatabase;

    @Autowired
    Generator generator;

    @Override
    public void run(String... args) throws Exception {
        if(mongoDatabase==null){
            logger.severe("There was an issue connecting to the MongoDatabase!");
        } else {
            logger.info("DB looks good");

            // TODO add null handling for collection (ex bad name)
            MongoCollection mongoCollection = mongoDatabase.getCollection("functionals");

            String jsonSchema = generator.forCollection(mongoCollection).generateSchema().toJson( new JsonWriterSettings(true));
            logger.info("\n"+jsonSchema);
        }
    }

	public static void main(String[] args) {
		SpringApplication.run(Condensation.class, args);
    }

}