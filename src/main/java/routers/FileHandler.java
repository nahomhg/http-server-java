package routers;

import http.CustomHttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import service.FileService;
import service.FileWriterService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler implements RouteHandler {

    private final String directory;
    private final FileService fileService;
    private static final Logger LOGGER = Logger.getLogger(FileHandler.class.getName());


    public FileHandler(String directory) {
        if(directory==null) throw new IllegalArgumentException("Directory cannot be null");
        this.directory = directory;
        fileService = new FileService(directory);
    }

    @Override
    public boolean matchesHandler(CustomHttpRequest request) {
        return request.method().equals("GET") && request.path().startsWith("/files/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request)  {
        try {
            String fileName = request.path().substring(7);
            if (fileService.doesFileExist(this.directory, fileName)) {
                byte[] fileContent = fileService.getFileContent(this.directory + fileName);
                return new HttpResponse.HttpResponseBuilder()
                        .setHttpStatus(HttpStatus.OK)
                        .addHeader("Content-Type","application/octet-stream")
                        .addHeader("Content-Length",String.valueOf(fileContent.length))
                        .addBody(fileContent)
                        .build();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
        LOGGER.log(Level.INFO,"INFO: 404 Returned to client, File Not Found.");
        return new HttpResponse.HttpResponseBuilder().setHttpStatus(HttpStatus.NOT_FOUND).build();
    }

}
