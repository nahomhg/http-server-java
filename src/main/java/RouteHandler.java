public interface RouteHandler {
    boolean matchesHandler(String endpoint);
    HttpResponse handle(CustomHttpRequest request);
}
