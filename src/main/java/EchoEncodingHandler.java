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
        byte[] buffer = request.path().substring(6).getBytes(StandardCharsets.UTF_8);

        if(encoding.contains("gzip")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream)) {
                gzip.write(buffer);
            } catch (IOException exception) {
                System.err.println("EXCEPTION: " + exception.getMessage());
            }

            byte[] compress = byteArrayOutputStream.toByteArray();

            return new HttpResponse.HttpResponseBuilder()
                    .setHttpStatus(HttpStatus.OK)
                    .addHeader("Content-Encoding", "gzip")
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", String.valueOf(compress.length))
                    .addBody(compress)
                    .build();
        }
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Content-Length", String.valueOf(buffer.length))
                .addBody(buffer)
                .build();
//        return new HttpResponse.HttpResponseBuilder()
//                .setHttpStatus(HttpStatus.NOT_FOUND)
//                .build();
    }
}
