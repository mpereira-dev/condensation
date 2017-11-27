package lazy.dev.condensation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.json.JsonWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

    private static Logger logger = LoggerFactory.getLogger(Condensation.class);

    @Autowired
    MongoDatabase mongoDatabase;

    @Autowired
    Generator generator;

    @Override
    public void run(String... args) throws Exception {
        if(mongoDatabase==null){
            logger.error("There was an issue connecting to the MongoDatabase!");
        } else {
            logger.info("DB looks good");

            // TODO add null handling for collection (ex bad name)
            MongoCollection mongoCollection = mongoDatabase.getCollection("someCollection");

//          If you want to generate a schema with only a subset of documents, you can specify a query here.
//          Bson filter = Filters.eq("someField","someValue");

            String jsonSchema = generator
                    .forCollection(mongoCollection)
//                    .withQuery(filter)                            // And uncomment here to apply the query.
                    .generateSchema()
                    .toJson(new JsonWriterSettings(true));

            logger.info("\n"+jsonSchema);
        }
    }

	public static void main(String[] args) {
		SpringApplication.run(Condensation.class, args);
    }

}