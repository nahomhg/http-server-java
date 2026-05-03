public class UserAgentHandler implements RouteHandler{

    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.equalsIgnoreCase("/user-agent");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String userAgent = request.headers().get("User-Agent");
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text")
                .addHeader("Content-Length",String.valueOf(userAgent.length()))
                .addBody(userAgent.getBytes())
                .build();
    }
}
