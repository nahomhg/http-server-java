package routers;

import http.CustomHttpRequest;
import http.HttpResponse;

public interface RouteHandler {
    boolean matchesHandler(CustomHttpRequest request);
    HttpResponse handle(CustomHttpRequest request);
}
