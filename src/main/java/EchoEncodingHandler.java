import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class EchoEncodingHandler implements RouteHandler{


    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.startsWith("/echo/") && endpoint.substring(6).matches("[a-zA-Z]+");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        String encoding = request.headers().get("Accept-Encoding");
        String endpoint = request.path().substring(6);
        try(ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            GZIPOutputStream gzip = new GZIPOutputStream(output);
            byte[] buffer = endpoint.getBytes();
            gzip.write(buffer,0,buffer.length);
            gzip.close();
            return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Encoding",encoding)
                .addHeader("Content-Type","text")
                .addHeader("Content-Length",String.valueOf(buffer.length))
                    .addBody(buffer)
                    .build();
        }catch (IOException exception) {
            System.err.println("EXCEPTION: "+exception.getMessage());
        }
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .build();
    }
}
