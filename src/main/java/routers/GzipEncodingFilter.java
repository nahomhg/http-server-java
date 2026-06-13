package routers;

import Filter.RequestFilter;
import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class GzipEncodingFilter implements RequestFilter {

    private static final Logger LOGGER = Logger.getLogger(GzipEncodingFilter.class.getName());

    @Override
    public HttpResponse doFilter(HttpResponse response) {
        String encoding = response.getHeaders().get("Accept-Encoding");
        byte[] buffer = response.getBody();

        if(encoding != null && encoding.contains("gzip")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream)) {
                gzip.write(buffer);
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "EXCEPTION: " + exception.getMessage());
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
        return response;
    }
}
