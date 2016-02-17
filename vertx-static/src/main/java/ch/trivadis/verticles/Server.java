package ch.trivadis.verticles;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedList;
import java.util.List;

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

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

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
        mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));

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

       /* VertxOptions vOpts = new VertxOptions();
        DeploymentOptions options = new DeploymentOptions().setInstances(1).setConfig(new JsonObject().put("embedded", false));
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster -> {
            if (cluster.succeeded()) {
                final Vertx result = cluster.result();
                result.deployVerticle(Server.class.getName(), options, handle -> {

                });
            }
        });*/
    }

}
