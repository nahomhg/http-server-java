package routers.config;

public interface Router {
    void addRoute(String method, String path, RouteHandler routeHandler);
    RouteHandler match(String method, String route);
}
