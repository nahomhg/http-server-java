package routers.config;

import routers.NotFoundHandler;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMapRouter implements Router {

    private final HashMap<RouterKey, RouteHandler> handlers = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(HashMapRouter.class.getName());

    @Override
    public void addRoute(String method, String path, RouteHandler routeHandler) {
        handlers.put(new RouterKey(method, path), routeHandler);
    }

    @Override
    public RouteHandler match(String method, String route) {

        RouterKey key = new RouterKey(method.toUpperCase(), route);
        if(handlers.containsKey(key)){
            return handlers.get(key);
        }

        RouterKey prefix = new RouterKey(method.toUpperCase(), "/files/");
        if(route.startsWith("/files/") && handlers.containsKey(prefix)){
            return handlers.get(prefix);
        }

        RouterKey echoPrefix = new RouterKey(method.toUpperCase(),"/echo/");
        if(route.startsWith("/echo/") && handlers.containsKey(echoPrefix)){
            return handlers.get(echoPrefix);
        }
        return new NotFoundHandler();
    }

}
