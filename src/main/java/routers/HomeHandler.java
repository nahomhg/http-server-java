package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public class HomeHandler implements RouteHandler {

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.method().equals("GET") && request.path().equals("/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String payload = "Connection Established\nHello!";
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(payload.getBytes().length))
                .addBody(payload.getBytes())
                .build();
    }
}
