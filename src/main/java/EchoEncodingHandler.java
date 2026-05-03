public class EchoEncodingHandler implements RouteHandler{


    @Override
    public boolean matchesHandler(String endpoint) {
        System.out.println(this.getClass().getSimpleName()+"endopint "+endpoint+"\n"+endpoint.substring(6));
        return endpoint.startsWith("/echo/") && endpoint.substring(6).matches("[a-zA-Z]");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        System.out.println("Handling with more context");
        return null;
    }
}
