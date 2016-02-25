package ch.trivadis.configuration;

/**
 * Created by amo on 10.10.14.
 */

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import javax.inject.Inject;
import java.util.Optional;

@Configuration
@Profile("default")
public class MongoDataSourceConfiguration {

    @Inject
    Environment environment;

    @Bean(name="local")
    public MongoDbFactory mongoDbFactoryLocal() throws Exception {
        String address = Optional.ofNullable(System.getenv("MONGODB_PORT_27017_TCP_ADDR")).orElse("localhost");
        String dbname = address.equals("localhost")?"demo":"vxmsdemo";
        System.out.println("mongo connection: "+address+"  dbname:"+dbname);
        MongoClient mongo = new MongoClient(address, 27017);
        return new SimpleMongoDbFactory(mongo, dbname);
    }
}
