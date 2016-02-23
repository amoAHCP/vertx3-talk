package ch.trivadis.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.Optional;

/**
 * Created by Andy Moncsek on 17.02.16.
 */
public class Aggregator extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*").
                allowedMethod(HttpMethod.GET).
                allowedMethod(HttpMethod.POST).
                allowedMethod(HttpMethod.PUT).
                allowedMethod(HttpMethod.DELETE).
                allowedHeader("Content-Type").
                allowedHeader("X-Requested-With"));

        router.route().handler(BodyHandler.create());

        // define some REST API
        router.get("/").handler(handler -> handler.response().end());

        router.get("/api/users").handler(this::getUsers);

        router.get("/api/users/:id").handler(this::getUserById);

        router.post("/api/users").handler(this::postUser);

        router.put("/api/users/:id").handler(this::updateUser);

        router.delete("/api/users/:id").handler(this::deleteUser);

        final Integer port = Integer.valueOf(Optional.ofNullable(System.getenv("httpPort")).orElse("8080"));
        final String host = Optional.ofNullable(System.getProperty("http.address")).orElse("0.0.0.0");

        vertx.createHttpServer().requestHandler(router::accept).listen(port,host);
        System.out.println("started on: "+host+":"+port);
        startFuture.complete();
    }

    private void deleteUser(RoutingContext ctx) {
        vertx.eventBus().send("/api/users/:id-delete", ctx.request().getParam("id"), (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            if (responseHandler.failed()) {
                ctx.fail(500);
            } else {
                final Message<String> result = responseHandler.result();
                ctx.response().setStatusCode(204);
                ctx.response().end();
            }

        });
    }

    private void updateUser(RoutingContext ctx) {
        // update the user properties
        JsonObject update = ctx.getBodyAsJson();
        JsonObject message = new JsonObject();
        message.put("username", update.getString("username"));
        message.put("firstName", update.getString("firstName"));
        message.put("lastName", update.getString("lastName"));
        message.put("address", update.getString("address"));
        message.put("id", ctx.request().getParam("id"));

        vertx.eventBus().send("/api/users/:id-put", message, (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(ctx, responseHandler);

        });
    }


    private void postUser(RoutingContext ctx) {
        JsonObject newUser = ctx.getBodyAsJson();

        vertx.eventBus().send("/api/users-post", newUser, (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(ctx, responseHandler);

        });
    }

    private void getUserById(RoutingContext ctx) {
        vertx.eventBus().send("/api/users/:id", ctx.request().getParam("id"), (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(ctx, responseHandler);

        });
    }

    private void getUsers(RoutingContext ctx) {
        System.out.println("get users ");
        vertx.eventBus().send("/api/users", "", (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(ctx, responseHandler);

        });
    }

    private void defaultResponse(RoutingContext ctx, AsyncResult<Message<String>> responseHandler) {
        if (responseHandler.failed()) {
            ctx.fail(500);
        } else {
            final Message<String> result = responseHandler.result();
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            ctx.response().end(result.body());
        }
    }

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        VertxOptions vOpts = new VertxOptions();
        DeploymentOptions options = new DeploymentOptions().setInstances(1).setConfig(new JsonObject().put("embedded", false));
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster -> {
            if (cluster.succeeded()) {
                final Vertx result = cluster.result();
                result.deployVerticle(Aggregator.class.getName(), options, handle -> {

                });
            }
        });
    }


}
