public class EchoHandler implements RouteHandler{

    @Override
    public boolean matchesHandler(String endpoint) {
        System.out.println(this.getClass().getSimpleName()+" endopint "+endpoint);
        return endpoint.matches("/echo/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String payload = request.path().substring(6);
        System.out.println("Executing on class "+this.getClass().getSimpleName());
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text")
                .addHeader("Content-Length", String.valueOf(payload.length()))
                .addBody(payload.getBytes())
                .build();

    }

}
