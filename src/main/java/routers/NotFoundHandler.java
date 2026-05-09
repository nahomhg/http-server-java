package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public class NotFoundHandler implements RouteHandler {

    @Override
    public boolean matchesHandler(CustomHttpRequest endpoint) {
        return false;
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String payload = "404 - Not Found";
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(payload.getBytes().length))
                .addBody(payload.getBytes())
                .build();
    }
}
