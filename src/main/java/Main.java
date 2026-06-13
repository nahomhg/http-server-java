import http.HttpServer;
import routers.*;
import routers.config.HashMapRouter;
import routers.config.Router;
import routers.config.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        String directory = null;
        for(int i = 0; i < args.length-1; i++){
            if(args[i].equals("--directory")){
                directory = args[i+1];
                break;
            }
        }
        int indexOfDirectory = Arrays.asList(args).indexOf("--directory");
        try{
            ServerSocket serverSocket = new ServerSocket(4221);

            Router router = new HashMapRouter();
            router.addRoute("GET","/", new HomeHandler());
            router.addRoute("GET", "/echo/abc", new EchoHandler());
            router.addRoute("GET","/user-agent/", new UserAgentHandler());
            if(directory != null){
                router.addRoute("GET","/files/", new FileHandler(directory));
                router.addRoute("POST","/files/", new PostHandler(directory));
            }

            HttpServer httpServer = new HttpServer(serverSocket, router);

            httpServer.startServer();
        }catch (IOException e){
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

    }
}


