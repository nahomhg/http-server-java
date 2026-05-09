package service;

import http.RequestParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileWriterService {

    private FileOutputStream fileOutputStream;
    private static final Logger LOGGER = Logger.getLogger(FileWriterService.class.getName());

    public FileWriterService(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public boolean write(FileOutputStream fileOutputStream, byte[] data){
        try(fileOutputStream){
            fileOutputStream.write(data);
            return true;
        }catch(IOException e){
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
        return false;
    }
}
