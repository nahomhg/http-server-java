package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import http.RequestParser;
import routers.config.RouteHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NotFoundHandler implements RouteHandler {

    private static final Logger LOGGER = Logger.getLogger(NotFoundHandler.class.getName());

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String payload = "404 - Not Found";
        LOGGER.log(Level.SEVERE,"Route Handler NOT Able to Route Request");
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(payload.getBytes().length))
                .addBody(payload.getBytes())
                .build();
    }
}
