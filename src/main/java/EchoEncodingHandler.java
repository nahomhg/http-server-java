import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class EchoEncodingHandler implements RouteHandler{


    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.startsWith("/echo/") && endpoint.substring(6).matches("[a-zA-Z]+");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
//        String encoding = request.headers().get("Accept-Encoding");
//        //GZIPOutputStream
//        GZIPInputStream
//        return new HttpResponse.HttpResponseBuilder()
//                .setHttpStatus(HttpStatus.OK)
//                .addHeader("Content-Encoding",encoding)
//                .addHeader("Content-Type","text")
//                .addHeader("Content-Length",)
//
//        """
//
//                Content-Encoding: gzip
//                Content-Type: text/plain
//                Content-Length: 23"""
        return null;
    }
}
