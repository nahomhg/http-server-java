public class EchoEncodingHandler implements RouteHandler{


    @Override
    public boolean matchesHandler(String endpoint) {
        System.out.println("endopint "+endpoint);
        return endpoint.startsWith("/echo/") && endpoint.substring(6).matches("[a-zA-Z]");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        System.out.println("Handling with more context");
        return null;
    }
}
