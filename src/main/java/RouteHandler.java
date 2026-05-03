public interface RouteHandler {
    boolean matchesHandler(CustomHttpRequest request);
    HttpResponse handle(CustomHttpRequest request);
}
