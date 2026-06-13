package http;

import routers.config.HashMapRouter;
import routers.config.RouteHandler;
import routers.config.Router;
import routers.config.RouterKey;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final ExecutorService service;
    private final Router httpRouter;
    private volatile boolean isRunning = true;
    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    public HttpServer(ServerSocket serverSocket, Router router) throws SocketException {
        this.serverSocket = serverSocket;
        this.service = Executors.newFixedThreadPool(10);
        this.serverSocket.setReuseAddress(true);
        this.httpRouter = router;
    }

    public void startServer() {
        try {
//            Thread terminatingThread = new Thread(() -> {
//                try {
//                    LOGGER.log(Level.SEVERE, "Shutting down server");
//                    System.out.println("SHUTTING DOWN");
//                        serverSocket.close();
//                        service.awaitTermination(5, TimeUnit.SECONDS);
//                    isRunning = false;
//                }catch (SocketException socket){
//                    System.out.println("Shut down ");
//                }
//                catch (IOException io){
//                        LOGGER.log(Level.SEVERE, "ERROR: "+io.getMessage());
//                    System.out.println("Shut down ");
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//            });

            while (isRunning) {
                Socket socket = this.serverSocket.accept();
                service.execute(() -> {
                    handleRequest(socket);
                });
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
            System.err.println("Hello, here's exception \n"+e.getMessage());
        }
    }

    private void handleRequest(Socket socket) {
        try (socket) {
            InputStream inputStream = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            while (true) {
                Optional<CustomHttpRequest> httpRequest = RequestParser.parser(inputStream);
                if (httpRequest.isEmpty()) {
                    break;
                }
                CustomHttpRequest request = httpRequest.get();
                socket.setSoTimeout(10000);
                //
                RouteHandler requestRouteHandler = httpRouter.match(request.method(),request.path());

                HttpResponse response = requestRouteHandler.handle(request);
//                response = new GzipEncodingFilter().doFilter(response);

                String connectionHeader = request.headers().get("Connection");

                boolean shouldCloseConnection = connectionHeader != null &&  connectionHeader.equalsIgnoreCase("close");

                if(shouldCloseConnection){
                    response.getHeaders().put("Connection","close");
                }
                output.write(response.toByteArray());
                if(shouldCloseConnection) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IO Disconnection\n"+e.getMessage());
            LOGGER.log(Level.SEVERE, e.getMessage()+"\tSome bs happened");
        }
    }
}