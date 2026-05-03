package routers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {

    private String directory;

    public FileService(String directory) {
        this.directory = directory;
    }

    public boolean doesFileExist(String directory, String fileName) {
        File file = new File(directory + fileName);
        System.out.println("file info: " + file.getPath() + "\t" + fileName);
        return file.exists();
    }

    public byte[] getFileContent(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }
}
