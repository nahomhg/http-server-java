package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import http.RequestParser;
import service.FileWriterService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostHandler implements RouteHandler {

    private String directory;
    private static final Logger LOGGER = Logger.getLogger(PostHandler.class.getName());

    public PostHandler(String directory) {
        this.directory = directory;
    }

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.method().equals("POST") && request.path().startsWith("/files/");
    }
    @Override
    public HttpResponse handle(CustomHttpRequest request) {
        // TODO
        String fileName = request.path().substring(7);
        File outputFile = new File(this.directory + fileName);
        try (FileOutputStream file = new FileOutputStream(outputFile)) {
            file.write(request.body());
            return new HttpResponse.HttpResponseBuilder()
                    .setHttpStatus(HttpStatus.CREATED)
                    .build();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "ERROR: " + e.getMessage());
        }
        return new HttpResponse.HttpResponseBuilder()
                .setHttpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }
}
