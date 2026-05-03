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
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .build();
    }
}
