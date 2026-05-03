package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public class CloseConnectionHandler implements RouteHandler {

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.method().equalsIgnoreCase("GET") &&
                (request.headers().containsKey("Connection") && request.headers().get("Connection").equalsIgnoreCase(" close"));
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Connection"," close")
                .build();
    }
}
