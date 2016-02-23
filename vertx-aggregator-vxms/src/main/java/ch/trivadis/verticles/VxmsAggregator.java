package ch.trivadis.verticles;

import ch.trivadis.configuration.CustomEndpointConfig;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;
import org.jacpfx.common.ServiceEndpoint;
import org.jacpfx.common.exceptions.EndpointExecutionException;
import org.jacpfx.vertx.rest.annotation.EndpointConfig;
import org.jacpfx.vertx.rest.response.RestHandler;
import org.jacpfx.vertx.services.VxmsEndpoint;

import javax.ws.rs.*;

/**
 * Created by Andy Moncsek on 17.02.16.
 * -Dvertx.metrics.options.enabled=true -cluster
 */
@ServiceEndpoint(value = "", port = 9090)
@EndpointConfig(CustomEndpointConfig.class)
public class VxmsAggregator extends VxmsEndpoint {


    @Path("/api/users")
    @GET
    public void userGet(RestHandler handler) {
        vertx.eventBus().send("/api/users", "", (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(handler, responseHandler);
        });
    }



    @Path("/api/users")
    @POST
    public void userPOST(RestHandler handler) {
        JsonObject newUser = handler.request().body().toJsonObject();
        vertx.eventBus().send("/api/users-post", newUser, (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(handler, responseHandler);

        });


    }

    @Path("/api/users/:id")
    @GET
    public void userGetById(RestHandler handler) {
        vertx.eventBus().send("/api/users/:id", handler.request().param("id"), (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            defaultResponse(handler, responseHandler);
        });
    }

    @Path("/api/users/:id")
    @PUT
    public void userPutById(RestHandler handler) {
        JsonObject update = handler.request().body().toJsonObject();
        JsonObject message = new JsonObject();
        message.put("username", update.getString("username"));
        message.put("firstName", update.getString("firstName"));
        message.put("lastName", update.getString("lastName"));
        message.put("address", update.getString("address"));
        message.put("id", handler.request().param("id"));

        vertx.eventBus().send("/api/users/:id-put", message, (Handler<AsyncResult<Message<String>>>) responseHandler -> {
             defaultResponse(handler, responseHandler);

        });
    }

    @Path("/api/users/:id")
    @DELETE
    public void userDeleteById(RestHandler handler) {
        vertx.eventBus().send("/api/users/:id-delete", handler.request().param("id"), (Handler<AsyncResult<Message<String>>>) responseHandler -> {
            if (responseHandler.failed()) {
                handler.response().end(HttpResponseStatus.BAD_REQUEST);
            } else {
                handler.response().end(HttpResponseStatus.NO_CONTENT);
            }

        });
    }




    private void defaultResponse(RestHandler handler, AsyncResult<Message<String>> responseHandler) {
        if (responseHandler.failed()) {
           // handler.response().end(HttpResponseStatus.BAD_REQUEST);
            throw new EndpointExecutionException(responseHandler.cause());
        } else {
            final Message<String> result = responseHandler.result();
            handler.response().stringResponse(()->result.body()).contentType("application/json").execute();
        }
    }


    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        VertxOptions vOpts = new  VertxOptions().setMetricsOptions(new MetricsOptions().setEnabled(true));
        DeploymentOptions options = new DeploymentOptions().setInstances(1).setConfig(new JsonObject().put("embedded", false));
        vOpts.setClustered(true);
        Vertx.clusteredVertx(vOpts, cluster -> {
            if (cluster.succeeded()) {
                final Vertx result = cluster.result();
                result.deployVerticle(VxmsAggregator.class.getName(), options, handle -> {

                });
            }
        });
    }


}
