package routers;

import http.CustomHttpRequest;
import http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router {
    private final HashMap<String, RouteHandler> handlers = new HashMap<>();

    public void registerHandler(String handlerEndpoint, RouteHandler handler) {
        this.handlers.put(handlerEndpoint, handler);
    }

    public HttpResponse route(CustomHttpRequest httpRequest){
        if(handlers.containsKey(httpRequest.path())){
            var router = handlers.get(httpRequest.path());
            return router.handle(httpRequest);
        }
        return new NotFoundHandler().handle(httpRequest);

    }
}
