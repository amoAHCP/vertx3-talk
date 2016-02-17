package ch.trivadis.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by Andy Moncsek on 17.02.16.
 */
public class UsersReadFromMongo extends AbstractVerticle {
    private MongoClient mongo;

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {

        VertxOptions vOpts = new VertxOptions();
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster-> {
            if(cluster.succeeded()){
                final Vertx result = cluster.result();
                result.deployVerticle(UsersReadFromMongo.class.getName(),options, handle -> {

                });
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // Create a mongo client using all defaults (connect to localhost and default port) using the database name "demo".
        mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));

        vertx.eventBus().consumer("/api/users", getAllUsers());

        vertx.eventBus().consumer("/api/users/:id", getAllUserById());
    }





    private Handler<Message<Object>> getAllUsers() {
        return handler -> mongo.find("users", new JsonObject(), lookup -> {
            // error handling
            if (lookup.failed()) {
                handler.fail(500, "lookup failed");
                return;
            }

            // now convert the list to a JsonArray because it will be easier to encode the final object as the response.
            final JsonArray json = new JsonArray();

            for (JsonObject o : lookup.result()) {
                json.add(o);
            }

            handler.reply(json.encode());
        });
    }

    private Handler<Message<Object>> getAllUserById() {
        return handler -> {
            final Object body = handler.body();
            final String id = body.toString();
            mongo.findOne("users", new JsonObject().put("_id", id), null, lookup -> {
                // error handling
                if (lookup.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }

                JsonObject user = lookup.result();

                if (user == null) {
                    handler.fail(404, "no user found");
                } else {
                    handler.reply(user.encode());
                }
            });
        };
    }


}
