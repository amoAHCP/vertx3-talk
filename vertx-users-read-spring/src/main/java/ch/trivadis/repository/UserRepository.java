package ch.trivadis.repository;

import ch.trivadis.configuration.MongoRepositoryConfiguration;
import ch.trivadis.entities.Users;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andy Moncsek on 18.02.16.
 */
@Repository
@Import({MongoRepositoryConfiguration.class})
@Qualifier("UserCommentRepository")
public class UserRepository {

    static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Inject
    private MongoTemplate mongoTemplate;

    private final Class<Users> entityClass = Users.class;


    public Collection<Users> getAllUsers() {
        try {
            Collection<Users> results = mongoTemplate.findAll(entityClass);

            logger.info("Total amount of persons: {"+ results.size()+"}");
            logger.info("Results: {" + results+" }");
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Users> findUserById(String id) {
        return mongoTemplate.find(new Query(Criteria.where("_id").regex(id)), entityClass);
    }
}
