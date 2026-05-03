public class EchoHandler implements RouteHandler{

    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.startsWith("/echo/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String payload = request.path().substring(6);
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text")
                .addHeader("Content-Length", String.valueOf(payload.length()))
                .addBody(payload.getBytes())
                .build();

    }

}
