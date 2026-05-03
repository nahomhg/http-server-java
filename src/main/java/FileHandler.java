import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler implements RouteHandler{

    private final String directory;

    public FileHandler(String directory) {
        if(directory==null) throw new IllegalArgumentException("Directory cannot be null");
        this.directory = directory;
    }

    @Override
    public boolean matchesHandler(String endpoint) {
        return endpoint.startsWith("/files/");
    }

    @Override
    public HttpResponse handle(CustomHttpRequest request)  {
        try {
            String fileName = request.path().substring(7);
            if (doesFileExist(this.directory, fileName)) {
                byte[] fileContent = getFileContent(this.directory + fileName);
                System.out.println("filecontent data"+fileContent.length);
                return new HttpResponse.HttpResponseBuilder()
                        .setHttpStatus(HttpStatus.OK)
                        .addHeader("Content-Type","application/octet-stream")
                        .addHeader("Content-Length",String.valueOf(fileContent.length))
                        .build();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new HttpResponse.HttpResponseBuilder().setHttpStatus(HttpStatus.NOT_FOUND).build();

        }
    }

    private boolean doesFileExist(String directory, String fileName){
        File file = new File(directory+fileName);
        System.out.println("file info: "+file.getPath()+"\t"+fileName);
        return file.exists();
    }

    private byte[] getFileContent(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }
}
