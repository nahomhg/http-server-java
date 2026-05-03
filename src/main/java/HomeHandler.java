public class HomeHandler implements RouteHandler{
    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.equals("/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .build();
    }
}
