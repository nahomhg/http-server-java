import http.HttpServer;
import service.FileWriterService;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        int indexOfDirectory = Arrays.asList(args).indexOf("--directory");
        try{
            ServerSocket serverSocket = new ServerSocket(4221);

            HttpServer httpServer = indexOfDirectory != -1 ?
                    new HttpServer(serverSocket, args[indexOfDirectory + 1]) :
                    new HttpServer(serverSocket);

            httpServer.startServer();
        }catch (IOException e){
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

    }
}


