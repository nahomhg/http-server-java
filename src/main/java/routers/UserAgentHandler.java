package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import routers.config.RouteHandler;

public class UserAgentHandler implements RouteHandler {

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String userAgent = request.headers().get("User-Agent");
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(userAgent.length()))
                .addBody(userAgent.getBytes())
                .build();
    }
}
