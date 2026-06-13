package routers.config;

import routers.NotFoundHandler;

import java.util.HashMap;

public class HashMapRouter implements Router {

    private final HashMap<RouterKey, RouteHandler> handlers = new HashMap<>();

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
        return new NotFoundHandler();
    }

//    @Override
//    public RouteHandler match(String method, String route) {
//        RouterKey key = new RouterKey(method.toUpperCase(), route);
//        if(handlers.containsKey(key)){
//            return handlers.get(key);
//        }
//
//        RouterKey prefix = new RouterKey(method.toUpperCase(), "/files/");
//        if(route.startsWith("/files/") && handlers.containsKey(prefix)){
//            return handlers.get(prefix);
//        }
//       return new NotFoundHandler();
//    }
}
