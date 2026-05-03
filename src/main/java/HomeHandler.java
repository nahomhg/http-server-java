public class HomeHandler implements RouteHandler{

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.path().equals("/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .build();
    }
}
