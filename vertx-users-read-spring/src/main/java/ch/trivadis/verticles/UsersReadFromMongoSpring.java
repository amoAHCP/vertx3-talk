package ch.trivadis.verticles;

import ch.trivadis.configuration.SpringConfiguration;
import ch.trivadis.entities.Users;
import ch.trivadis.repository.UserRepository;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jacpfx.vertx.spring.SpringVerticle;

import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Andy Moncsek on 17.02.16.
 */
@SpringVerticle(springConfig = SpringConfiguration.class)
public class UsersReadFromMongoSpring extends AbstractVerticle {
    @Inject
    private UserRepository repo;



    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.eventBus().consumer("/api/users", getAllUsers());

        vertx.eventBus().consumer("/api/users/:id", getAllUserById());
    }


    private Handler<Message<Object>> getAllUsers() {
        return handler -> executeBlocking(handler, () -> repo.getAllUsers());
    }


    private Handler<Message<Object>> getAllUserById() {
        return handler -> {
            final Object body = handler.body();
            final String id = body.toString();
            executeBlocking(handler, () -> repo.findUserById(id));
        };
    }


    public void executeBlocking(Message<Object> handler, Supplier<Collection<Users>> usersSupplier) {
        vertx.executeBlocking(
                blocking -> blocking.complete(convertToJsonArray(usersSupplier.get()).encode()),
                result -> handler.reply(result.result())
        );
    }

    private JsonArray convertToJsonArray(Collection<Users> allUsers) {
        JsonArray result = new JsonArray();
        for (Users u : allUsers) {
            u.setFirstName(u.getFirstName() + "-spring");
            result.add(new JsonObject(Json.encode(u)));
        }
        return result;
    }


    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {

        VertxOptions vOpts = new VertxOptions();
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster -> {
            if (cluster.succeeded()) {
                final Vertx result = cluster.result();
                result.deployVerticle("java-spring:" + UsersReadFromMongoSpring.class.getName(), options, handle -> {

                });
            }
        });
    }

}
