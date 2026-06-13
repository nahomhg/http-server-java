package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import routers.config.RouteHandler;

public class EchoHandler implements RouteHandler {

    @Override
    public HttpResponse handle(CustomHttpRequest request) {

        String payload = request.path().substring(6);
        if(request.headers().containsKey("Accept-Encoding")){
            return new HttpResponse.HttpResponseBuilder()
                    .setHttpStatus(HttpStatus.OK)
                    .addHeader("Content-Encoding",request.headers().get("Accept-Encoding"))
                    .addHeader("Content-Type","text")
                    .addHeader("Content-Length", String.valueOf(payload.length()))
                    .addBody(payload.getBytes())
                    .build();
        }
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text")
                .addHeader("Content-Length", String.valueOf(payload.length()))
                .addBody(payload.getBytes())
                .build();

    }
}
