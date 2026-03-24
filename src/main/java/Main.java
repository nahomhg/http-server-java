import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int indexOfDirectory = Arrays.asList(args).indexOf("--directory");
        try{
            ServerSocket serverSocket = new ServerSocket(4221);

            HttpServer httpServer = indexOfDirectory != -1 ?
                    new HttpServer(serverSocket, args[indexOfDirectory + 1]) :
                    new HttpServer(serverSocket);

            httpServer.startServer();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}


