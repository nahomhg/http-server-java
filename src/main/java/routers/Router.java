package routers;

import http.CustomHttpRequest;
import http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private final List<RouteHandler> handlers = new ArrayList<>();

    public void registerHandler(RouteHandler handler) {
        this.handlers.add(handler);
    }

    public HttpResponse route(CustomHttpRequest httpRequest){
        for(RouteHandler routeHandler : handlers){
            if(routeHandler.matchesHandler(httpRequest)){
                System.out.println("Found match!"+routeHandler.getClass().getSimpleName());
                return routeHandler.handle(httpRequest);
            }
        }
        return new NotFoundHandler().handle(httpRequest);

    }
}
