package routers.config;

import http.CustomHttpRequest;
import http.HttpResponse;

public interface RouteHandler {
    HttpResponse handle(CustomHttpRequest request);
}
