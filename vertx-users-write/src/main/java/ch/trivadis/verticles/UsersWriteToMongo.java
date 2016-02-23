package ch.trivadis.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by Andy Moncsek on 17.02.16.
 */
public class UsersWriteToMongo extends AbstractVerticle {
    private MongoClient mongo;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // Create a mongo client using all defaults (connect to localhost and default port) using the database name "demo".
        String connectionUrl = connectionURL();
        if (connectionUrl != null) {
            String dbName = config().getString("dbname", "vxmsdemo");
            mongo = MongoClient.createShared(vertx, new JsonObject().put("connection_string", connectionUrl).put("db_name", dbName));
        } else {
            mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));
        }
        vertx.eventBus().consumer("/api/users-post", insertUser());

        vertx.eventBus().consumer("/api/users/:id-put", updateUser());

        vertx.eventBus().consumer("/api/users/:id-delete", deleteUser());
    }

    private Handler<Message<JsonObject>> insertUser() {
        return handler -> {
            final JsonObject newUser = handler.body();
            mongo.findOne("users", new JsonObject().put("username", newUser.getString("username")), null, lookup -> {
                // error handling
                if (lookup.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }

                JsonObject user = lookup.result();

                if (user != null) {
                    // already exists
                    handler.fail(404, "user already exists");
                } else {
                    mongo.insert("users", newUser, insert -> {
                        // error handling
                        if (insert.failed()) {
                            handler.fail(500, "lookup failed");
                            return;
                        }

                        // add the generated id to the user object
                        newUser.put("_id", insert.result());
                        handler.reply(newUser.encode());
                    });
                }
            });
        };
    }

    private Handler<Message<JsonObject>> updateUser() {
        return handler -> {
            final JsonObject body = handler.body();
            mongo.findOne("users", new JsonObject().put("_id", body.getString("id")), null, lookup -> {
                // error handling
                if (lookup.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }

                JsonObject user = lookup.result();

                if (user == null) {
                    // does not exist
                    handler.fail(404, "user does not exists");
                } else {

                    // update the user properties
                    user.put("username", body.getString("username"));
                    user.put("firstName", body.getString("firstName"));
                    user.put("lastName", body.getString("lastName"));
                    user.put("address", body.getString("address"));

                    mongo.replace("users", new JsonObject().put("_id", body.getString("id")), user, replace -> {
                        // error handling
                        if (replace.failed()) {
                            handler.fail(500, "lookup failed");
                            return;
                        }
                        handler.reply(user.encode());
                    });
                }
            });
        };
    }


    private Handler<Message<String>> deleteUser() {
        return handler -> {
            final String id = handler.body();
            mongo.findOne("users", new JsonObject().put("_id", id), null, lookup -> {
                // error handling
                if (lookup.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }

                JsonObject user = lookup.result();

                if (user == null) {
                    // does not exist
                    handler.fail(404, "user does not exists");
                } else {

                    mongo.remove("users", new JsonObject().put("_id", id), remove -> {
                        // error handling
                        if (remove.failed()) {
                            handler.fail(500, "lookup failed");
                            return;
                        }
                        handler.reply("end");
                    });
                }
            });
        };
    }

    private String connectionURL() {
        if (System.getenv("OPENSHIFT_MONGODB_DB_URL") != null) {
            return System.getenv("OPENSHIFT_MONGODB_DB_URL");
        } else if (System.getenv("MONGODB_PORT_27017_TCP_ADDR") != null) {
            String address = System.getenv("MONGODB_PORT_27017_TCP_ADDR");
            String port = System.getenv("MONGODB_PORT_27017_TCP_PORT");
            return "mongodb://" + address + ":" + port;

        }
        return null;
    }

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {

        VertxOptions vOpts = new VertxOptions();
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster -> {
            if (cluster.succeeded()) {
                final Vertx result = cluster.result();
                result.deployVerticle(UsersWriteToMongo.class.getName(), options, handle -> {

                });
            }
        });
    }

}
