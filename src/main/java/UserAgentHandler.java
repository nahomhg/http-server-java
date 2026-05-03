public class UserAgentHandler implements RouteHandler{

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.method().equals("GET") && request.path().equalsIgnoreCase("/user-agent");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String userAgent = request.headers().get("User-Agent");
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(userAgent.length()))
                .addBody(userAgent.getBytes())
                .build();
    }
}
