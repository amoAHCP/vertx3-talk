package ch.trivadis.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Andy Moncsek on 17.02.16.
 */
public class Server extends AbstractVerticle {
    private MongoClient mongo;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        initMongoData();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());
        final Integer port = Integer.valueOf(Optional.ofNullable(System.getenv("httpPort")).orElse("8080"));
        final String host = Optional.ofNullable(System.getProperty("http.address")).orElse("0.0.0.0");
        vertx.createHttpServer().requestHandler(router::accept).listen(port, host);

        boolean embedded = config().getBoolean("embedded", false);
        if (embedded) {
            startEmbeddedAggregator(startFuture);
        } else {
            startFuture.complete();
        }

    }

    private void startEmbeddedAggregator(Future<Void> startFuture) {
        vertx.deployVerticle(AggregatorFallback.class.getName(), handler -> {
            if (handler.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail("failed to start aggregator: " + handler.cause().getMessage());
            }
        });
    }



    private void initMongoData() {
        // Create a mongo client using all defaults (connect to localhost and default port) using the database name "demo".
        String connectionUrl = connectionURL();
        if (connectionUrl != null) {
            String dbName = config().getString("dbname", "vxmsdemo");
            mongo = MongoClient.createShared(vertx, new JsonObject().put("connection_string", connectionUrl).put("db_name", dbName));
        } else {
            mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));
        }
        // the load function just populates some data on the storage
        loadData(mongo);
    }

    private void loadData(MongoClient db) {
        db.dropCollection("users", drop -> {
            if (drop.failed()) {
                throw new RuntimeException(drop.cause());
            }

            List<JsonObject> users = new LinkedList<>();

            users.add(new JsonObject()
                    .put("username", "pmlopes")
                    .put("firstName", "Paulo")
                    .put("lastName", "Lopes")
                    .put("address", "The Netherlands"));

            users.add(new JsonObject()
                    .put("username", "timfox")
                    .put("firstName", "Tim")
                    .put("lastName", "Fox")
                    .put("address", "The Moon"));

            for (JsonObject user : users) {
                db.insert("users", user, res -> {
                    System.out.println("inserted " + user.encode());
                });
            }
        });
    }

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        Vertx.vertx().deployVerticle(Server.class.getName(), options);
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

}
